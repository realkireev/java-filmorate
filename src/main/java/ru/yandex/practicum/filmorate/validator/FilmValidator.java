package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;

public class FilmValidator {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final int MIN_DURATION_EXCLUDING = 0;

    public static boolean validate(Film film) throws ValidationException {
        if (film == null) {
            throw new ValidationException("Film object should not be null!");
        }

        if (film.getName().isBlank()) {
            throw new ValidationException("Name should not be empty or null!");
        }

        if (film.getDescription() == null) {
            throw new ValidationException("Description should not be null!");
        }

        if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            throw new ValidationException("Description length should not be greater than " + MAX_DESCRIPTION_LENGTH + "!");
        }

        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Release date should not be earlier than " + MIN_RELEASE_DATE + "!");
        }

        if (film.getDuration() <= MIN_DURATION_EXCLUDING) {
            throw new ValidationException("Duration should be greater than " + MIN_DURATION_EXCLUDING + "!");
        }

        return true;
    }
}
