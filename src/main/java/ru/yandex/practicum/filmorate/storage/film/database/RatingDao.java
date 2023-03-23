package ru.yandex.practicum.filmorate.storage.film.database;

import java.util.Collection;

public interface RatingDao {
    Collection<Integer> findPopular(int count);

    void addLike(Integer filmId, Integer userId);

    void removeLike(Integer filmId, Integer userId);
}
