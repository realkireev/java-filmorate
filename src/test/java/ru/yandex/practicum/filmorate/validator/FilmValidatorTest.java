package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class FilmValidatorTest {
    private static final LocalDate CORRECT_DATE = LocalDate.of(1895, 12, 28);
    private static final LocalDate INCORRECT_DATE = LocalDate.of(1895, 12, 27);
    private static final int CORRECT_DURATION = 1;
    private static final int INCORRECT_DURATION = 0;
    private static final String CORRECT_NAME = "Some correct name";
    private static final String DESCRIPTION_200_CHARS =
            "Some correct description!This is a correct description!Attention!This description is correct! Yes!!!" +
            "Some correct description!This is a correct description!Attention!This description is correct! Yes!!!";
    private static final String DESCRIPTION_201_CHARS =
            "Some incorrect description!This is an incorrect description!Attention!This description is incorrect!" +
            "Some incorrect description!This is the incorrect description!Attention!This description is incorrect!";

    @Test
    void shouldNotPassNullObject() {
        final ValidationException e = assertThrows(
                ValidationException.class,
                () -> FilmValidator.validate(null)
        );

        assertEquals(FilmValidator.FILM_NULL_ALERT, e.getMessage());
    }

    @Test
    void shouldNotPassNullName() {
        Film film = new Film(null, DESCRIPTION_200_CHARS, CORRECT_DATE, CORRECT_DURATION );

        final ValidationException e = assertThrows(
                ValidationException.class,
                () -> FilmValidator.validate(film)
        );

        assertEquals(FilmValidator.NAME_NULL_ALERT, e.getMessage());
    }

    @Test
    void shouldNotPassEmptyName() {
        Film film = new Film("", DESCRIPTION_200_CHARS, CORRECT_DATE, CORRECT_DURATION);

        final ValidationException e = assertThrows(
                ValidationException.class,
                () -> FilmValidator.validate(film)
        );

        assertEquals(FilmValidator.NAME_NULL_ALERT, e.getMessage());
    }

    @Test
    void shouldNotPassBlankName() {
        Film film = new Film("   ", DESCRIPTION_200_CHARS, CORRECT_DATE, CORRECT_DURATION );

        final ValidationException e = assertThrows(
                ValidationException.class,
                () -> FilmValidator.validate(film)
        );

        assertEquals(FilmValidator.NAME_NULL_ALERT, e.getMessage());
    }

    @Test
    void shouldNotPassDescriptionOver200Characters() {
        Film film = new Film(CORRECT_NAME, DESCRIPTION_201_CHARS, CORRECT_DATE, CORRECT_DURATION);

        final ValidationException e = assertThrows(
                ValidationException.class,
                () -> FilmValidator.validate(film)
        );

        assertEquals(FilmValidator.DESCRIPTION_LENGTH_ALERT, e.getMessage());
    }

    @Test
    void shouldNotPassEarlierReleaseDate() {
        Film film = new Film(CORRECT_NAME, DESCRIPTION_200_CHARS, INCORRECT_DATE, CORRECT_DURATION);

        final ValidationException e = assertThrows(
                ValidationException.class,
                () -> FilmValidator.validate(film)
        );

        assertEquals(FilmValidator.RELEASE_DATE_ALERT, e.getMessage());
    }

    @Test
    void shouldNotPassDuration0() {
        Film film = new Film(CORRECT_NAME, DESCRIPTION_200_CHARS, CORRECT_DATE, INCORRECT_DURATION);

        final ValidationException e = assertThrows(
                ValidationException.class,
                () -> FilmValidator.validate(film)
        );

        assertEquals(FilmValidator.DURATION_ALERT, e.getMessage());
    }
}