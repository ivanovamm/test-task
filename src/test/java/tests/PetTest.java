package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import generators.PetGenerator;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PetTest extends TestBase {

    private final File validImage = new File("src/test/resources/test_image.png");

    private static String testPetId;

    @BeforeEach
    void createTestPet() throws JsonProcessingException {
        String testPet = PetGenerator.generateRandomPet();
        Response response = given()
                .contentType("application/json")
                .body(testPet)
                .post("/pet");


        testPetId = response.jsonPath().getString("id");

        response.then()
                .statusCode(200);

        System.out.println("Created Pet ID: " + testPetId);
    }

    @AfterEach
    void cleanup() {
        if (testPetId != null) {
            given()
                    .header("api_key", 12)
                    .pathParam("petId", testPetId)
                    .when()
                    .delete("/pet/{petId}")
                    .then()
                    .statusCode(anyOf(is(200), is(404)));
        }
        testPetId = null;
    }
    @Test
    @DisplayName("POST /pet/{petId}/uploadImage - Успешная загрузка изображения")
    void uploadImageWithMetadataShouldReturnSuccess() {
        int existingPetId = 1;

        given()
                .pathParam("petId", existingPetId)
                .contentType(ContentType.MULTIPART)
                .multiPart("additionalMetadata", "test metadata")
                .multiPart("file", validImage)
                .when()
                .post("/pet/{petId}/uploadImage")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("type", equalTo("unknown"))
                .body("message", containsString("File uploaded"));
    }

    @Test
    @DisplayName("POST /pet/{petId}/uploadImage - Загрузка без метаданных")
    void uploadImageWithoutMetadataShouldSucceed() {

        given()
                .pathParam("petId", testPetId)
                .contentType(ContentType.MULTIPART)
                .multiPart("file", validImage)
                .when()
                .post("/pet/{petId}/uploadImage")
                .then()
                .statusCode(200)
                .body("message", notNullValue());
    }

    @Test
    @DisplayName("GET /pet/findByStatus - Получение питомцев со статусом available")
    void whenStatusIsAvailable_shouldReturnAvailablePets() {
        given()
                .queryParam("status", "available")
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", everyItem(is("available")));
    }

    @Test
    @DisplayName("GET /pet/findByStatus - Получение питомцев со статусами available и pending")
    void whenMultipleStatusesProvided_shouldReturnFilteredPets() {
        given()
                .queryParam("status", "available,pending")
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(200)
                .body("status", everyItem(isIn(Arrays.asList("available", "pending"))));
    }



    @Test
    @DisplayName("GET /pet/findByStatus - Получение питомцев без статуса")
    void whenNoPetsMatchStatus_shouldReturnEmptyArray() {
        given()
                .queryParam("status", "unknown_status")
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(200)
                .body("", empty());
    }

    @Test
    @DisplayName("GET /pet/findByStatus - Получение питомцев со статусами с пробелами")
    void whenStatusHasSpaces_shouldSanitizeInput() {
        given()
                .queryParam("status", " available , pending ")
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(200)
                .body("status", everyItem(isIn(Arrays.asList("available", "pending"))));
    }

    @Test
    @DisplayName("POST /pet - Создание питомца со случайными данными")
    void createRandomPetTest() throws Exception {
        String petJson = PetGenerator.generateRandomPet();
        given()
                .contentType("application/json")
                .body(petJson)
                .when()
                .post("/pet")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("name", matchesPattern("^[A-Za-z]+_[a-f0-9]{5}$"))
                .body("status", anyOf(is("available"), is("pending"), is("sold")))
                .body("photoUrls", not(emptyArray()))
                .body("tags", everyItem(hasKey("name")));
    }


    @Test
    @DisplayName("GET /pet/{petId} - Успешное получение питомца")
    void getPetByIdShouldReturnValidPet() {
        given()
                .pathParam("petId", testPetId)
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(200);

    }


    @ParameterizedTest
    @ValueSource(strings = {"0", "-5", "abc", "1.5"})
    @DisplayName("GET /pet/{petId} - Невалидный ID")
    void getPetWithInvalidIdShouldFail(String invalidId) {
        given()
                .pathParam("petId", invalidId)
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(400)
                .body("message", containsString("Invalid ID"));
    }

    @Test
    @DisplayName("PUT /pet - Успешное обновление питомца")
    void updatePetWithValidDataShouldSucceed() throws JsonProcessingException {
        Map<String, Object> category = Map.of(
                "id", 1,
                "name", "mammal"
        );

        Map<String, Object> updatedBody = Map.of(
                "id", testPetId,
                "category", category,
                "name", "Lion",
                "photoUrls", List.of("https://example.com/lion.jpg"),
                "tags", Collections.singletonList(Map.of("id", 1, "name", "wild")),
                "status", "available"
        );

        given()
                .contentType(ContentType.JSON)
                .body(updatedBody)
                .when()
                .put("/pet")
                .then()
                .statusCode(200)
                .body("id", equalTo(Integer.parseInt(testPetId)))
                .body("name", equalTo("Lion"))
                .body("status", equalTo("available"))
                .body("category.name", equalTo("mammal"));
    }

    @Test
    @DisplayName("DELETE /pet/{petId} - Успешное удаление питомца")
    void deletePetWithValidIdShouldSucceed() {
        given()
                .header("api_key", "12")
                .pathParam("petId", testPetId)
                .when()
                .delete("/pet/{petId}")
                .then()
                .statusCode(200);

    }


    @Test
    @DisplayName("DELETE /pet/{petId} - Несуществующий питомец")
    void deleteNonExistentPetShouldReturn404() {
        String nonExistentId = "999999";
        given()
                .header("api_key", "12")
                .pathParam("petId", nonExistentId)
                .when()
                .delete("/pet/{petId}")
                .then()
                .statusCode(404);
    }


}
