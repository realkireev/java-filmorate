package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    @Autowired
    public UserService(UserStorage userStorage, FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(int id) {
        return userStorage.findById(id);
    }

    public Collection<User> findFriends(int userId) {
        return friendStorage.findFriends(userId);
    }

    public User createUser(User user) {
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        return userStorage.update(user);
    }

    public void createFriendship(Integer userId1, Integer userId2) {
        addFriend(userId1, userId2);
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
        User friend = userStorage.findById(friendId);
        friendStorage.addFriend(userId, friend);

        log.debug("User with id:{} got a friend with id:{}", userId, friendId);
    }

    private void removeFriend(int userId, int friendId) {
        User friend = userStorage.findById(friendId);
        friendStorage.removeFriend(userId, friend);

        log.debug("User with id:{} lost a friend with id:{}", userId, friendId);
    }
}
