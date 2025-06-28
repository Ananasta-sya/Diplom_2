package ru.practicum.steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import ru.practicum.BaseClass;
import ru.practicum.models.User;

import static io.restassured.RestAssured.given;


public class UserSteps{
    public final String CREATE_USER = "/api/auth/register";
    public final String LOGIN_USER = "/api/auth/login";
    public final String DELETE_USER = "/api/auth/user";

    @Step("Создание нового пользователя")
    public ValidatableResponse createUser(User user){
        return given()
                .baseUri(BaseClass.URL)
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(CREATE_USER)
                .then();
    }
    @Step("Вход в систему под логином пользователя")
    public ValidatableResponse loginUser(User user){
        return given()
                .baseUri(BaseClass.URL)
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(LOGIN_USER)
                .then();
    }

    @Step("Удаление пользователя из базы данных")
    public ValidatableResponse deleteCourier(String accessToken) {
        return given()
                .baseUri(BaseClass.URL)
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .when()
                .delete(DELETE_USER)
                .then();
    }
}
