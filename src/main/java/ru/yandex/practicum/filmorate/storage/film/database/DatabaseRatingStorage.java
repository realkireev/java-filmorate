package ru.yandex.practicum.filmorate.storage.film.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Primary
public class DatabaseRatingStorage implements RatingDao {
    private final JdbcTemplate jdbcTemplate;

    private final static String SQL_ADD_LIKE = "MERGE INTO \"film_user_like\" " +
            "(\"film_id\", \"user_id\", \"deleted_at\") VALUES (?, ?, NULL)";

    private final static String SQL_REMOVE_LIKE = "UPDATE \"film_user_like\" SET \"deleted_at\" = NOW() " +
            "WHERE \"film_id\" = ? AND \"user_id\" = ?";

    private final static String SQL_FIND_MOST_POPULAR_FILMS = "SELECT TOP ? f.*, COALESCE(l.likes, 0) \"likes\" " +
            "FROM \"film\" f LEFT JOIN (SELECT \"film_id\", COUNT(*) likes FROM \"film_user_like\" " +
            "WHERE \"deleted_at\" IS NULL GROUP BY \"film_id\") AS l ON f.\"id\" = l.\"film_id\" " +
            "WHERE f.\"deleted_at\" IS NULL ORDER BY likes DESC";

    @Autowired
    public DatabaseRatingStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        jdbcTemplate.update(SQL_ADD_LIKE, filmId, userId);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        int rowsUpdated = jdbcTemplate.update(SQL_REMOVE_LIKE, filmId, userId);
        if (rowsUpdated == 0) {
            throw new ObjectNotFoundException("Like of user with id: " + userId + " not found!");
        }
    }

    @Override
    public Collection<Integer> findPopular(int count) {
        return jdbcTemplate.query(SQL_FIND_MOST_POPULAR_FILMS, (rs, rowNum) -> obtainFilmId(rs), count);
    }

    private Integer obtainFilmId(ResultSet rs) throws SQLException {
        return rs.getInt("id");
    }
}
