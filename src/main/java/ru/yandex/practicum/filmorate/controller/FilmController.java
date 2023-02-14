package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import javax.validation.Valid;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private static Integer currentId = 1;

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film, BindingResult errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors.toString());
        }

        Integer id = currentId++;
        film.setId(id);
        films.put(id, film);
        log.debug("Film created: {}", film);

        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film, BindingResult errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors.toString());
        }

        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Film with id: " + film.getId() + " not found!");
        }
        films.put(film.getId(), film);
        log.debug("Film updated: {}", film);

        return film;
    }
}
