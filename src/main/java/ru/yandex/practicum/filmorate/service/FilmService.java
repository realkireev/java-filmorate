package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.RatingStorage;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final RatingStorage ratingStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, RatingStorage ratingStorage) {
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
        return ratingStorage.findAll().entrySet()
                .stream()
                .sorted((x1, x2) -> x2.getValue().size() - x1.getValue().size())
                .limit(count)
                .map(Map.Entry::getKey)
                .map(filmStorage::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public Film create(Film film) {
        Film result = filmStorage.create(film);
        ratingStorage.createRatingContainer(result.getId());

        return result;
    }

    public Film update(Film film) {
        Optional<Film> result = filmStorage.update(film);
        if (result.isEmpty()) {
            throw new ObjectNotFoundException("Film with id: " + film.getId() + " not found!");
        }

        return result.get();
    }

    public void addLike(Integer filmId, Integer userId) {
        ratingStorage.addLike(filmId, userId);
        log.debug("Film with id:{} was liked by user with id:{}", filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        ratingStorage.removeLike(filmId, userId);
        log.debug("Film with id:{} was unliked by user with id:{}", filmId, userId);
    }
}
