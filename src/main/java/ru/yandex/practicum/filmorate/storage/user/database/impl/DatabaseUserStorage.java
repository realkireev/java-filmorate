package ru.yandex.practicum.filmorate.storage.user.database.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.database.UserDao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Component
@Primary
public class DatabaseUserStorage implements UserDao {
    private final JdbcTemplate jdbcTemplate;

    public static final String SQL_SELECT_ALL_USERS = "SELECT * FROM \"user\" WHERE \"deleted_at\" IS NULL";
    public static final String SQL_SELECT_USER_BY_ID = "SELECT * FROM \"user\" WHERE \"id\" = ?";
    public static final String SQL_INSERT_USER =
            "INSERT INTO \"user\" (\"email\", \"login\", \"name\", \"birthday\") VALUES (?, ?, ?, ?) ";
    public static final String SQL_GET_LAST_ID = "SELECT TOP 1 \"id\" FROM \"user\" ORDER BY \"id\" DESC";
    public static final String SQL_UPDATE_USER =
            "UPDATE \"user\" SET \"email\" = ?, \"login\" = ?, \"name\" = ?, \"birthday\" = ? WHERE \"id\" = ?";

    @Autowired
    public DatabaseUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> findAll() {
        return jdbcTemplate.query(SQL_SELECT_ALL_USERS, (rs, rowNum) -> getNewUser(rs));
    }

    @Override
    public Optional<User> create(User user) {
        setNameIfNotExists(user);
        jdbcTemplate.update(SQL_INSERT_USER, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        Integer id = jdbcTemplate.queryForObject(SQL_GET_LAST_ID, Integer.class);
        user.setId(id);

        return Optional.of(user);
    }

    @Override
    public Optional<User> update(User user) {
        setNameIfNotExists(user);
        int updateCount = jdbcTemplate.update(SQL_UPDATE_USER, user.getEmail(), user.getLogin(), user.getName(),
                user.getBirthday(), user.getId());

        if (updateCount == 0) {
            return Optional.empty();
        }

        return Optional.of(user);
    }

    @Override
    public Optional<User> findById(Integer id) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_SELECT_USER_BY_ID, id);

        if (rs.next()) {
            String email = rs.getString("email");
            String login = rs.getString("login");
            String name = rs.getString("name");
            LocalDate birthday = Objects.requireNonNull(rs.getDate("birthday")).toLocalDate();

            return Optional.of(new User(id, email, login, name, birthday));
        }
        return Optional.empty();
    }

    private void setNameIfNotExists(User user) {
        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
        }
    }

    private User getNewUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()
        );
    }
}
