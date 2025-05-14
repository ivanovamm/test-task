package generators;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class UserGenerator {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Random random = new Random();

    private static final List<String> FIRST_NAMES = List.of("John", "Alice", "Bob", "Emma", "David");
    private static final List<String> LAST_NAMES = List.of("Doe", "Smith", "Johnson", "Brown", "Wilson");
    private static final Long MIN_ID = 1000L;
    private static final Long MAX_ID = 10_000L;
    private static final Integer USERNAME_UUID_LENGTH = 8;
    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Integer PASSWORD_LENGTH = 10;
    private static final String PHONE_PREFIX = "+1";
    private static final Integer PHONE_NUMBER_LENGTH = 10;
    private static final Integer PHONE_NUMBER_MAX = 1_000_000_000;
    private static final Integer TEST_USER_STATUS = 1;
    private static final String TEST_PHONE_NUMBER = "1223818010";
    private static final String TEST_PASSWORD = "testPass123";

    public static String generateRandomUser() throws JsonProcessingException {
        User user = new User();
        user.setId(generateUniqueId());
        user.setUsername(generateUsername());
        user.setFirstName(getRandomElement(FIRST_NAMES));
        user.setLastName(getRandomElement(LAST_NAMES));
        user.setEmail(user.getUsername() + "@example.com");
        user.setPassword(generatePassword());
        user.setPhone(generatePhone());
        user.setUserStatus(random.nextInt(2));
        return mapper.writeValueAsString(user);
    }

    public static String generateTestUser() throws JsonProcessingException {
        User user = new User();
        user.setId(generateUniqueId());
        user.setUsername("testuser");
        user.setFirstName("Ivan");
        user.setLastName("Ivanov");
        user.setEmail("ivan@example.com");
        user.setPassword(TEST_PASSWORD);
        user.setPhone(TEST_PHONE_NUMBER);
        user.setUserStatus(TEST_USER_STATUS);
        return mapper.writeValueAsString(user);
    }

    public static String generateUserArrayJson(int count) throws JsonProcessingException {
        List<String> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(generateRandomUser());
        }
        return "[" + String.join(",", users) + "]";
    }

    private static Long generateUniqueId() {
        return ThreadLocalRandom.current().nextLong(MIN_ID, MAX_ID);
    }

    public static String generateUsername() {
        return "user_" + UUID.randomUUID().toString().substring(0, USERNAME_UUID_LENGTH);
    }

    private static String generatePassword() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
        }
        return sb.toString();
    }

    private static String generatePhone() {
        return PHONE_PREFIX + String.format("%0" + PHONE_NUMBER_LENGTH + "d", random.nextInt(PHONE_NUMBER_MAX));
    }

    private static <T> T getRandomElement(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    static {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }
}