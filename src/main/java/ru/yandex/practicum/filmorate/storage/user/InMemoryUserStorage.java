package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private static Integer currentId = 0;

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        currentId++;
        user.setId(currentId);
        setNameIfNotExists(user);
        users.put(currentId, user);
        log.debug("User created: {}", user);

        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new ObjectNotFoundException("User with id: " + user.getId() + " not found!");
        }
        setNameIfNotExists(user);

        users.put(user.getId(), user);
        log.debug("User updated: {}", user);

        return user;
    }

    @Override
    public User findById(Integer id) {
        if (!users.containsKey(id)) {
            throw new ObjectNotFoundException("User with id: " + id + " not found!");
        }
        return users.get(id);
    }

    private void setNameIfNotExists(User user) {
        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
        }
    }
}
