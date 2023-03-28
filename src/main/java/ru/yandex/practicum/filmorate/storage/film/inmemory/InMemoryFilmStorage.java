package ru.yandex.practicum.filmorate.storage.film.inmemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private static Integer currentId = 0;

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        currentId++;
        film.setId(currentId);
        films.put(currentId, film);

        return film;
    }

    @Override
    public Optional<Film> update(Film film) {
        if (!films.containsKey(film.getId())) {
            return Optional.empty();
        }

        films.put(film.getId(), film);
        return Optional.of(film);
    }

    @Override
    public Optional<Film> findById(Integer id) {
        if (!films.containsKey(id)) {
            return Optional.empty();
        }

        return Optional.of(films.get(id));
    }
}
