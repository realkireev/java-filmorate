package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;

public class UserValidator {
    public static final String USER_NULL_ALERT = "User object should not be null!";
    public static final String EMAIL_ALERT = "Email should contain @!";
    public static final String LOGIN_ALERT = "Login should not contain spaces!";
    public static final String BIRTHDAY_ALERT = "Birthday should not be in the future!";

    public static boolean validate(User user) throws ValidationException {
        if (user == null) {
            throw new ValidationException(USER_NULL_ALERT);
        }

        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().matches("^(.+)@(\\S+)$")) {
            throw new ValidationException(EMAIL_ALERT);
        }

        if (user.getLogin() == null || user.getLogin().contains(" ")) {
            throw new ValidationException(LOGIN_ALERT);
        }

        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException(BIRTHDAY_ALERT);
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return true;
    }
}
