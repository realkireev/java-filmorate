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
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private static final String ENDPOINT = "/users";
    private static final String CONTENT_TYPE = "application/json";

    @Test
    public void test001ShouldCreateCorrectUser() throws Exception {
        String login = "awesome_login";
        String name = "Ivan Ivanov";
        String email = "cool@hacker.ru";
        String birthday = "2005-03-08";
        int expectedId = 1;
        String body = createJson(login, name, email, birthday);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(CONTENT_TYPE)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedId))
                .andExpect(jsonPath("$.login").value(login))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.birthday").value(birthday));
    }

    @Test
    public void test002ShouldNotCreateFailLogin() throws Exception {
        String login = "awesome login";
        String name = "Petr Petrov";
        String email = "cool@hacker.ru";
        String birthday = "2001-03-08";
        String body = createJson(login, name, email, birthday);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(CONTENT_TYPE)
                        .content(body))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test003ShouldNotCreateFailEmail() throws Exception {
        String login = "11login";
        String name = "John Doe";
        String email = "cool@@_hacker.ru";
        String birthday = "2001-03-08";
        String body = createJson(login, name, email, birthday);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(CONTENT_TYPE)
                        .content(body))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test004ShouldNotCreateFailBirthday() throws Exception {
        String login = "login";
        String name = "John Doe";
        String email = "cool@_hacker.ru";
        String birthday = "2999-03-08";
        String body = createJson(login, name, email, birthday);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(CONTENT_TYPE)
                        .content(body))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test005ShouldUpdateUser() throws Exception {
        String id = "1";
        String login = "updated_login";
        String name = "Updated user";
        String email = "new@mail.dev";
        String birthday = "2000-01-01";
        String body = createJson(id, login, name, email, birthday);

        mockMvc.perform(put(ENDPOINT)
                        .contentType(CONTENT_TYPE)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.login").value(login))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.birthday").value(birthday));
    }

    @Test
    public void test006ShouldNotUpdateUserNotFound() throws Exception {
        String id = "999";
        String login = "norm_login";
        String name = "norm user";
        String email = "new@pochta.dev";
        String birthday = "2011-11-11";
        String body = createJson(id, login, name, email, birthday);

        mockMvc.perform(put(ENDPOINT)
                        .contentType(CONTENT_TYPE)
                        .content(body))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test007ShouldHaveOneUser() throws Exception {
        String id = "1";
        String login = "updated_login";
        String name = "Updated user";
        String email = "new@mail.dev";
        String birthday = "2000-01-01";

        mockMvc.perform(get(ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].login").value(login))
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[0].email").value(email))
                .andExpect(jsonPath("$[0].birthday").value(birthday));
    }

    @Test
    public void test008ShouldCreateFriendUserNoName() throws Exception {
        String login = "Friend";
        String email = "friend@vk.com";
        String birthday = "1917-11-07";
        int expectedId = 2;

        // null name will be replaced with login:
        String body = createJson(login, null, email, birthday);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(CONTENT_TYPE)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedId))
                .andExpect(jsonPath("$.login").value(login))
                .andExpect(jsonPath("$.name").value(login))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.birthday").value(birthday));
    }

    @Test
    public void test009ShouldCreateCommonFriendUser() throws Exception {
        String login = "Common";
        String name = "Common Friend";
        String email = "admin@commons.com";
        String birthday = "2006-06-06";
        int expectedId = 3;

        String body = createJson(login, name, email, birthday);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(CONTENT_TYPE)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedId))
                .andExpect(jsonPath("$.login").value(login))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.birthday").value(birthday));
    }

    @Test
    public void test010ShouldReturnUserById() throws Exception {
        String id = "1";
        String login = "updated_login";
        String name = "Updated user";
        String email = "new@mail.dev";
        String birthday = "2000-01-01";

        mockMvc.perform(get(ENDPOINT + "/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.login").value(login))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.birthday").value(birthday));
    }

    @Test
    public void test011ShouldReturnUserNotFound() throws Exception {
        String id = "9999";

        mockMvc.perform(get(ENDPOINT + "/" + id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void test012ShouldReturnZeroCommonFriends() throws Exception {
        int id1 = 1;
        int id2 = 2;

        mockMvc.perform(get(ENDPOINT + "/" + id1 + "/friends/common/" + id2))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void test013ShouldMakeFriendsUserId1AndUserId2() throws Exception {
        int id1 = 1;
        int id2 = 2;

        // Friendship requires confirmation, so this request makes:
        // id1 becomes a friend for id2,
        // but id2 isn't a friend for id1 until he confirms it

        mockMvc.perform(put(ENDPOINT + "/" + id1 + "/friends/" + id2))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void test014ShouldNotMakeFriendsWithUnknownUser() throws Exception {
        int id1 = 1;
        int id2 = 9999;

        mockMvc.perform(put(ENDPOINT + "/" + id1 + "/friends/" + id2))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void test015ShouldReturnFriendOfUserId1() throws Exception {
        int id1 = 1;
        int friendId = 2;
        String login = "Friend";
        String name = "Friend";
        String email = "friend@vk.com";
        String birthday = "1917-11-07";

        mockMvc.perform(get(ENDPOINT + "/" + id1 + "/friends"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(friendId))
                .andExpect(jsonPath("$[0].login").value(login))
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[0].email").value(email))
                .andExpect(jsonPath("$[0].birthday").value(birthday));
    }

    @Test
    public void test016ShouldReturnEmptyAsFriendshipRequiresConfirmation() throws Exception {
        int id1 = 2;

        mockMvc.perform(get(ENDPOINT + "/" + id1 + "/friends"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void test016_1ShouldMakeFriendsUserId2AndUserId1() throws Exception {
        int id1 = 2;
        int id2 = 1;

        // This request is a confirmation of friendship between id1 and id2

        mockMvc.perform(put(ENDPOINT + "/" + id1 + "/friends/" + id2))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void test017ShouldReturnEmptyCommonFriends() throws Exception {
        int id1 = 1;
        int id2 = 2;

        mockMvc.perform(get(ENDPOINT + "/" + id1 + "/friends/common/" + id2))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void test018ShouldMakeFriendsUser1AndUser3() throws Exception {
        int id1 = 1;
        int id2 = 3;

        mockMvc.perform(put(ENDPOINT + "/" + id1 + "/friends/" + id2))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void test019ShouldReturnTwoFriendsOfUserId1() throws Exception {
        int id1 = 1;
        int friendId1 = 2;
        int friendId2 = 3;

        mockMvc.perform(get(ENDPOINT + "/" + id1 + "/friends"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(friendId1, friendId2)));
    }

    @Test
    public void test020ShouldMakeFriendsUser2AndUser3() throws Exception {
        int id1 = 2;
        int id2 = 3;

        mockMvc.perform(put(ENDPOINT + "/" + id1 + "/friends/" + id2))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void test021ShouldReturnTwoFriendsOfUserId2() throws Exception {
        int id1 = 2;
        int friendId1 = 1;
        int friendId2 = 3;

        mockMvc.perform(get(ENDPOINT + "/" + id1 + "/friends"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(friendId1, friendId2)));
    }

    @Test
    public void test022ShouldReturnCommonFriendOfUserId1AndUserId2() throws Exception {
        int id1 = 1;
        int id2 = 2;
        String login = "Common";
        String name = "Common Friend";
        String email = "admin@commons.com";
        String birthday = "2006-06-06";
        int commonFriendId = 3;

        mockMvc.perform(get(ENDPOINT + "/" + id1 + "/friends/common/" + id2))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(commonFriendId))
                .andExpect(jsonPath("$[0].login").value(login))
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[0].email").value(email))
                .andExpect(jsonPath("$[0].birthday").value(birthday));
    }

    @Test
    public void test023ShouldDeleteFriendshipBetweenUserId1AndUserId2() throws Exception {
        int id1 = 1;
        int id2 = 2;

        mockMvc.perform(delete(ENDPOINT + "/" + id1 + "/friends/" + id2))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void test024ShouldReturnOneFriendOfUserId1() throws Exception {
        int id1 = 1;
        int friendId = 3;

        mockMvc.perform(get(ENDPOINT + "/" + id1 + "/friends"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(friendId));
    }

    @Test
    public void test025ShouldReturnOneFriendOfUserId2() throws Exception {
        int id1 = 2;
        int friendId = 3;

        mockMvc.perform(get(ENDPOINT + "/" + id1 + "/friends"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(friendId));
    }

    @Test
    public void test026ShouldReturnCommonFriendOfUserId1AndUserId2() throws Exception {
        int id1 = 1;
        int id2 = 2;
        String login = "Common";
        String name = "Common Friend";
        String email = "admin@commons.com";
        String birthday = "2006-06-06";
        int commonFriendId = 3;

        mockMvc.perform(get(ENDPOINT + "/" + id1 + "/friends/common/" + id2))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(commonFriendId))
                .andExpect(jsonPath("$[0].login").value(login))
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[0].email").value(email))
                .andExpect(jsonPath("$[0].birthday").value(birthday));
    }

    private String createJson(String id, String login, String name, String email, String birthday)
            throws JsonProcessingException {
        Map<String, String> object = createJsonMap(login, name, email, birthday);
        object.put("id", id);
        return new ObjectMapper().writeValueAsString(object);
    }

    private String createJson(String login, String name, String email, String birthday)
            throws JsonProcessingException {
        Map<String, String> object = createJsonMap(login, name, email, birthday);
        return new ObjectMapper().writeValueAsString(object);
    }

    private Map<String, String> createJsonMap(String login, String name, String email, String birthday) {
        Map<String, String> object = new HashMap<>();
        object.put("login", login);
        object.put("name", name);
        object.put("email", email);
        object.put("birthday", birthday);

        return object;
    }
}
