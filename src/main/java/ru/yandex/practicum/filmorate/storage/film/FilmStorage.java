package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> findAll();

    Film create(Film film);

    Optional<Film> update(Film film);

    Optional<Film> findById(Integer id);
}
