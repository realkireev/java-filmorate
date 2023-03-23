package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.database.FriendDao;
import ru.yandex.practicum.filmorate.storage.user.database.UserDao;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class UserService {
    private final UserDao userStorage;
    private final FriendDao friendStorage;

    @Autowired
    public UserService(UserDao userStorage, FriendDao friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(Integer id) {
        Optional<User> user = userStorage.findById(id);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("User with id: " + id + " not found!");
        }
        return user.get();
    }

    public Collection<User> findFriends(int userId) {
        return friendStorage.findFriends(userId);
    }

    public User createUser(User user) {
        Optional<User> createdUser = userStorage.create(user);
        if (createdUser.isPresent()) {
            log.debug("User created: {}", createdUser.get());
            return createdUser.get();
        }

        log.error("User not created: {}", user);
        return null;
    }

    public User updateUser(User user) {
        Optional<User> updatedUser = userStorage.update(user);
        if (updatedUser.isEmpty()) {
            throw new ObjectNotFoundException("User with id: " + user.getId() + " not found!");
        }

        log.debug("User updated: {}", updatedUser);
        return updatedUser.get();
    }

    public void createFriendship(Integer userId1, Integer userId2) {
        addFriend(userId1, userId2);
        friendStorage.confirmFriendship(userId1, userId2);

        addFriend(userId2, userId1);
    }

    public void removeFriendship(Integer userId1, Integer userId2) {
        removeFriend(userId1, userId2);
        removeFriend(userId2, userId1);
    }

    public Collection<User> findCommonFriends(Integer firstUserId, Integer secondUserId) {
        Collection<User> firstUserFriends = friendStorage.findFriends(firstUserId);
        Collection<User> secondUserFriends = friendStorage.findFriends(secondUserId);

        Set<User> commonFriends = new HashSet<>(firstUserFriends);
        commonFriends.retainAll(secondUserFriends);

        return commonFriends;
    }

    private void addFriend(int userId, int friendId) {
        Optional<User> friend = userStorage.findById(friendId);

        if (friend.isEmpty()) {
            throw new ObjectNotFoundException("User with id: " + friendId + " not found!");
        }

        friendStorage.addFriend(userId, friend.get());
        log.debug("User with id:{} got a friend with id:{}", userId, friendId);
    }

    private void removeFriend(int userId, int friendId) {
        Optional<User> friend = userStorage.findById(friendId);
        if (friend.isEmpty()) {
            throw new ObjectNotFoundException("User with id: " + friendId + " not found!");
        }

        friendStorage.removeFriend(userId, friend.get());
        log.debug("User with id:{} lost a friend with id:{}", userId, friendId);
    }
}
