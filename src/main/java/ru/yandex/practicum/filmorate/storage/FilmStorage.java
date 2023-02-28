package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;

public interface FilmStorage {
    Collection<Film> findAll();

    Collection<Film> findPopular(int count);

    Film create(Film film);

    Film update(Film film);

    Film findById(Integer id);

    void addLike(Integer filmId, Integer userId);

    void removeLike(Integer filmId, Integer userId);
}
