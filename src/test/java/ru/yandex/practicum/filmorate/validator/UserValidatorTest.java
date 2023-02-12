package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {
    private final static String CORRECT_LOGIN = "login";
    private final static String CORRECT_NAME = "This is not LOGIN!";
    private final static String CORRECT_EMAIL = "me@ya.ru";
    private final static String INCORRECT_EMAIL_WITHOUT_AT = "meya.ru";
    private final static String INCORRECT_EMAIL_WITH_AT = "me@";
    private final static LocalDate CORRECT_BIRTHDAY = LocalDate.of(2000, 1, 1);
    private final static LocalDate INCORRECT_BIRTHDAY = LocalDate.of(3000, 1, 1);


    @Test
    void shouldNotPassNullObject() {
        final ValidationException e = assertThrows(
                ValidationException.class,
                () -> UserValidator.validate(null)
        );

        assertEquals(UserValidator.USER_NULL_ALERT, e.getMessage());
    }

    @Test
    void shouldNotPassNullEmail() {
        User user = new User(null, CORRECT_LOGIN, CORRECT_BIRTHDAY);

        final ValidationException e = assertThrows(
                ValidationException.class,
                () -> UserValidator.validate(user)
        );

        assertEquals(UserValidator.EMAIL_ALERT, e.getMessage());
    }

    @Test
    void shouldNotPassEmptyEmail() {
        User user = new User("   ", CORRECT_LOGIN, CORRECT_BIRTHDAY);

        final ValidationException e = assertThrows(
                ValidationException.class,
                () -> UserValidator.validate(user)
        );

        assertEquals(UserValidator.EMAIL_ALERT, e.getMessage());
    }

    @Test
    void shouldNotPassIncorrectEmailWithAt() {
        User user = new User(INCORRECT_EMAIL_WITH_AT, CORRECT_LOGIN, CORRECT_BIRTHDAY);

        final ValidationException e = assertThrows(
                ValidationException.class,
                () -> UserValidator.validate(user)
        );

        assertEquals(UserValidator.EMAIL_ALERT, e.getMessage());
    }

    @Test
    void shouldNotPassIncorrectEmailWithoutAt() {
        User user = new User(INCORRECT_EMAIL_WITHOUT_AT, CORRECT_LOGIN, CORRECT_BIRTHDAY);

        final ValidationException e = assertThrows(
                ValidationException.class,
                () -> UserValidator.validate(user)
        );

        assertEquals(UserValidator.EMAIL_ALERT, e.getMessage());
    }

    @Test
    void shouldNotPassNullLogin() {
        User user = new User(CORRECT_EMAIL, null, CORRECT_BIRTHDAY);

        final ValidationException e = assertThrows(
                ValidationException.class,
                () -> UserValidator.validate(user)
        );

        assertEquals(UserValidator.LOGIN_ALERT, e.getMessage());
    }

    @Test
    void shouldNotPassLoginWithSpaces() {
        User user = new User(CORRECT_EMAIL, "I think it is ok", CORRECT_BIRTHDAY);

        final ValidationException e = assertThrows(
                ValidationException.class,
                () -> UserValidator.validate(user)
        );

        assertEquals(UserValidator.LOGIN_ALERT, e.getMessage());
    }

    @Test
    void shouldNotPassNullBirthday() {
        User user = new User(CORRECT_EMAIL, CORRECT_LOGIN, null);

        final ValidationException e = assertThrows(
                ValidationException.class,
                () -> UserValidator.validate(user)
        );

        assertEquals(UserValidator.BIRTHDAY_ALERT, e.getMessage());
    }

    @Test
    void shouldNotPassBirthdayInFuture() {
        User user = new User(CORRECT_EMAIL, CORRECT_LOGIN, INCORRECT_BIRTHDAY);

        final ValidationException e = assertThrows(
                ValidationException.class,
                () -> UserValidator.validate(user)
        );

        assertEquals(UserValidator.BIRTHDAY_ALERT, e.getMessage());
    }

    @Test
    void shouldSetNameAsLogin() {
        User user = new User(CORRECT_EMAIL, CORRECT_LOGIN, CORRECT_BIRTHDAY);
        UserValidator.validate(user);

        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void shouldSetDifferentNameAsLogin() {
        User user = new User(CORRECT_EMAIL, CORRECT_LOGIN, CORRECT_BIRTHDAY);
        user.setName(CORRECT_NAME);
        UserValidator.validate(user);

        assertEquals(CORRECT_LOGIN, user.getLogin());
        assertEquals(CORRECT_NAME, user.getName());
    }
}