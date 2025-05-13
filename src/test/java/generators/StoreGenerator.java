package generators;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import models.Order;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class StoreGenerator {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Random random = new Random();

    private static final List<String> STATUS = List.of("placed", "approved", "delivered");
    private static final int PET_ID_RANGE = 1000;

    public static String generateRandomOrder() throws JsonProcessingException {
        Order order = new Order();
        order.setId(generateUniqueId());
        order.setPetId(generatePetId());
        order.setQuantity(random.nextInt(1, 11));
        order.setShipDate(LocalDateTime.now().plusDays(1).toString());
        order.setStatus(getRandomElement(STATUS));
        order.setComplete(random.nextBoolean());

        return mapper.writeValueAsString(order);
    }

    public static String generateTestOrder() throws JsonProcessingException {
        Order order = new Order();
        order.setId(1);
        order.setPetId(generatePetId());
        order.setQuantity((random.nextInt(1, 11)));
        order.setShipDate(LocalDateTime.now().plusDays(1).toString());
        order.setStatus(getRandomElement(STATUS));
        order.setComplete(random.nextBoolean());
        return mapper.writeValueAsString(order);
    }

    private static Integer generateUniqueId() {
        return ThreadLocalRandom.current().nextInt(1000, 10_000);
    }

    private static Integer generatePetId() {
        return ThreadLocalRandom.current().nextInt(1, PET_ID_RANGE);
    }

    private static <T> T getRandomElement(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    static {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.findAndRegisterModules();
    }
}