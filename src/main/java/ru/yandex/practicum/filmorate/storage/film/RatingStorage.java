package ru.yandex.practicum.filmorate.storage.film;

import java.util.Map;
import java.util.Set;

public interface RatingStorage {
    Map<Integer, Set<Integer>> findAll();

    void addLike(Integer filmId, Integer userId);

    void removeLike(Integer filmId, Integer userId);

    void createRatingContainer(Integer filmId);
}
