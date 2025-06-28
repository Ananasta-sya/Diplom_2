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

import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class LogInUserTests {
    private User user;
    private final UserSteps userSteps = new UserSteps();
    String accessToken;
    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        String email = RandomStringUtils.randomAlphabetic(10) + "@yandex.ru";
        String password = RandomStringUtils.randomAlphabetic(10);
        String name = RandomStringUtils.randomAlphabetic(10);
        user = new User(email, password, name);
        userSteps.createUser(user);
        Response response = userSteps.loginUser(user).extract().response();
        accessToken = response.path("accessToken");
    }
    @Test
    @DisplayName("Вход под существующим пользователем")
    public void loginUserTest() {
        userSteps.loginUser(user)
                .statusCode(200)
                .body("accessToken", notNullValue());
    }
    @Test
    @DisplayName("Вход под существующим пользователем с неверным паролем")
    public void loginInvalidPasswordUserTest() {
        user.setPassword(RandomStringUtils.randomAlphabetic(10));
        userSteps.loginUser(user)
                .statusCode(SC_UNAUTHORIZED)
                .body("success", Matchers.is (false))
                .body("message", equalTo("email or password are incorrect"));
    }
    @Test
    @DisplayName("Вход под существующим пользователем с неверным логином")
    public void loginInvalidEmailUserTest() {
        user.setEmail(RandomStringUtils.randomAlphabetic(10));
        userSteps.loginUser(user)
                .statusCode(SC_UNAUTHORIZED)
                .body("success", Matchers.is (false))
                .body("message", equalTo("email or password are incorrect"));

    }
    @After
    public void tearDown() {

            userSteps.deleteCourier(accessToken);

    }
}
