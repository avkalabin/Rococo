package guru.qa.rococo.utils;

import com.github.javafaker.Faker;
import guru.qa.rococo.data.entity.geo.CountryEntity;
import guru.qa.rococo.data.repository.GeoRepository;
import guru.qa.rococo.data.repository.impl.GeoRepositoryHibernate;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class RandomDataUtils {

    private static final Faker faker = new Faker();

    @Nonnull
    public static String generateRandomWord(Integer charsCount) {
        return faker.lorem().characters(charsCount);
    }

    @Nonnull
    public static String randomUsername() {
        return faker.name().username();
    }

    @Nonnull
    public static String generateRandomPassword(Integer minLength, Integer maxLength) {
        return faker.internet().password(minLength, maxLength);
    }

    @Nonnull
    public static String generateRandomPassword() {
        return generateRandomPassword(5, 12);
    }

    @Nonnull
    public static String randomArtistName() {
        return faker.artist().name();
    }

    @Nonnull
    public static String randomBiography() {
        return faker.lorem().paragraph(3);
    }

    @Nonnull
    public static String randomMuseumTitle() {
        return faker.company().name() + " Museum";
    }

    @Nonnull
    public static String randomDescription() {
        return faker.lorem().paragraph(5);
    }

    @Nonnull
    public static String randomPaintingTitle() {
        return faker.funnyName().name();
    }

    @Nonnull
    public static String randomCity() {
        return faker.address().city();
    }

    @Nonnull
    public static synchronized String randomCountry() {
        GeoRepository geoRepository = new GeoRepositoryHibernate();

        List<CountryEntity> country = geoRepository.getAllCountry();
        Random random = new Random();
        int randomIndex = random.nextInt(country.size());
        return country.get(randomIndex).getName();
    }
}
