package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFriendStorage implements FriendStorage {
    private final Map<Integer, Set<User>> friends = new HashMap<>();

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
    public void addFriend(Integer userId, User friend) {
        if (friends.containsKey(userId)) {
            friends.get(userId).add(friend);
        } else {
            Set<User> userFriends = new HashSet<>();
            userFriends.add(friend);
            friends.put(userId, userFriends);
        }
    }

    @Override
    public void removeFriend(Integer userId, User friend) {
        if (friends.containsKey(userId)) {
            friends.get(userId).remove(friend);
        }
    }
}
