package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import generators.UserGenerator;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class UserTest extends TestBase {

    private  String testUsername;

    private String testUser;

    @BeforeEach
    void createTestUser() throws JsonProcessingException {
        testUser = UserGenerator.generateTestUser();
        Response response = given()
                .contentType("application/json")
                .body(testUser)
                .post("/user");


        String createdId = response.jsonPath().getString("message");
        testUsername = "testuser";

        response.then()
                .statusCode(200)
                .body("message", notNullValue());
    }


    @Test
    @DisplayName("POST /user - Успешное добавление пользователя")
    void createUserShouldReturnSuccess() throws JsonProcessingException {
        String requestBody = UserGenerator.generateRandomUser();
        given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/user")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("type", equalTo("unknown"));
    }

    @Test
    @DisplayName("POST /user/createWithArray - Успешное добавление нескольких пользователей")
    void createUsersWithArrayShouldReturnSuccess() throws JsonProcessingException {
        String usersArray = UserGenerator.generateUserArrayJson(2);
        given()
                .contentType("application/json")
                .body(usersArray)
                .log().all()
                .when()
                .post("/user/createWithArray")
                .then()
                .log().all()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("type", equalTo("unknown"))
                .body("message", anyOf(
                        containsString("ok"),
                        matchesPattern("\\d+")
                ));
    }

    @Test
    @DisplayName("GET /user/login - Успешный вход пользователя")
    void loginUserWithCorrectDataShouldReturnSuccess() {
        String requestBody = """
                {
                    "username": "testuser",
                    "password": "testPass123"
                }
                """;

        given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .get("/user/login")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("type", equalTo("unknown"))
                .body("message",
                        containsString("logged in user session:")
                );
    }


    @Test
    @DisplayName("GET /user/logout - Успешный выход пользователя")
    void logoutShouldReturnSuccess(){
        given()
                .contentType("application/json")
                .when()
                .get("/user/logout")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("type", equalTo("unknown"))
                .body("message",
                        containsString("ok")
                );
    }


    @Test
    @DisplayName("DELETE /user/{username} - Успешное удаление пользователя")
    void deleteUserShouldReturnSuccess() {
        String name = testUsername;
        given()
                .pathParam("username", name)
                .when()
                .delete("/user/{username}")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("type", equalTo("unknown"))
                .body("message", equalTo(name));
    }


    @Test
    @DisplayName("DELETE /user/{username} - Пользователь не найден (404)")
    void deleteNonExistentUserShouldReturnNotFound() {
        String nonExistentUsername = "nonexistentuser" + System.currentTimeMillis();
        given()
                .pathParam("username", nonExistentUsername)
                .when()
                .delete("/user/{username}")
                .then()
                .statusCode(404);

    }


    @Test
    @DisplayName("GET /user/{username} - Успешное получение пользователя")
    void getUserByUsernameShouldReturnValidData() {
        given()
                .pathParam("username", testUsername)
                .when()
                .get("/user/{username}")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("username", equalTo(testUsername))
                .body("firstName", not(emptyOrNullString()))
                .body("lastName", not(emptyOrNullString()))
                .body("email", matchesPattern(".+@.+\\..+"))
                .body("password", notNullValue())
                .body("phone", notNullValue())
                .body("userStatus", anyOf(is(0), is(1)));
    }


    @ParameterizedTest
    @MethodSource("nonExistentUsernamesProvider")
    @DisplayName("GET /user/{username} - Пользователь не найден (404)")
    void getNonExistentUserShouldReturnNotFound(String username) {
        given()
                .pathParam("username", username)
                .when()
                .get("/user/{username}")
                .then()
                .statusCode(404)
                .body("code", equalTo(1))
                .body("type", equalTo("error"))
                .body("message", containsString("User not found"));
    }

    private static Stream<Arguments> nonExistentUsernamesProvider() {
        return Stream.of(
                Arguments.of("user" + System.currentTimeMillis())
        );
    }

    @Test
    @DisplayName("PUT /user/{username} - Пользователь не найден (404)")
    void updateNonExistentUser() {
        String randomUserName = UserGenerator.generateUsername();
        Map<String, Object> userBody = new HashMap<>();
        userBody.put("firstName", "TestName");

        given()
                .contentType(ContentType.JSON)
                .pathParam("username", randomUserName)
                .body(userBody)
                .when()
                .put("/user/{username}")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("PUT /user/{username} - Успешное изменение данных пользователя")
    void updateExistingUser() throws JsonProcessingException {
        String newUser = UserGenerator.generateRandomUser();
        given()
                .contentType(ContentType.JSON)
                .pathParam("username", testUsername)
                .body(newUser)
                .when()
                .put("/user/{username}")
                .then()
                .statusCode(200);

    }

    @AfterEach
    void cleanup() {
        if (testUsername != null && !testUsername.isEmpty()) {
            System.out.println("Cleaning up user: " + testUsername);

            try {
                given()
                        .pathParam("username", testUsername)
                        .when()
                        .delete("/user/{username}")
                        .then()
                        .statusCode(anyOf(is(200), is(404)));
            } catch (Exception e) {
                System.err.println("Error during cleanup: " + e.getMessage());
            }
        }
        testUsername = null;
    }





}
