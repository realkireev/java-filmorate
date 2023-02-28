package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, Set<User>> friends = new HashMap<>();
    private static Integer currentId = 1;

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        Integer id = currentId++;
        user.setId(id);
        setNameIfNotExists(user);

        users.put(id, user);
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

    @Override
    public Collection<User> findFriends(Integer id) {
        Set<User> userFriends = friends.get(id);
        if (userFriends == null) {
            return Collections.emptyList();
        } else {
            return userFriends.stream()
                    .sorted(Comparator.comparingInt(User::getId))
                    .collect(Collectors.toList());
        }

    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        User friend = findById(friendId);
        if (friends.containsKey(userId)) {
            friends.get(userId).add(friend);
        } else {
            Set<User> userFriends = new HashSet<>();
            userFriends.add(friend);
            friends.put(userId, userFriends);
        }

        log.debug("User with id:{} got a friend with id:{}", userId, friendId);
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        User friend = findById(friendId);
        if (friends.containsKey(userId)) {
            friends.get(userId).remove(friend);
        }

        log.debug("User with id:{} lost a friend with id:{}", userId, friendId);
    }

    private void setNameIfNotExists(User user) {
        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
        }
    }
}
