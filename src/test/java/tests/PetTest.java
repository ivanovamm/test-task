package tests;

import generators.PetGenerator;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PetTest extends TestBase {

    private final File validImage = new File("src/test/resources/test_image.png");

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
        int existingPetId = 2;

        given()
                .pathParam("petId", existingPetId)
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
                .pathParam("petId", 5)
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(200)
                .body("id", equalTo(5))
                .body("name", notNullValue())
                .body("status", notNullValue())
                .body("category.id", notNullValue())
                .body("tags", hasSize(greaterThan(0)));
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












}
