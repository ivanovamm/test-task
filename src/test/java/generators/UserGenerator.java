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
        user.setPassword("testPass123");
        user.setPhone("1223818010");
        user.setUserStatus(1);
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
        return ThreadLocalRandom.current().nextLong(1000, 10_000);
    }

    private static String generateUsername() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private static String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private static String generatePhone() {
        return "+1" + String.format("%010d", random.nextInt(1_000_000_000));
    }

    private static <T> T getRandomElement(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    static {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }
}