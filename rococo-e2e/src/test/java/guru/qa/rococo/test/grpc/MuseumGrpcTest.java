package guru.qa.rococo.test.grpc;

import guru.qa.grpc.rococo.*;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.jupiter.annotation.meta.GrpcTest;
import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.utils.ImgUtils;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.qameta.allure.grpc.AllureGrpc;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static guru.qa.rococo.utils.CustomAssert.check;
import static guru.qa.rococo.utils.RandomDataUtils.*;
import static io.qameta.allure.Allure.step;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@GrpcTest
@Tag("grpc")
@DisplayName("gRPC | Museum tests")
public class MuseumGrpcTest {
    private static final Config CFG = Config.getInstance();
    private static final Channel museumChannel;

    static {
        museumChannel = ManagedChannelBuilder
                .forAddress(CFG.museumGrpcAddress(), CFG.museumGrpcPort())
                .intercept(new AllureGrpc())
                .usePlaintext()
                .build();
    }

    private static final RococoMuseumServiceGrpc.RococoMuseumServiceBlockingStub museumStub
            = RococoMuseumServiceGrpc.newBlockingStub(museumChannel);

    @Test
    @Museum
    @DisplayName("Should return museum after filter by title")
    void shouldReturnMuseumAfterFilterByTitle(@NotNull MuseumJson museum) {
        String museumTitle = museum.title();
        AllMuseumsRequest request = AllMuseumsRequest.newBuilder()
                .setTitle(museumTitle)
                .setPage(0)
                .setSize(10)
                .build();

        final AllMuseumsResponse allMuseumsResponse = museumStub.getAllMuseums(request);
        guru.qa.grpc.rococo.Museum museumResponse = allMuseumsResponse.getMuseumsList().getFirst();

        step("Check museum in response", () -> {
            assertEquals(1, allMuseumsResponse.getMuseumsCount(), "Should return 1 museum");
            check("museum ID",
                    museumResponse.getId(), equalTo(museum.id().toString()));
            check("museum title",
                    museumResponse.getTitle(), equalTo(museum.title()));
            check("museum description",
                    museumResponse.getDescription(), equalTo(museum.description()));
            check("museum photo",
                    museumResponse.getPhoto(), equalTo(museum.photo()));
            check("museum city",
                    museumResponse.getGeo().getCity(), equalTo(museum.geo().city()));
            check("museum countryId",
                    museumResponse.getGeo().getCountry().getId(), equalTo(museum.geo().country().id().toString()));
        });
    }

    @Test
    @DisplayName("Should return zero if museum search result is empty")
    void shouldReturnZeroIfMuseumSearchResultIsEmpty() {
        String museumTitle = randomMuseumTitle();
        AllMuseumsRequest request = AllMuseumsRequest.newBuilder()
                .setTitle(museumTitle)
                .setPage(0)
                .setSize(10)
                .build();

        final AllMuseumsResponse response = museumStub.getAllMuseums(request);

        step("Check museum in response", () -> {
            assertEquals(0, response.getMuseumsList().size(), "Should return empty list of artists");
            assertEquals(0, response.getTotalCount(), "Should return 0 total count");
        });
    }

    @Test
    @Museum
    @DisplayName("Should return museum by ID")
    void shouldReturnMuseumById(@NotNull MuseumJson museum) {
        String createMuseumId = museum.id().toString();
        MuseumRequest request = MuseumRequest.newBuilder()
                .setId(createMuseumId)
                .build();

        final guru.qa.grpc.rococo.Museum response = museumStub.getMuseumById(request);

        step("Check museum in response", () -> {
            check("museum ID",
                    response.getId(), equalTo(museum.id().toString()));
            check("museum title",
                    response.getTitle(), equalTo(museum.title()));
            check("museum description",
                    response.getDescription(), equalTo(museum.description()));
            check("museum photo",
                    response.getPhoto(), equalTo(museum.photo()));
            check("museum city",
                    response.getGeo().getCity(), equalTo(museum.geo().city()));
            check("museum countryId",
                    response.getGeo().getCountry().getId(), equalTo(museum.geo().country().id().toString()));
        });
    }

    @Test
    @DisplayName("Should return NOT_FOUND error if send not existing museum id")
    void shouldReturnNotFoundErrorIfSendNotExistingMuseumId() {
        String notExistingId = UUID.randomUUID().toString();
        MuseumRequest request = MuseumRequest.newBuilder()
                .setId((notExistingId))
                .build();

        final StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> museumStub.getMuseumById(request));

        step("Check exception status code and description", () -> {
            assertEquals(Status.NOT_FOUND.getCode(), exception.getStatus().getCode());
            assertEquals("Museum not found by id: " + notExistingId, exception.getStatus().getDescription());
        });
    }

    @Test
    @DisplayName("Should create new museum")
    void shouldCreateNewMuseum() {
        final String title = randomMuseumTitle();
        final String description = randomDescription();
        final String PHOTO_MUSEUM = "img/museum.jpg";
        CountryJson randomCountry = randomCountry();
        Geo geo = Geo.newBuilder()
                .setCity(randomCity())
                .setCountry(Country.newBuilder()
                        .setId((randomCountry.id().toString()))
                        .setName(randomCountry.name()))
                .build();

        final guru.qa.grpc.rococo.Museum request = guru.qa.grpc.rococo.Museum.newBuilder()
                .setTitle(title)
                .setDescription(description)
                .setPhoto(ImgUtils.convertImageToBase64(PHOTO_MUSEUM))
                .setGeo(geo)
                .build();

        final guru.qa.grpc.rococo.Museum response = museumStub.createMuseum(request);

        step("Check created museum in response", () -> {
            check("expected id",
                    response.getId(), notNullValue());
            check("expected title",
                    response.getTitle(), equalTo(title));
            check("expected description",
                    response.getDescription(), equalTo(description));
            check("expected photo",
                    response.getPhoto(),
                    equalTo(ImgUtils.convertImageToBase64(PHOTO_MUSEUM)));
            check("expected geo",
                    response.getGeo(), equalTo(geo));
        });
    }

    @Test
    @Museum
    @DisplayName("Should update museum")
    void shouldUpdateMuseum(@NotNull MuseumJson museum) {
        final String newTitle = randomMuseumTitle();
        final String newDescription = randomDescription();
        final String PHOTO_MUSEUM_NEW = "img/museum_new.jpg";

        CountryJson randomCountry = randomCountry();
        Geo newGeo = Geo.newBuilder()
                .setCity(randomCity())
                .setCountry(Country.newBuilder()
                        .setId((randomCountry.id().toString()))
                        .setName(randomCountry.name()))
                .build();

        final guru.qa.grpc.rococo.Museum request = guru.qa.grpc.rococo.Museum.newBuilder()
                .setId(museum.id().toString())
                .setTitle(newTitle)
                .setDescription(newDescription)
                .setPhoto(ImgUtils.convertImageToBase64(PHOTO_MUSEUM_NEW))
                .setGeo(newGeo)
                .build();

        final guru.qa.grpc.rococo.Museum response = museumStub.updateMuseum(request);

        step("Check updated museum in response", () -> {
            check("updated museum ID",
                    response.getId(), equalTo(museum.id().toString()));
            check("updated museum title",
                    response.getTitle(), equalTo(newTitle));
            check("updated museum description",
                    response.getDescription(), equalTo(newDescription));
            check("updated museum photo",
                    response.getPhoto(),
                    equalTo(ImgUtils.convertImageToBase64(PHOTO_MUSEUM_NEW)));
            check("updated museum geo",
                    response.getGeo(), equalTo(newGeo));
        });
    }
}
