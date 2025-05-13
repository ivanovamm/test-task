package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import generators.StoreGenerator;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class StoreTest extends TestBase {

    private static String testOrderId;

    @BeforeEach
    void createTestOrder() throws JsonProcessingException {
        String order = StoreGenerator.generateTestOrder();
        Response response = given()
                .contentType("application/json")
                .body(order)
                .post("/store/order");

        testOrderId = response.jsonPath().getString("id");

        response.then()
                .statusCode(200)
                .body("id", notNullValue());

    }


    @Test
    @DisplayName("GET /store/inventory - Получение списка заказов и статусов")
    void getInventoryShouldReturnSuccess(){
        given()
                .contentType("application/json")
                .when()
                .get("/store/inventory")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("$", instanceOf(Map.class))
                .body("size()", greaterThan(0));

    }

    @Test
    @DisplayName("POST /store/order - Успешное создание заказа")
    void createOrderShouldReturnValidResponse() throws JsonProcessingException {
        String validOrderJson = StoreGenerator.generateRandomOrder();
        given()
                .contentType("application/json")
                .body(validOrderJson)
                .when()
                .post("/store/order")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("shipDate", notNullValue())
                .body("status", isIn(Arrays.asList("placed", "approved", "delivered")))
                .body("complete", isIn(Arrays.asList(true, false)));
    }

    @Test
    @DisplayName("GET /store/order/{orderId} - Успешное получение заказа")
    void getOrderByIdShouldReturnValidOrder() {
        given()
                .pathParam("orderId", testOrderId)
                .when()
                .get("/store/order/{orderId}")
                .then()
                .statusCode(200)
                .body("id", equalTo(Integer.parseInt(testOrderId)))
                .body("status", notNullValue())
                .body("shipDate", notNullValue())
                .body("complete", notNullValue());
    }

    @Test
    @DisplayName("GET /store/order/{orderId} - Несуществующий заказ")
    void getNonExistentOrderShouldReturnNotFound() {
        int nonExistentOrderId = 999999;
        given()
                .pathParam("orderId", nonExistentOrderId)
                .when()
                .get("/store/order/{orderId}")
                .then()
                .statusCode(404)
                .body("message", containsString("Order not found"));
    }




    @Test
    @DisplayName("Удаление заказа с несуществующим ID — ожидается 404 Not Found")
    void deleteNonExistentOrderShouldReturnNotFound() {
        long nonExistentId = 999999999;

        given()
                .pathParam("orderId", nonExistentId)
                .when()
                .delete("/store/order/{orderId}")
                .then()
                .statusCode(404)
                .body("message", containsString("Order Not Found"));
    }


    @Test
    @DisplayName("Удаление заказа — успешный DELETE")
    void DeleteOrderShouldSucceed() {
        given()
                .pathParam("orderId", testOrderId)
                .when()
                .delete("/store/order/{orderId}")
                .then()
                .statusCode(200);
    }



}
