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
    private static final int MIN_QUANTITY = 1;
    private static final int MAX_QUANTITY = 10;
    private static final int MIN_ORDER_ID = 1000;
    private static final int MAX_ORDER_ID = 10_000;
    private static final int MIN_PET_ID = 1;
    private static final int MAX_PET_ID = 1000;
    private static final int SHIP_DATE_DAYS_OFFSET = 1;

    public static String generateRandomOrder() throws JsonProcessingException {
        Order order = new Order();
        order.setId(generateUniqueId());
        order.setPetId(generatePetId());
        order.setQuantity(random.nextInt(MIN_QUANTITY, MAX_QUANTITY + 1));
        order.setShipDate(LocalDateTime.now().plusDays(SHIP_DATE_DAYS_OFFSET).toString());
        order.setStatus(getRandomElement(STATUS));
        order.setComplete(random.nextBoolean());
        return mapper.writeValueAsString(order);
    }

    public static String generateTestOrder() throws JsonProcessingException {
        Order order = new Order();
        order.setId(generateUniqueId());
        order.setPetId(generatePetId());
        order.setQuantity(random.nextInt(MIN_QUANTITY, MAX_QUANTITY + 1));
        order.setShipDate(LocalDateTime.now().plusDays(SHIP_DATE_DAYS_OFFSET).toString());
        order.setStatus(getRandomElement(STATUS));
        order.setComplete(random.nextBoolean());
        return mapper.writeValueAsString(order);
    }

    public static Integer generateUniqueId() {
        return ThreadLocalRandom.current().nextInt(MIN_ORDER_ID, MAX_ORDER_ID);
    }

    private static Integer generatePetId() {
        return ThreadLocalRandom.current().nextInt(MIN_PET_ID, MAX_PET_ID);
    }

    private static <T> T getRandomElement(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    static {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.findAndRegisterModules();
    }
}