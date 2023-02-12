package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import javax.validation.Valid;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Set<User> users = new TreeSet<>(Comparator.comparingInt(User::getId));
    private int currentId = 1;

    @GetMapping
    public Set<User> findAll() {
        return users;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        user.setId(currentId++);
        setNameIfNotExists(user);

        users.add(user);
        log.debug("User created: {}", user);

        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (!users.remove(user)) {
            throw new ValidationException("User with id: " + user.getId() + " not found!");
        }
        setNameIfNotExists(user);

        users.add(user);
        log.debug("User updated: {}", user);

        return user;
    }
    
    private void setNameIfNotExists(User user) {
        if (user != null) {
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
        }
    }
}
