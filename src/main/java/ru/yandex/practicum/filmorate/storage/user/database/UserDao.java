package ru.yandex.practicum.filmorate.storage.user.database;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.Optional;

public interface UserDao {
    Collection<User> findAll();

    Optional<User> create(User user);

    Optional<User> update(User user);

    Optional<User> findById(Integer id);
}
