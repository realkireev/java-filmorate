package ru.yandex.practicum.filmorate.storage.film.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Primary
public class DatabaseFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    public static final String SQL_SELECT_ALL_FILMS = "SELECT * FROM \"film\" WHERE \"deleted_at\" IS NULL";
    public static final String SQL_SELECT_FILM_BY_ID = "SELECT * FROM \"film\" WHERE \"id\" = ?";
    public static final String SQL_INSERT_FILM =
            "INSERT INTO \"film\" (\"name\", \"description\", \"release_date\", \"duration\", \"mpa_id\") " +
                    "VALUES (?, ?, ?, ?, ?) ";
    public static final String SQL_GET_LAST_ID = "SELECT TOP 1 \"id\" FROM \"film\" ORDER BY \"id\" DESC";
    public static final String SQL_UPDATE_FILM =
            "UPDATE \"film\" SET \"name\" = ?, \"description\" = ?, \"release_date\" = ?, \"duration\" = ?," +
                    " \"mpa_id\" = ? WHERE \"id\" = ?";
    public static final String SQL_SELECT_GENRES_BY_FILM_ID = "SELECT * FROM \"film_genre\" WHERE \"film_id\" = ?";
    public static final String SQL_SELECT_LIKES_BY_FILM_ID = "SELECT * FROM \"film_user_like\" WHERE \"film_id\" = ? " +
            "AND \"deleted_at\" IS NULL";
    public static final String SQL_INSERT_FILM_GENRE = "MERGE INTO \"film_genre\" (\"film_id\", \"genre_id\") " +
            "VALUES (?, ?)";
    public static final String SQL_DELETE_FILM_GENRE = "DELETE FROM \"film_genre\" WHERE \"film_id\" = ?";

    @Autowired
    public DatabaseFilmStorage(JdbcTemplate jdbcTemplate, MpaStorage mpaStorage, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    @Override
    public Collection<Film> findAll() {
        return jdbcTemplate.query(SQL_SELECT_ALL_FILMS, (rs, rowNum) -> createFilmObject(rs));
    }

    @Override
    public Film create(Film film) {
        Optional<Mpa> mpaFromDatabase = getMpaFromDatabase(film);
        Collection<Integer> likes = new ArrayList<>();

        // Replace Genre_id_collection from request with Genre_objects_collection from database
        Collection<Genre> genresToSave = getGenresFromDb(film.getGenres());

        jdbcTemplate.update(SQL_INSERT_FILM, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), mpaFromDatabase.map(Mpa::getId).orElse(null));

        Integer id = jdbcTemplate.queryForObject(SQL_GET_LAST_ID, Integer.class);
        film.setId(id);

        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> jdbcTemplate.update(SQL_INSERT_FILM_GENRE, film.getId(), genre.getId()));
        }

        mpaFromDatabase.ifPresent(film::setMpa);
        film.setGenres(genresToSave);
        film.setUserIdLikes(likes);

        return film;
    }

    @Override
    public Optional<Film> update(Film film) {
        Optional<Mpa> mpaFromDatabase = getMpaFromDatabase(film);

        int updateCount = jdbcTemplate.update(SQL_UPDATE_FILM, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), mpaFromDatabase.map(Mpa::getId).orElse(null),
                film.getId());

        if (updateCount == 0) {
            return Optional.empty();
        }

        if (film.getGenres() != null) {
            jdbcTemplate.update(SQL_DELETE_FILM_GENRE, film.getId());
            film.getGenres().forEach(genre -> jdbcTemplate.update(SQL_INSERT_FILM_GENRE, film.getId(), genre.getId()));
         }

        // Replace Genre_id_collection from request with Genre_objects_collection from database
        Collection<Genre> genresToSave = findGenresByFilmId(film.getId());
        Collection<Integer> likes = findLikesByFilmId(film.getId());

        mpaFromDatabase.ifPresent(film::setMpa);
        film.setGenres(genresToSave);
        film.setUserIdLikes(likes);

        return Optional.of(film);
    }

    @Override
    public Optional<Film> findById(Integer id) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_SELECT_FILM_BY_ID, id);

        if (rs.next()) {
            Optional<Mpa> mpa = mpaStorage.findById(rs.getInt("mpa_id"));

            Film film = new Film(
                    id,
                    rs.getString("name"),
                    rs.getString("description"),
                    Objects.requireNonNull(rs.getDate("release_date")).toLocalDate(),
                    rs.getInt("duration"),
                    mpa.isEmpty() ? null : mpa.get(),
                    findGenresByFilmId(id),
                    findLikesByFilmId(id)
            );
            return Optional.of(film);
        }
        return Optional.empty();
    }

    private Film createFilmObject(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        Optional<Mpa> mpa = mpaStorage.findById(rs.getInt("mpa_id"));

        return new Film(
                id,
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                mpa.isEmpty() ? null : mpa.get(),
                findGenresByFilmId(id),
                findLikesByFilmId(id)
        );
    }

    private Collection<Genre> findGenresByFilmId(Integer filmId) {
        return jdbcTemplate.query(SQL_SELECT_GENRES_BY_FILM_ID, (rs, rowNum) -> createGenreObject(rs), filmId);
    }

    private Genre createGenreObject(ResultSet rs) throws SQLException {
        Optional<Genre> genre = genreStorage.findById(rs.getInt("genre_id"));

        if (genre.isEmpty()) {
            return null;
        } else {
            return genre.get();
        }
    }

    private Collection<Integer> findLikesByFilmId(Integer filmId) {
        return jdbcTemplate.query(SQL_SELECT_LIKES_BY_FILM_ID, (rs, rowNum) -> rs.getInt("user_id"), filmId);
    }

    private Collection<Genre> getGenresFromDb(Collection<Genre> genresPosted) {
        Collection<Genre> genresToSave = new ArrayList<>();

        if (genresPosted != null) {
            for (Genre genre : genresPosted) {
                Optional<Genre> genreFromDb = genreStorage.findById(genre.getId());
                genreFromDb.ifPresent(genresToSave::add);
            }
        }
        return genresToSave;
    }

    private Optional<Mpa> getMpaFromDatabase(Film film) {
        if (film.getMpa() != null) {
            return mpaStorage.findById(film.getMpa().getId());
        } else {
            return Optional.empty();
        }
    }
}
