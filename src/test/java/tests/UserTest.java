package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import generators.TestDataGenerator;
import generators.UserGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class UserTest extends TestBase {

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
    void createUsersWithArrayShouldReturnSuccess() {
        String usersArray = TestDataGenerator.generateUserArrayJson(2);
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
        String name = "test";
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


    @ParameterizedTest
    @ValueSource(strings = {"testuser"})
    @DisplayName("GET /user/{username} - Успешное получение пользователя")
    void getUserByUsernameShouldReturnValidData(String username) {
        given()
                .pathParam("username", username)
                .when()
                .get("/user/{username}")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("username", equalTo(username))
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



}
