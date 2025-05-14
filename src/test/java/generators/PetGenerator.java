package generators;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import models.Category;
import models.Pet;
import models.Tag;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PetGenerator {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Random random = new Random();

    private static final List<String> PET_NAMES = Arrays.asList(
            "Buddy", "Luna", "Max", "Bella", "Charlie", "Lucy", "Cooper", "Daisy"
    );

    private static final List<String> CATEGORIES = Arrays.asList(
            "Dogs", "Cats", "Birds", "Fish", "Reptiles"
    );

    private static final List<String> TAGS = Arrays.asList(
            "friendly", "playful", "quiet", "trained", "vaccinated"
    );

    private static final List<String> STATUSES = Arrays.asList(
            "available", "pending", "sold"
    );

    public static String generateRandomPet() throws JsonProcessingException {
        Pet pet = new Pet();
        pet.setId(generateUniqueId());
        pet.setCategory(generateRandomCategory());
        pet.setName(getRandomElement(PET_NAMES) + "_" + UUID.randomUUID().toString().substring(0, 5));
        pet.setPhotoUrls(generatePhotoUrls());
        pet.setTags(generateRandomTags());
        pet.setStatus(getRandomElement(STATUSES));
        return mapper.writeValueAsString(pet);
    }

    private static Integer generateUniqueId() {
        return ThreadLocalRandom.current().nextInt(10, 10_000);
    }

    private static Category generateRandomCategory() {
        return new Category(
                (long) random.nextInt(100),
                getRandomElement(CATEGORIES)
        );
    }

    private static Tag[] generateRandomTags() {
        int tagCount = random.nextInt(3) + 1;
        Tag[] tags = new Tag[tagCount];
        for (int i = 0; i < tagCount; i++) {
            tags[i] = new Tag(
                    random.nextLong(100),
                    getRandomElement(TAGS)
            );
        }
        return tags;
    }

    private static String[] generatePhotoUrls() {
        int count = random.nextInt(3) + 1;
        String[] urls = new String[count];
        Arrays.setAll(urls, i -> "https://example.com/photo_" + UUID.randomUUID() + ".jpg");
        return urls;
    }

    private static <T> T getRandomElement(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    static {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }
}