package ru.yandex.practicum.filmorate.storage.user.database.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.database.FriendDao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Primary
@Slf4j
public class DatabaseFriendStorage implements FriendDao {
    private final JdbcTemplate jdbcTemplate;

    private final static String SQL_FIND_FRIENDS = "SELECT u.* FROM \"friendship\" f, \"user\" u " +
            "WHERE u.\"id\" = f.\"friend_id\" AND f.\"user_id\" = ? AND f.\"deleted_at\" IS NULL " +
            "AND f.\"confirmed_at\" IS NOT NULL";
    private final static String SQL_ADD_FRIEND = "MERGE INTO \"friendship\" (\"user_id\", \"friend_id\") VALUES (?,?)";
    private final static String SQL_CONFIRM_FRIENDSHIP = "UPDATE \"friendship\" SET \"confirmed_at\" = NOW() " +
            "WHERE \"user_id\" = ? AND \"friend_id\" = ?";
    private final static String SQL_REMOVE_FRIEND = "UPDATE \"friendship\" SET \"deleted_at\" = NOW() " +
            "WHERE \"user_id\" = ? AND \"friend_id\" = ?";

    @Autowired
    public DatabaseFriendStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> findFriends(Integer id) {
        return jdbcTemplate.query(SQL_FIND_FRIENDS, (rs, rowNum) -> getNewUser(rs), id);
    }

    @Override
    public void addFriend(Integer userId, User friend) {
        jdbcTemplate.update(SQL_ADD_FRIEND,userId, friend.getId());
    }

    @Override
    public void removeFriend(Integer userId, User friend) {
        int updateCount = jdbcTemplate.update(SQL_REMOVE_FRIEND, userId, friend.getId());
        if (updateCount == 0) {
            throw new ObjectNotFoundException("There is no friendship between users with id " + userId + " and "
                    + friend.getId());
        }
        log.debug("Friendship between users with id {} and {} has been removed", userId, friend.getId());
    }

    @Override
    public void confirmFriendship(Integer userId, Integer friendId) {
        int updateCount = jdbcTemplate.update(SQL_CONFIRM_FRIENDSHIP, userId, friendId);
        if (updateCount == 0) {
            throw new ObjectNotFoundException("There is no friendship between users with id " + userId + " and "
                    + friendId);
        }
        log.debug("Friendship between users with id {} and {} has been confirmed", userId, friendId);
    }

    private User getNewUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate());
    }
}
