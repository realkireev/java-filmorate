package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Component
@Primary
public class DatabaseMpaStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public static final String SQL_SELECT_ALL_MPA = "SELECT * FROM \"mpa\" ORDER BY \"id\"";
    public static final String SQL_SELECT_MPA_BY_ID = "SELECT * FROM \"mpa\" WHERE \"id\" = ?";

    @Autowired
    public DatabaseMpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Mpa> findAll() {
        return jdbcTemplate.query(SQL_SELECT_ALL_MPA, (rs, rowNum) -> createMpaObject(rs));
    }

    @Override
    public Optional<Mpa> findById(Integer id) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_SELECT_MPA_BY_ID, id);

        if (rs.next()) {
            String name = rs.getString("name");
            String description = rs.getString("description");
            return Optional.of(new Mpa(id, name, description));
        }
        return Optional.empty();
    }

    private Mpa createMpaObject(ResultSet rs) throws SQLException {
        return new Mpa(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"));
    }
}
