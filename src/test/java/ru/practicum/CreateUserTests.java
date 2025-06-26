package ru.practicum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.models.User;
import ru.practicum.steps.UserSteps;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateUserTests {
    private User user;
    private final UserSteps userSteps = new UserSteps();
    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        String email = RandomStringUtils.randomAlphabetic(10) + "@yandex.ru";
        String password = RandomStringUtils.randomAlphabetic(10);
        String name = RandomStringUtils.randomAlphabetic(10);
        user = new User(email, password, name);
    }


    @Test
    @DisplayName("Создание нового уникального пользователя")
    public void createNewUserTest() {
        userSteps.createUser(user)
                .statusCode(SC_OK)
                .body("success", Matchers.is (true));
    }
    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void createAreadyExistsUserTest() {
        userSteps.createUser(user);
        userSteps.createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("User already exists"));

    }
    @Test
    @DisplayName("Создание пользователя без имени")
    public void createUserWhithoutNameTest () {
        user.setName(null);
        userSteps.createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }
    @Test
    @DisplayName("Создание пользователя без пароля")
    public void createUserWhithoutPasswordTest () {
        user.setPassword(null);
        userSteps.createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }
    @Test
    @DisplayName("Создание пользователя без email")
    public void createUserWhithoutEmailTest () {
        user.setEmail(null);
        userSteps.createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }
    @After
    public void tearDown() {
        Response response = userSteps.loginUser(user).extract().response();

        if (response.getStatusCode() == SC_OK) {
            String accessToken = response.path("accessToken");
            userSteps.deleteCourier(accessToken);
        }
    }
}
