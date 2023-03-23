package ru.yandex.practicum.filmorate.storage.film.inmemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.storage.film.RatingStorage;
import java.util.*;

@Component
public class InMemoryRatingStorage implements RatingStorage {
    private final Map<Integer, Set<Integer>> likes = new HashMap<>();

    @Override
    public void addLike(Integer filmId, Integer userId) {
        checkFilmExists(filmId);
        likes.get(filmId).add(userId);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        checkFilmExists(filmId);
        if (!likes.get(filmId).remove(userId)) {
            throw new ObjectNotFoundException("Like of user with id: " + userId + " not found!");
        }
    }

    @Override
    public Map<Integer, Set<Integer>> findAll() {
        return likes;
    }

    @Override
    public void createRatingContainer(Integer filmId) {
        likes.put(filmId, new HashSet<>());
    }

    private void checkFilmExists(int filmId) {
        if (!likes.containsKey(filmId)) {
            throw new ObjectNotFoundException("Film with id: " + filmId + " not found!");
        }
    }
}
