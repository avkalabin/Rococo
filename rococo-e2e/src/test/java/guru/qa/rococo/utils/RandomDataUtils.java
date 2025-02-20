package guru.qa.rococo.utils;

import com.github.javafaker.Faker;
import guru.qa.rococo.data.repository.GeoRepository;
import guru.qa.rococo.data.repository.impl.GeoRepositoryHibernate;
import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.model.GeoJson;
import io.qameta.allure.Step;
import org.apache.hc.client5.http.utils.Base64;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class RandomDataUtils {

    private static final Faker faker = new Faker();
    private static final Random random = new Random();

    @Nonnull
    public static String randomUsername() {
        return faker.name().username();
    }

    @Nonnull
    public static String randomArtistName() {
        return faker.artist().name();
    }

    @Nonnull
    public static String randomArtistName(int length) {
        return randomString(length);
    }

    @Nonnull
    public static String randomBiography() {
        return faker.lorem().paragraph(3);
    }

    @Nonnull
    public static String randomBiography(int length) {
        return randomString(length);
    }

    @Nonnull
    public static String randomBase64Image() {
        byte[] randomBytes = new byte[256];
        new Random().nextBytes(randomBytes);
        String base64 = Base64.encodeBase64String(randomBytes);
        return "data:image/jpeg;base64," + base64;
    }

    @Nonnull
    public static String randomBase64Image(int number) {
        byte[] randomBytes = new byte[number];
        new Random().nextBytes(randomBytes);
        String base64 = Base64.encodeBase64String(randomBytes);
        return "data:image/jpeg;base64," + base64;
    }



    @Nonnull
    public static String randomMuseumTitle() {
        return faker.company().name() + " Museum";
    }

    @Nonnull
    public static String randomMuseumTitle(int length) {
        return randomString(length);
    }

    @Nonnull
    public static String randomMuseumDescription() {
        return faker.lorem().paragraph(5);
    }

    @Nonnull
    public static String randomMuseumDescription(int length) {
        return randomString(length);
    }

    @Nonnull
    public static GeoJson randomGeoJson() {
        return new GeoJson(
                randomCity(),
                randomCountryJson()
        );
    }

    @Nonnull
    public static String randomCity() {
        return faker.address().city();
    }

    @Nonnull
    public static String randomCity(int length) {
        return randomString(length);
    }

    @Nonnull
    public static synchronized CountryJson randomCountryJson() {
        GeoRepository geoRepository = new GeoRepositoryHibernate();

        List<CountryJson> countryName = geoRepository.getAllCountry().stream().map(CountryJson::fromEntity).toList();
        Random random = new Random();
        int randomIndex = random.nextInt(countryName.size());
        return countryName.get(randomIndex);
    }

    @Nonnull
    private static String randomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" +
                "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
                "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" +
                "0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
