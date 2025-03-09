package guru.qa.rococo.test.grpc;

import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.AllArtistsRequest;
import guru.qa.grpc.rococo.AllArtistsResponse;
import guru.qa.grpc.rococo.ArtistRequest;
import guru.qa.grpc.rococo.RococoArtistServiceGrpc;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.jupiter.annotation.meta.GrpcTest;
import guru.qa.rococo.model.ArtistJson;
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

import static com.google.protobuf.ByteString.copyFromUtf8;
import static guru.qa.rococo.model.ArtistJson.fromGrpc;
import static guru.qa.rococo.utils.CustomAssert.check;
import static guru.qa.rococo.utils.RandomDataUtils.randomBiography;
import static guru.qa.rococo.utils.RandomDataUtils.randomUsername;
import static io.qameta.allure.Allure.step;
import static java.util.UUID.fromString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@GrpcTest
@Tag("grpc")
@DisplayName("Artist gRPC tests")
public class ArtistGrpcTest {
    private static final Config CFG = Config.getInstance();
    private static final Channel artistChannel;

    static {
        artistChannel = ManagedChannelBuilder
                .forAddress(CFG.artistGrpcAddress(), CFG.artistGrpcPort())
                .intercept(new AllureGrpc())
                .usePlaintext()
                .build();
    }

    private static final RococoArtistServiceGrpc.RococoArtistServiceBlockingStub artistStub
            = RococoArtistServiceGrpc.newBlockingStub(artistChannel);


    @Test
    @Artist
    @DisplayName("Should return all artists without name filter")
    void shouldReturnAllArtistsWithoutNameFilter(ArtistJson artist) {

        AllArtistsRequest request = AllArtistsRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .build();

        AllArtistsResponse response = artistStub.getAllArtists(request);

        step("Should return 1 artist in response", () -> {
            assertEquals(1, response.getArtistsCount(), "Should return 1 artist");
            assertEquals(artist, fromGrpc(response.getArtistsList().getFirst()),
                    "Should return correct artist");
        });
    }

    @Test
    @DisplayName("Should return zero if artist search result is empty")
    void shouldReturnZeroIfArtistSearchResultIsEmpty() {
        String artistName = randomUsername();
        AllArtistsRequest request = AllArtistsRequest.newBuilder()
                .setName(artistName)
                .setPage(0)
                .setSize(10)
                .build();

        final AllArtistsResponse response = artistStub.getAllArtists(request);

        step("Check that artists in response count is 0", () -> {
            assertEquals(0, response.getArtistsList().size(), "Should return empty list of artists");
            assertEquals(0, response.getTotalCount(), "Should return 0 total count");
        });
    }

    @Test
    @Artist
    @DisplayName("Should return artist data by ID")
    void shouldReturnArtistDataById(@NotNull ArtistJson artist) {
        String createdArtistId = artist.id().toString();
        ArtistRequest request = ArtistRequest.newBuilder()
                .setId(createdArtistId)
                .build();

        final guru.qa.grpc.rococo.Artist artistResponse = artistStub.getArtistById(request);

        step("Check artist in response", () -> assertEquals(artist, fromGrpc(artistResponse)));
    }

    @Test
    @DisplayName("Should return NOT_FOUND error if send not existing artist id")
    void shouldReturnNotFoundErrorIfSendNotExistingArtistId() {
        String notExistingId = UUID.randomUUID().toString();
        ArtistRequest request = ArtistRequest.newBuilder()
                .setId((notExistingId))
                .build();

        final StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> artistStub.getArtistById(request));

        step("Check exception status code and description", () -> {
            assertEquals(Status.NOT_FOUND.getCode(), exception.getStatus().getCode());
            assertEquals("Artist not found by id: " + notExistingId, exception.getStatus().getDescription());
        });
    }

    @Test
    @DisplayName("Should create new artist")
    void shouldCreateNewArtist() {
        final String name = randomUsername();
        final String biography = randomBiography();
        final String PHOTO_ARTIST = "img/artist.jpg";
        guru.qa.grpc.rococo.Artist request = guru.qa.grpc.rococo.Artist.newBuilder()
                .setName(name)
                .setBiography(biography)
                .setPhoto(ImgUtils.convertImageToBase64(PHOTO_ARTIST))
                .build();

        final guru.qa.grpc.rococo.Artist artistResponse = artistStub.createArtist(request);

        step("Check created artist in response", () -> {
            check("expected id",
                    artistResponse.getId(), notNullValue());
            check("expected name",
                    artistResponse.getName(), equalTo(name));
            check("expected biography",
                    artistResponse.getBiography(), equalTo(biography));
            check("expected photo",
                    artistResponse.getPhoto(),
                    equalTo(ImgUtils.convertImageToBase64(PHOTO_ARTIST)));
        });
    }

    @Test
    @Artist
    @DisplayName("Should update artist")
    void shouldUpdateArtist(ArtistJson artist) {
        final String newName = randomUsername();
        final String newBiography = randomBiography();
        final String PHOTO_ARTIST_NEW = "img/artist_new.jpg";

        guru.qa.grpc.rococo.Artist request = guru.qa.grpc.rococo.Artist.newBuilder()
                .setId(artist.id().toString())
                .setName(newName)
                .setBiography(newBiography)
                .setPhoto(ImgUtils.convertImageToBase64(PHOTO_ARTIST_NEW))
                .build();

        final guru.qa.grpc.rococo.Artist artistResponse = artistStub.updateArtist(request);

        step("Check created artist in response", () -> {
            check("updated artist ID",
                    artistResponse.getId(), equalTo(artist.id().toString()));
            check("updated arist name",
                    artistResponse.getName(), equalTo(newName));
            check("updated artist biography",
                    artistResponse.getBiography(), equalTo(newBiography));
            check("updated artist photo",
                    artistResponse.getPhoto(),
                    equalTo(ImgUtils.convertImageToBase64(PHOTO_ARTIST_NEW)));
        });
    }
}
