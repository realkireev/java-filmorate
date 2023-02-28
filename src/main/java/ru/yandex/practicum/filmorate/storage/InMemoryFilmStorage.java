package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final Map<Integer, Set<Integer>> likes = new HashMap<>();
    private static Integer currentId = 1;

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        Integer id = currentId++;
        film.setId(id);
        films.put(id, film);
        likes.put(id, new HashSet<>());

        log.debug("Film created: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new ObjectNotFoundException("Film with id: " + film.getId() + " not found!");
        }
        films.put(film.getId(), film);
        log.debug("Film updated: {}", film);

        return film;
    }

    @Override
    public Film findById(Integer id) {
        if (!films.containsKey(id)) {
            throw new ObjectNotFoundException("Film with id: " + id + " not found!");
        }
        return films.get(id);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        checkFilmExistsInLikes(filmId);
        likes.get(filmId).add(userId);
        log.debug("Film with id:{} was liked by user with id:{}", filmId, userId);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        checkFilmExistsInLikes(filmId);

        if (likes.get(filmId).remove(userId)) {
            log.debug("Film with id:{} was unliked by user with id:{}", filmId, userId);
        } else {
            throw new ObjectNotFoundException("Like of user with id: " + userId + " not found!");
        }
    }

    @Override
    public Collection<Film> findPopular(int count) {
        return likes.entrySet().stream()
                .sorted((x1, x2) -> x2.getValue().size() - x1.getValue().size())
                .limit(count)
                .map(x -> findById(x.getKey()))
                .collect(Collectors.toList());
    }

    private void checkFilmExistsInLikes(Integer filmId) {
        if (!likes.containsKey(filmId)) {
            throw new ObjectNotFoundException("Film with id: " + filmId + " not found!");
        }
    }
}
