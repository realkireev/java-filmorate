package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Component
@Primary
public class DatabaseGenreStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public static final String SQL_SELECT_ALL_GENRES = "SELECT * FROM \"genre\" ORDER BY \"id\"";
    public static final String SQL_SELECT_GENRE_BY_ID = "SELECT * FROM \"genre\" WHERE \"id\" = ?";

    @Autowired
    public DatabaseGenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> findAll() {
        return jdbcTemplate.query(SQL_SELECT_ALL_GENRES, (rs, rowNum) -> createGenreObject(rs));
    }

    @Override
    public Optional<Genre> findById(Integer id) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_SELECT_GENRE_BY_ID, id);

        if (rs.next()) {
            String name = rs.getString("name");
            return Optional.of(new Genre(id, name));
        }
        return Optional.empty();
    }

    private Genre createGenreObject(ResultSet rs) throws SQLException {
        return new Genre(
                rs.getInt("id"),
                rs.getString("name"));
    }
}
