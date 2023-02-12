package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;

public class FilmValidator {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final int MIN_DURATION_EXCLUDING = 0;

    public static final String FILM_NULL_ALERT = "Film object should not be null!";
    public static final String NAME_NULL_ALERT = "Name should not be empty or null!";
    public static final String DESCRIPTION_NULL_ALERT = "Description should not be null!";
    public static final String DESCRIPTION_LENGTH_ALERT = "Description length should not be greater than "
            + MAX_DESCRIPTION_LENGTH + "!";
    public static final String RELEASE_DATE_ALERT = "Release date should not be earlier than " + MIN_RELEASE_DATE + "!";
    public static final String DURATION_ALERT = "Duration should be greater than " + MIN_DURATION_EXCLUDING + "!";


    public static boolean validate(Film film) throws ValidationException {
        if (film == null) {
            throw new ValidationException(FILM_NULL_ALERT);
        }

        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException(NAME_NULL_ALERT);
        }

        if (film.getDescription() == null) {
            throw new ValidationException(DESCRIPTION_NULL_ALERT);
        }

        if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            throw new ValidationException(DESCRIPTION_LENGTH_ALERT);
        }

        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException(RELEASE_DATE_ALERT);
        }

        if (film.getDuration() <= MIN_DURATION_EXCLUDING) {
            throw new ValidationException(DURATION_ALERT);
        }

        return true;
    }
}
