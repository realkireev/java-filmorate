package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;

public interface FriendStorage {

    void addFriend(Integer userId, User friend);

    void removeFriend(Integer userId, User friend);

    Collection<User> findFriends(Integer id);
}
