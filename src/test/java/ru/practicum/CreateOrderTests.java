package ru.practicum;

import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.models.Order;
import ru.practicum.models.User;
import ru.practicum.steps.OrderSteps;
import ru.practicum.steps.UserSteps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.empty;

public class CreateOrderTests {
    private OrderSteps orderSteps;
    private UserSteps userSteps;
    private User user;
    private String accessToken;
    private List<String> validIngredients = new ArrayList<>();
    private List<String> invalidIngredients = new ArrayList<>();

    @Before
    public void setUp() {
        orderSteps = new OrderSteps();
        userSteps = new UserSteps();
        user = new User(
                RandomStringUtils.randomAlphabetic(10) + "@yandex.ru",
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10)
        );

        validIngredients.add ("61c0c5a71d1f82001bdaaa76");
        validIngredients.add ("61c0c5a71d1f82001bdaaa77");
        invalidIngredients.add("invalidIngredientHash");
        userSteps.createUser(user);
        accessToken = userSteps.loginUser(user)
                .extract()
                .path("accessToken");
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и валидными ингредиентами")
    public void createOrderWithAuthAndValidIngredientsTest() {
        Order order = new Order(validIngredients);
        userSteps.loginUser(user);
        orderSteps.createOrder(order, accessToken)
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue())
                .body("order.ingredients", not(empty()));
    }

    @Test
    @DisplayName("Создание заказа без авторизации и c валидными ингредиентами")
    public void createOrderWithoutAuthTest() {
        Order order = new Order(validIngredients);

        orderSteps.createOrder(order, "")
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue())
                .body("order.ingredients", not(empty()));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredientsTest() {
        Order order = new Order(Collections.emptyList());
        userSteps.loginUser(user);
        orderSteps.createOrder(order, accessToken)
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithInvalidIngredientsTest() {
        Order order = new Order(invalidIngredients);
        userSteps.loginUser(user);
        orderSteps.createOrder(order, accessToken)
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @After
    public void tearDown() {

        userSteps.deleteCourier(accessToken);

    }
}
