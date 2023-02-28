package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(int id) {
        return userStorage.findById(id);
    }

    public Collection<User> findFriends(int userId) {
        return userStorage.findFriends(userId);
    }

    public User createUser(User user) {
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        return userStorage.update(user);
    }

    public void createFriendship(Integer userId1, Integer userId2) {
        userStorage.addFriend(userId1, userId2);
        userStorage.addFriend(userId2, userId1);
    }

    public void removeFriendship(Integer userId1, Integer userId2) {
        userStorage.removeFriend(userId1, userId2);
        userStorage.removeFriend(userId2, userId1);
    }

    public Collection<User> findCommonFriends(Integer firstUserId, Integer secondUserId) {
        Collection<User> firstUserFriends = userStorage.findFriends(firstUserId);
        Collection<User> secondUserFriends = userStorage.findFriends(secondUserId);

        Set<User> commonFriends = new HashSet<>(firstUserFriends);
        commonFriends.retainAll(secondUserFriends);

        return commonFriends;
    }
}
