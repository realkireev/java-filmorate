package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Set<User> users = new TreeSet<>(Comparator.comparingInt(User::getId));
    private final UserValidator userValidator = new UserValidator();
    private int currentId = 1;

    @GetMapping
    public Set<User> findAll() {
        return users;
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (userValidator.validate(user)) {
            user.setId(currentId++);
            users.add(user);
            log.debug("User created: {}", user);

            return user;
        }
        return null;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        if (!users.remove(user)) {
            throw new ValidationException("User with id: " + user.getId() + " not found!");
        }

        if (userValidator.validate(user)) {
            users.add(user);
            log.debug("User updated: {}", user);

            return user;
        }
        return null;
    }
}
