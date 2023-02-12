package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;

public class UserValidator {
    public boolean validate(User user) throws ValidationException {
        if (user == null) {
            throw new ValidationException("User object should not be null!");
        }

        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Email should contain @!");
        }

        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Login should not contain spaces!");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Birthday should not be in the future!");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return true;
    }
}
