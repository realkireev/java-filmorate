package ru.yandex.practicum.filmorate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsInAnyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private static final String ENDPOINT = "/films";
    private static final String CONTENT_TYPE = "application/json";

    @Test
    public void test001ShouldReturnEmptyArray() throws Exception {
        mockMvc.perform(get(ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void test002ShouldCreateCorrectFilm() throws Exception {
        String name = "The very first movie";
        String description = "The fascinating description";
        String releaseDate = "1990-01-25";
        int duration = 100;
        int expectedId = 1;
        int mpaId = 1;

        String body = createJson(name, description, releaseDate, duration, mpaId, null);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(CONTENT_TYPE)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedId))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.releaseDate").value(releaseDate))
                .andExpect(jsonPath("$.duration").value(duration))
                .andExpect(jsonPath("$.mpa.id").value(mpaId));
    }

    @Test
    public void test003ShouldNotCreateFailName() throws Exception {
        String name = "";
        String description = "adipisicing";
        String releaseDate = "1967-03-25";
        int duration = 100;

        String body = createJson(name, description, releaseDate, duration, null, null);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(CONTENT_TYPE)
                        .content(body))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test004ShouldNotCreateFailDescription() throws Exception {
        String name = "Scary movie";
        String description = "Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
                "Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно " +
                "20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.";
        String releaseDate = "1967-03-25";
        int duration = 100;

        String body = createJson(name, description, releaseDate, duration, null, null);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(CONTENT_TYPE)
                        .content(body))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test005ShouldNotCreateFailReleaseDate() throws Exception {
        String name = "Good movie";
        String description = "The very first movie";
        String releaseDate = "1890-03-25";
        int duration = 100;

        String body = createJson(name, description, releaseDate, duration, null, null);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(CONTENT_TYPE)
                        .content(body))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test006ShouldNotCreateFailDuration() throws Exception {
        String name = "Movie that had never been made";
        String description = "No one knows";
        String releaseDate = "2010-01-23";
        int duration = -1;

        String body = createJson(name, description, releaseDate, duration, null, null);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(CONTENT_TYPE)
                        .content(body))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test007ShouldUpdateFilm() throws Exception {
        Integer id = 1;
        String name = "Film updated";
        String description = "New film update description";
        String releaseDate = "1989-04-17";
        int duration = 37;
        int mpaId = 2;

        String body = createJson(id, name, description, releaseDate, duration, mpaId, null);

        mockMvc.perform(put(ENDPOINT)
                        .contentType(CONTENT_TYPE)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.releaseDate").value(releaseDate))
                .andExpect(jsonPath("$.duration").value(duration))
                .andExpect(jsonPath("$.mpa.id").value(mpaId));
    }

    @Test
    public void test008ShouldNotUpdateUnknownFilm() throws Exception {
        Integer id = 9999;
        String name = "Film updated";
        String description = "New film update description";
        String releaseDate = "1989-04-17";
        int duration = 37;

        String body = createJson(id, name, description, releaseDate, duration, null, null);

        mockMvc.perform(put(ENDPOINT)
                        .contentType(CONTENT_TYPE)
                        .content(body))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test009ShouldHaveOneFilm() throws Exception {
        Integer id = 1;
        String name = "Film updated";
        String description = "New film update description";
        String releaseDate = "1989-04-17";
        String duration = "37";

        mockMvc.perform(get(ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[0].description").value(description))
                .andExpect(jsonPath("$[0].releaseDate").value(releaseDate))
                .andExpect(jsonPath("$[0].duration").value(duration));
    }

    @Test
    public void test010ShouldHaveOnePopularFilm() throws Exception {
        Integer id = 1;
        String name = "Film updated";
        String description = "New film update description";
        String releaseDate = "1989-04-17";
        int duration = 37;

        mockMvc.perform(get(ENDPOINT + "/popular"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[0].description").value(description))
                .andExpect(jsonPath("$[0].releaseDate").value(releaseDate))
                .andExpect(jsonPath("$[0].duration").value(duration));
    }

    @Test
    public void test011ShouldCreateAnotherFilm() throws Exception {
        String name = "New film";
        String description = "A new film about friends";
        String releaseDate = "1999-12-31";
        int duration = 1000;
        int expectedId = 2;
        int mpaId = 3;

        String body = createJson(name, description, releaseDate, duration, mpaId, null);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(CONTENT_TYPE)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedId))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.releaseDate").value(releaseDate))
                .andExpect(jsonPath("$.duration").value(duration))
                .andExpect(jsonPath("$.mpa.id").value(mpaId));
    }

    @Test
    public void test012ShouldReturnFilmById() throws Exception {
        Integer id = 1;
        String name = "Film updated";
        String description = "New film update description";
        String releaseDate = "1989-04-17";
        String duration = "37";

        mockMvc.perform(get(ENDPOINT + "/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.releaseDate").value(releaseDate))
                .andExpect(jsonPath("$.duration").value(duration));
    }

    @Test
    public void test013ShouldReturnNotFound() throws Exception {
        int id = 999;

        mockMvc.perform(get(ENDPOINT + "/" + id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void test014ShouldSetLikeAndReturnOK() throws Exception {
        // Create a user first
        String body = "{\"login\": \"test_login\",\"email\":\"a@f.k\",\"birthday\":\"2006-06-06\"}";

        mockMvc.perform(post("/users")
                        .contentType(CONTENT_TYPE)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk());

        int filmId = 2;
        int userId = 1;

        mockMvc.perform(put(ENDPOINT + "/" + filmId + "/like/" + userId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void test015ShouldReturnOnePopularFilm() throws Exception {
        Integer id = 2;
        String name = "New film";
        String description = "A new film about friends";
        String releaseDate = "1999-12-31";
        String duration = "1000";

        mockMvc.perform(get(ENDPOINT + "/popular?count=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[0].description").value(description))
                .andExpect(jsonPath("$[0].releaseDate").value(releaseDate))
                .andExpect(jsonPath("$[0].duration").value(duration));
    }

    @Test
    public void test016ShouldReturnTwoPopularFilmsInCorrectOrder() throws Exception {
        mockMvc.perform(get(ENDPOINT + "/popular?count=2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[1].id").value(1));
    }

    @Test
    public void test017ShouldRemoveLikeAndReturnOK() throws Exception {
        int filmId = 2;
        int userId = 1;

        mockMvc.perform(delete(ENDPOINT + "/" + filmId + "/like/" + userId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void test018ShouldReturnTwoPopularFilmInAnyOrder() throws Exception {
        int id1 = 1;
        int id2 = 2;

        mockMvc.perform(get(ENDPOINT + "/popular"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(id1, id2)));
    }

    @Test
    public void test019ShouldReturnUserNotFound() throws Exception {
        int filmId = 2;
        int userId = -500;

        mockMvc.perform(delete(ENDPOINT + "/" + filmId + "/like/" + userId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void test020ShouldCreateCorrectFilmWithoutMPA() throws Exception {
        String name = "The movie without MPA";
        String description = "Usual description";
        String releaseDate = "2000-02-28";
        int duration = 145;
        int expectedId = 3;

        String body = createJson(name, description, releaseDate, duration, null, null);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(CONTENT_TYPE)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedId))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.releaseDate").value(releaseDate))
                .andExpect(jsonPath("$.duration").value(duration));
    }

    @Test
    public void test021ShouldUpdateFilmWithGenre() throws Exception {
        Integer id = 1;
        String name = "Film updated twice";
        String description = "New film twice updated description";
        String releaseDate = "2004-02-29";
        int duration = 199;
        int mpaId = 5;
        String mpaName = "NC-17";
        Integer[] genreIds = { 2 };
        String[] genreNames = { "Драма" };

        String body = createJson(id, name, description, releaseDate, duration, mpaId, genreIds);

        mockMvc.perform(put(ENDPOINT)
                        .contentType(CONTENT_TYPE)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.releaseDate").value(releaseDate))
                .andExpect(jsonPath("$.duration").value(duration))
                .andExpect(jsonPath("$.mpa.id").value(mpaId))
                .andExpect(jsonPath("$.mpa.name").value(mpaName))
                .andExpect(jsonPath("$.genres", hasSize(1)))
                .andExpect(jsonPath("$.genres[0].id").value(genreIds[0]))
                .andExpect(jsonPath("$.genres[0].name").value(genreNames[0]));
    }

    @Test
    public void test022ShouldReturnFilmByIdWithMPAAndGenres() throws Exception {
        int id = 1;
        String name = "Film updated twice";
        String description = "New film twice updated description";
        String releaseDate = "2004-02-29";
        String duration = "199";
        int mpaId = 5;
        String mpaName = "NC-17";
        Integer[] genres = { 2 };
        String[] genreNames = { "Драма" };

        mockMvc.perform(get(ENDPOINT + "/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.releaseDate").value(releaseDate))
                .andExpect(jsonPath("$.duration").value(duration))
                .andExpect(jsonPath("$.mpa.id").value(mpaId))
                .andExpect(jsonPath("$.mpa.name").value(mpaName))
                .andExpect(jsonPath("$.genres", hasSize(1)))
                .andExpect(jsonPath("$.genres[0].id").value(genres[0]))
                .andExpect(jsonPath("$.genres[0].name").value(genreNames[0]));
    }

    @Test
    public void test023ShouldHaveThreeFilmsWithMPAAndGenres() throws Exception {
        String[] names = { "Film updated twice", "New film", "The movie without MPA" };
        String[] descriptions = { "New film twice updated description", "A new film about friends", "Usual description" };
        String[] releaseDates = { "2004-02-29", "1999-12-31", "2000-02-28" };
        Integer[] duration = { 199, 1000, 145 };
        Integer[] mpaIds = { 5, 3, null };
        String[] mpaNames = { "NC-17", "PG-13", null };
        Integer[][] genreIds = { { 2 }, {}, {} };
        String[][] genreNames = { { "Драма" }, {}, {} };

        mockMvc.perform(get(ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value(names[0]))
                .andExpect(jsonPath("$[0].description").value(descriptions[0]))
                .andExpect(jsonPath("$[0].releaseDate").value(releaseDates[0]))
                .andExpect(jsonPath("$[0].duration").value(duration[0]))
                .andExpect(jsonPath("$[0].mpa.id").value(mpaIds[0]))
                .andExpect(jsonPath("$[0].mpa.name").value(mpaNames[0]))
                .andExpect(jsonPath("$[0].genres", hasSize(genreIds[0].length)))
                .andExpect(jsonPath("$[0].genres[0].id").value(genreIds[0][0]))
                .andExpect(jsonPath("$[0].genres[0].name").value(genreNames[0][0]))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value(names[1]))
                .andExpect(jsonPath("$[1].description").value(descriptions[1]))
                .andExpect(jsonPath("$[1].releaseDate").value(releaseDates[1]))
                .andExpect(jsonPath("$[1].duration").value(duration[1]))
                .andExpect(jsonPath("$[1].mpa.id").value(mpaIds[1]))
                .andExpect(jsonPath("$[1].mpa.name").value(mpaNames[1]))
                .andExpect(jsonPath("$[1].genres", hasSize(genreIds[1].length)))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].name").value(names[2]))
                .andExpect(jsonPath("$[2].description").value(descriptions[2]))
                .andExpect(jsonPath("$[2].releaseDate").value(releaseDates[2]))
                .andExpect(jsonPath("$[2].duration").value(duration[2]))
                .andExpect(jsonPath("$[2].genres", hasSize(genreIds[2].length)));
    }

    @Test
    public void test024ShouldUpdateFilmRemoveGenre() throws Exception {
        Integer id = 1;
        String name = "Film updated 3 times in a row!";
        String description = "New film triple updated description";
        String releaseDate = "2003-03-03";
        int duration = 234;
        int mpaId = 5;
        String mpaName = "NC-17";

        String body = createJson(id, name, description, releaseDate, duration, mpaId, null);

        mockMvc.perform(put(ENDPOINT)
                        .contentType(CONTENT_TYPE)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.releaseDate").value(releaseDate))
                .andExpect(jsonPath("$.duration").value(duration))
                .andExpect(jsonPath("$.mpa.id").value(mpaId))
                .andExpect(jsonPath("$.mpa.name").value(mpaName))
                .andExpect(jsonPath("$.genres", hasSize(0)));
    }

    @Test
    public void test025ShouldReturnFilmByIdWithMPAAndWithoutGenres() throws Exception {
        int id = 1;
        String name = "Film updated 3 times in a row!";
        String description = "New film triple updated description";
        String releaseDate = "2003-03-03";
        int duration = 234;
        int mpaId = 5;
        String mpaName = "NC-17";

        mockMvc.perform(get(ENDPOINT + "/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.releaseDate").value(releaseDate))
                .andExpect(jsonPath("$.duration").value(duration))
                .andExpect(jsonPath("$.mpa.id").value(mpaId))
                .andExpect(jsonPath("$.mpa.name").value(mpaName))
                .andExpect(jsonPath("$.genres", hasSize(0)));
    }

    @Test
    public void test026ShouldUpdateFilmAddGenres() throws Exception {
        Integer id = 2;
        String name = "Film with ID=2";
        String description = "Second description";
        String releaseDate = "2004-04-04";
        int duration = 345;
        int mpaId = 4;
        String mpaName = "R";
        Integer[] genreIds = { 1, 2, 3 };
        String[] genreNames = { "Комедия", "Драма", "Мультфильм" };

        String body = createJson(id, name, description, releaseDate, duration, mpaId, genreIds);

        mockMvc.perform(put(ENDPOINT)
                        .contentType(CONTENT_TYPE)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.releaseDate").value(releaseDate))
                .andExpect(jsonPath("$.duration").value(duration))
                .andExpect(jsonPath("$.mpa.id").value(mpaId))
                .andExpect(jsonPath("$.mpa.name").value(mpaName))
                .andExpect(jsonPath("$.genres", hasSize(3)))
                .andExpect(jsonPath("$.genres[0].id").value(genreIds[0]))
                .andExpect(jsonPath("$.genres[0].name").value(genreNames[0]))
                .andExpect(jsonPath("$.genres[1].id").value(genreIds[1]))
                .andExpect(jsonPath("$.genres[1].name").value(genreNames[1]))
                .andExpect(jsonPath("$.genres[2].id").value(genreIds[2]))
                .andExpect(jsonPath("$.genres[2].name").value(genreNames[2]));
    }

    @Test
    public void test027ShouldReturnFilmByIdWithMPAAndSeveralGenres() throws Exception {
        Integer id = 2;
        String name = "Film with ID=2";
        String description = "Second description";
        String releaseDate = "2004-04-04";
        int duration = 345;
        int mpaId = 4;
        String mpaName = "R";
        Integer[] genreIds = { 1, 2, 3 };
        String[] genreNames = { "Комедия", "Драма", "Мультфильм" };

        mockMvc.perform(get(ENDPOINT + "/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.releaseDate").value(releaseDate))
                .andExpect(jsonPath("$.duration").value(duration))
                .andExpect(jsonPath("$.mpa.id").value(mpaId))
                .andExpect(jsonPath("$.mpa.name").value(mpaName))
                .andExpect(jsonPath("$.genres", hasSize(3)))
                .andExpect(jsonPath("$.genres[0].id").value(genreIds[0]))
                .andExpect(jsonPath("$.genres[0].name").value(genreNames[0]))
                .andExpect(jsonPath("$.genres[1].id").value(genreIds[1]))
                .andExpect(jsonPath("$.genres[1].name").value(genreNames[1]))
                .andExpect(jsonPath("$.genres[2].id").value(genreIds[2]))
                .andExpect(jsonPath("$.genres[2].name").value(genreNames[2]));
    }

    @Test
    public void test028ShouldCorrectlyUpdateFilmWithDuplicatedGenres() throws Exception {
        Integer id = 2;
        String name = "Film with ID=2";
        String description = "Second description";
        String releaseDate = "2004-04-04";
        int duration = 345;
        int mpaId = 4;
        String mpaName = "R";
        Integer[] genreIds = { 1, 1, 2, 1 };

        String body = createJson(id, name, description, releaseDate, duration, mpaId, genreIds);

        mockMvc.perform(put(ENDPOINT)
                        .contentType(CONTENT_TYPE)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.releaseDate").value(releaseDate))
                .andExpect(jsonPath("$.duration").value(duration))
                .andExpect(jsonPath("$.mpa.id").value(mpaId))
                .andExpect(jsonPath("$.mpa.name").value(mpaName))
                .andExpect(jsonPath("$.genres", hasSize(2)))
                .andExpect(jsonPath("$.genres[0].id").value(1))
                .andExpect(jsonPath("$.genres[1].id").value(2));
    }

    @Test
    public void test029ShouldReturnFilmNoDuplicatedGenres() throws Exception {
        Integer id = 2;
        String name = "Film with ID=2";
        String description = "Second description";
        String releaseDate = "2004-04-04";
        int duration = 345;
        int mpaId = 4;
        String mpaName = "R";
        Integer[] genreIds = { 1, 2 };
        String[] genreNames = { "Комедия", "Драма" };

        mockMvc.perform(get(ENDPOINT + "/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.releaseDate").value(releaseDate))
                .andExpect(jsonPath("$.duration").value(duration))
                .andExpect(jsonPath("$.mpa.id").value(mpaId))
                .andExpect(jsonPath("$.mpa.name").value(mpaName))
                .andExpect(jsonPath("$.genres", hasSize(2)))
                .andExpect(jsonPath("$.genres[0].id").value(genreIds[0]))
                .andExpect(jsonPath("$.genres[0].name").value(genreNames[0]))
                .andExpect(jsonPath("$.genres[1].id").value(genreIds[1]))
                .andExpect(jsonPath("$.genres[1].name").value(genreNames[1]));
    }

    private String createJson(Integer id, String name, String description, String releaseDate, int duration,
                              Integer mpaId, Integer[] genres) throws JsonProcessingException {
        Map<String, Object> object = createJsonMap(name, description, releaseDate, duration, mpaId, genres);
        object.put("id", id);
        return new ObjectMapper().writeValueAsString(object);
    }

    private String createJson(String name, String description, String releaseDate, int duration,
                              Integer mpaId, Integer[] genres) throws JsonProcessingException {
        Map<String, Object> object = createJsonMap(name, description, releaseDate, duration, mpaId, genres);
        return new ObjectMapper().writeValueAsString(object);
    }

    private Map<String, Object> createJsonMap(String name, String description, String releaseDate, int duration,
                                              Integer mpaId, Integer[] genres) {
        Map<String, Object> object = new HashMap<>();
        object.put("name", name);
        object.put("description", description);
        object.put("releaseDate", releaseDate);
        object.put("duration", duration);
        object.put("mpa", createMPA(mpaId));
        object.put("genres", createGenres(genres));

        return object;
    }

    private Map<String, Integer> createMPA(Integer id) {
        Map<String, Integer> mpa = new HashMap<>();

        if (id != null) {
            mpa.put("id", id);
        }
        return mpa;
    }

    private List<Map<String, Integer>> createGenres(Integer[] ids) {
        List<Map<String, Integer>> result = new ArrayList<>();

        if (ids != null) {
            for (Integer id : ids) {
                Map<String, Integer> genre = new HashMap<>();
                genre.put("id", id);

                result.add(genre);
            }
        }

        return result;
    }
}