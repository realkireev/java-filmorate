package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;

public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User update(User user);

    User findById(Integer id);

    void addFriend(Integer userId, Integer friendId);

    void removeFriend(Integer userId, Integer friendId);

    Collection<User> findFriends(Integer id);
}
