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
    private static final int MIN_ID = 10;
    private static final int MAX_ID = 10_000;
    private static final int MAX_CATEGORY_ID = 100;
    private static final int MAX_TAG_ID = 100;
    private static final int MIN_PHOTOS = 1;
    private static final int MAX_PHOTOS = 3;
    private static final int MIN_TAGS = 1;
    private static final int MAX_TAGS = 3;
    private static final int UUID_SUBSTRING_LENGTH = 5;

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

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Random random = new Random();

    static {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public static String generateRandomPet() throws JsonProcessingException {
        Pet pet = new Pet();
        pet.setId(generateUniqueId());
        pet.setCategory(generateRandomCategory());
        pet.setName(getRandomElement(PET_NAMES) + "_" +
                UUID.randomUUID().toString().substring(0, UUID_SUBSTRING_LENGTH));
        pet.setPhotoUrls(generatePhotoUrls());
        pet.setTags(generateRandomTags());
        pet.setStatus(getRandomElement(STATUSES));
        return mapper.writeValueAsString(pet);
    }

    public static Integer generateUniqueId() {
        return ThreadLocalRandom.current().nextInt(MIN_ID, MAX_ID);
    }

    private static Category generateRandomCategory() {
        return new Category(
                (long) random.nextInt(MAX_CATEGORY_ID),
                getRandomElement(CATEGORIES)
        );
    }

    private static Tag[] generateRandomTags() {
        int tagCount = random.nextInt(MAX_TAGS - MIN_TAGS + 1) + MIN_TAGS;
        Tag[] tags = new Tag[tagCount];
        for (int i = 0; i < tagCount; i++) {
            tags[i] = new Tag(
                    random.nextLong(MAX_TAG_ID),
                    getRandomElement(TAGS)
            );
        }
        return tags;
    }

    private static String[] generatePhotoUrls() {
        int count = random.nextInt(MAX_PHOTOS - MIN_PHOTOS + 1) + MIN_PHOTOS;
        String[] urls = new String[count];
        Arrays.setAll(urls, i -> "https://example.com/photo_" + UUID.randomUUID() + ".jpg");
        return urls;
    }

    private static <T> T getRandomElement(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
}