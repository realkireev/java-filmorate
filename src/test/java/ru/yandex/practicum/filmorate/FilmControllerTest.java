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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.MethodName.class)
public class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private final static String ENDPOINT = "/films";
    private final static String CONTENT_TYPE = "application/json";

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
        String duration = "100";
        int expectedId = 1;

        String body = createJson(name, description, releaseDate, duration);

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
    public void test003ShouldNotCreateFailName() throws Exception {
        String name = "";
        String description = "adipisicing";
        String releaseDate = "1967-03-25";
        String duration = "100";

        String body = createJson(name, description, releaseDate, duration);

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
        String duration = "100";

        String body = createJson(name, description, releaseDate, duration);

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
        String duration = "100";

        String body = createJson(name, description, releaseDate, duration);

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
        String duration = "-1";

        String body = createJson(name, description, releaseDate, duration);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(CONTENT_TYPE)
                        .content(body))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test007ShouldUpdateFilm() throws Exception {
        String id = "1";
        String name = "Film updated";
        String description = "New film update description";
        String releaseDate = "1989-04-17";
        String duration = "37";

        String body = createJson(id, name, description, releaseDate, duration);

        mockMvc.perform(put(ENDPOINT)
                        .contentType(CONTENT_TYPE)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.releaseDate").value(releaseDate))
                .andExpect(jsonPath("$.duration").value(duration));
    }

    @Test
    public void test008ShouldNotUpdateUnknownFilm() throws Exception {
        String id = "9999";
        String name = "Film updated";
        String description = "New film update description";
        String releaseDate = "1989-04-17";
        String duration = "37";

        String body = createJson(id, name, description, releaseDate, duration);

        mockMvc.perform(put(ENDPOINT)
                        .contentType(CONTENT_TYPE)
                        .content(body))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test009ShouldHaveOneFilm() throws Exception {
        String id = "1";
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
        String id = "1";
        String name = "Film updated";
        String description = "New film update description";
        String releaseDate = "1989-04-17";
        String duration = "37";

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
        String duration = "1000";
        int expectedId = 2;

        String body = createJson(name, description, releaseDate, duration);

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
    public void test012ShouldReturnFilmById() throws Exception {
        int id = 1;
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
        int filmId = 2;
        int userId = 1;

        mockMvc.perform(put(ENDPOINT + "/" + filmId + "/like/" + userId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void test015ShouldReturnOnePopularFilm() throws Exception {
        String id = "2";
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

    private String createJson(String id, String name, String description, String releaseDate, String duration)
            throws JsonProcessingException {
        Map<String, String> object = createJsonMap(name, description, releaseDate, duration);
        object.put("id", id);
        return new ObjectMapper().writeValueAsString(object);
    }

    private String createJson(String name, String description, String releaseDate, String duration)
            throws JsonProcessingException {
        Map<String, String> object = createJsonMap(name, description, releaseDate, duration);
        return new ObjectMapper().writeValueAsString(object);
    }

    private Map<String, String> createJsonMap(String name, String description, String releaseDate, String duration) {
        Map<String, String> object = new HashMap<>();
        object.put("name", name);
        object.put("description", description);
        object.put("releaseDate", releaseDate);
        object.put("duration", duration);

        return object;
    }
}