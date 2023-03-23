package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.database.RatingDao;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    public final static String DEFAULT_POPULAR_COUNT = "10";
    private final FilmStorage filmStorage;
    private final RatingDao ratingStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, RatingDao ratingStorage) {
        this.filmStorage = filmStorage;
        this.ratingStorage = ratingStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(Integer id) {
        Optional<Film> result = filmStorage.findById(id);

        if (result.isEmpty()) {
            throw new ObjectNotFoundException("Film with id: " + id + " not found!");
        }

        return result.get();
    }

    public Collection<Film> findPopular(int count) {
        return ratingStorage.findPopular(count)
                .stream()
                .map(filmStorage::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        Optional<Film> result = filmStorage.update(film);
        if (result.isEmpty()) {
            throw new ObjectNotFoundException("Film with id: " + film.getId() + " not found!");
        }
        return result.get();
    }

    public void addLike(Integer filmId, Integer userId) {
        if (checkFilmExists(filmId)) {
            ratingStorage.addLike(filmId, userId);
            log.debug("Film with id:{} was liked by user with id:{}", filmId, userId);
        } else {
            throw new ObjectNotFoundException("Film with id: " + filmId + " not found!");
        }
    }

    public void removeLike(Integer filmId, Integer userId) {
        if (checkFilmExists(filmId)) {
            ratingStorage.removeLike(filmId, userId);
            log.debug("Film with id:{} was unliked by user with id:{}", filmId, userId);
        }
    }

    private boolean checkFilmExists(Integer filmId) {
        boolean exists = filmStorage.findById(filmId).isPresent();
        if (!exists) {
            log.error("Film with id:{} not found", filmId);
        }
        return exists;
    }
}
