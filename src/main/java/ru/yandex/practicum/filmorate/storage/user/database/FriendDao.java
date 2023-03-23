package ru.yandex.practicum.filmorate.storage.user.database;

import ru.yandex.practicum.filmorate.storage.user.FriendStorage;

public interface FriendDao extends FriendStorage {
    void confirmFriendship(Integer userId, Integer friendId);
}
