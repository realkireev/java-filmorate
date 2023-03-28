package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.database.UserDao;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
	@Autowired
	private FilmController filmController;
	@Autowired
	private UserController userController;
	@Autowired
	private FilmService filmService;
	@Autowired
	private UserService userService;
	@Autowired
	private FilmStorage filmStorage;
	@Autowired
	private UserDao userStorage;

	@Test
	void filmControllerShouldBeInContext() {
		assertThat(filmController).isNotNull();
	}

	@Test
	void userControllerShouldBeInContext() {
		assertThat(userController).isNotNull();
	}

	@Test
	void filmServiceShouldBeInContext() {
		assertThat(filmService).isNotNull();
	}

	@Test
	void userServiceShouldBeInContext() {
		assertThat(userService).isNotNull();
	}

	@Test
	void filmStorageShouldBeInContext() {
		assertThat(filmStorage).isNotNull();
	}

	@Test
	void userStorageShouldBeInContext() {
		assertThat(userStorage).isNotNull();
	}
}