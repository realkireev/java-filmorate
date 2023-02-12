package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Set<Film> films = new TreeSet<>(Comparator.comparingInt(Film::getId));
    private int currentId = 1;

    @GetMapping
    public Set<Film> findAll() {
        return films;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film.setId(currentId++);
        films.add(film);
        log.debug("Film created: {}", film);

        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (!films.remove(film)) {
            throw new ValidationException("Film with id: " + film.getId() + " not found!");
        }
        films.add(film);
        log.debug("Film updated: {}", film);

        return film;
    }
}
