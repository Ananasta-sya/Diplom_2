package ru.practicum.steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import ru.practicum.models.Order;

import static io.restassured.RestAssured.given;

public class OrderSteps {
    public final String URL = "https://stellarburgers.nomoreparties.site/";
    public final String ORDER_API = "/api/orders";

    @Step("Создание заказа под авторизованным пользователем")
    public ValidatableResponse createOrder(Order order, String accessToken) {
        return given()
                .baseUri(URL)
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(ORDER_API)
                .then();
}

}
