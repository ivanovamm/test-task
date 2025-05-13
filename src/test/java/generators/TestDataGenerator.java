package generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class TestDataGenerator {
    private static final Random random = new Random();

    public static String generateUserArrayJson(int count) {
        List<String> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(generateSingleUserJson());
        }
        return "[" + String.join(",", users) + "]";
    }

    private static String generateSingleUserJson() {
        long timestamp = System.currentTimeMillis();
        return String.format(
                "{\"id\": %d, \"username\": \"user_%d\", \"firstName\": \"Test\", " +
                        "\"lastName\": \"User\", \"email\": \"test%d@example.com\", " +
                        "\"password\": \"pass%d\", \"phone\": \"+1%d\", \"userStatus\": 0}",
                timestamp, timestamp, timestamp, timestamp, timestamp
        );
    }
}