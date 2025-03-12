package guru.qa.rococo.test.grpc;

import guru.qa.grpc.rococo.*;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.jupiter.annotation.Painting;
import guru.qa.rococo.jupiter.annotation.meta.GrpcTest;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.model.PaintingJson;
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
import static guru.qa.rococo.utils.RandomDataUtils.randomDescription;
import static guru.qa.rococo.utils.RandomDataUtils.randomPaintingTitle;
import static io.qameta.allure.Allure.step;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@GrpcTest
@Tag("grpc")
@DisplayName("Painting gRPC tests")
public class PaintingGrpcTest {
    private static final Config CFG = Config.getInstance();
    private static final Channel paintingChannel;

    static {
        paintingChannel = ManagedChannelBuilder
                .forAddress(CFG.paintingGrpcAddress(), CFG.paintingGrpcPort())
                .intercept(new AllureGrpc())
                .usePlaintext()
                .build();
    }

    private static final RococoPaintingServiceGrpc.RococoPaintingServiceBlockingStub paintingStub
            = RococoPaintingServiceGrpc.newBlockingStub(paintingChannel);


    @Test
    @Artist
    @Museum
    @Painting
    @DisplayName("Should return painting after filter by title")
    void shouldReturnPaintingAfterFilterByTitle(@NotNull PaintingJson painting) {
        String paintingTitle = painting.title();
        AllPaintingsRequest request = AllPaintingsRequest.newBuilder()
                .setTitle(paintingTitle)
                .setPage(0)
                .setSize(10)
                .build();

        final AllPaintingsResponse allPaintingsResponse = paintingStub.getAllPaintings(request);
        guru.qa.grpc.rococo.Painting paintingResponse = allPaintingsResponse.getPaintingsList().getFirst();

        step("Check painting in response", () -> {
            assertEquals(1, allPaintingsResponse.getPaintingsCount(), "Should return 1 painting");
            check("painting ID",
                    paintingResponse.getId(), equalTo(painting.id().toString()));
            check("painting title",
                    paintingResponse.getTitle(), equalTo(painting.title()));
            check("painting description",
                    paintingResponse.getDescription(), equalTo(painting.description()));
            check("painting content",
                    paintingResponse.getContent(), equalTo(painting.content()));
            check("painting museumId",
                    paintingResponse.getMuseum().getId(), equalTo(painting.museum().id().toString()));
            check("painting artistId",
                    paintingResponse.getArtist().getId(), equalTo(painting.artist().id().toString()));
        });
    }

    @Test
    @DisplayName("Should return zero if painting search result is empty")
    void shouldReturnZeroIfPaintingSearchResultIsEmpty() {
        String paintingTitle = randomPaintingTitle();
        AllPaintingsRequest request = AllPaintingsRequest.newBuilder()
                .setTitle(paintingTitle)
                .setPage(0)
                .setSize(10)
                .build();

        final AllPaintingsResponse response = paintingStub.getAllPaintings(request);

        step("Check museum in response", () -> {
            assertEquals(0, response.getPaintingsList().size(), "Should return empty list of paintings");
            assertEquals(0, response.getTotalCount(), "Should return 0 total count");
        });
    }

    @Test
    @Artist
    @Museum
    @Painting
    @DisplayName("Should return painting by ID")
    void shouldReturnPaintingById(@NotNull PaintingJson painting) {
        String createdPaintingId = painting.id().toString();
        PaintingRequest request = PaintingRequest.newBuilder()
                .setId(createdPaintingId)
                .build();

        final guru.qa.grpc.rococo.Painting response = paintingStub.getPaintingById(request);

        step("Check painting in response", () -> {
            check("painting ID",
                    response.getId(), equalTo(painting.id().toString()));
            check("painting title",
                    response.getTitle(), equalTo(painting.title()));
            check("painting description",
                    response.getDescription(), equalTo(painting.description()));
            check("painting content",
                    response.getContent(), equalTo(painting.content()));
            check("painting museumId",
                    response.getMuseum().getId(), equalTo(painting.museum().id().toString()));
            check("painting artistId",
                    response.getArtist().getId(), equalTo(painting.artist().id().toString()));
        });
    }

    @Test
    @DisplayName("Should return NOT_FOUND error if send not existing painting id")
    void shouldReturnNotFoundErrorIfSendNotExistingPaintingId() {
        String notExistingId = UUID.randomUUID().toString();
        PaintingRequest request = PaintingRequest.newBuilder()
                .setId((notExistingId))
                .build();

        final StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> paintingStub.getPaintingById(request));

        step("Check exception status code and description", () -> {
            assertEquals(Status.NOT_FOUND.getCode(), exception.getStatus().getCode());
            assertEquals("Painting not found by id: " + notExistingId, exception.getStatus().getDescription());
        });
    }

    @Test
    @Artist
    @Museum
    @Painting
    @DisplayName("Should return painting after filter by artist")
    void shouldReturnPaintingAfterFilterByArtist(@NotNull PaintingJson painting, @NotNull ArtistJson artist) {

        ArtistRequest artistRequest = ArtistRequest.newBuilder()
                .setId(artist.id().toString())
                .build();

        PaintingByArtistRequest request = PaintingByArtistRequest.newBuilder()
                .setArtist(artistRequest)
                .setPage(0)
                .setSize(10)
                .build();

        final AllPaintingsResponse allPaintingsResponse = paintingStub.getPaintingByArtist(request);
        guru.qa.grpc.rococo.Painting paintingResponse = allPaintingsResponse.getPaintingsList().getFirst();

        step("Check painting in response", () -> {
            assertEquals(1, allPaintingsResponse.getPaintingsCount(), "Should return 1 painting");
            check("painting ID",
                    paintingResponse.getId(), equalTo(painting.id().toString()));
            check("painting title",
                    paintingResponse.getTitle(), equalTo(painting.title()));
            check("painting description",
                    paintingResponse.getDescription(), equalTo(painting.description()));
            check("painting content",
                    paintingResponse.getContent(), equalTo(painting.content()));
            check("painting museumId",
                    paintingResponse.getMuseum().getId(), equalTo(painting.museum().id().toString()));
            check("painting artistId",
                    paintingResponse.getArtist().getId(), equalTo(painting.artist().id().toString()));
        });
    }

    @Test
    @Artist
    @Museum
    @DisplayName("Should create new painting")
    void shouldCreateNewPainting(@NotNull ArtistJson artist, @NotNull MuseumJson museum) {
        final String title = randomPaintingTitle();
        final String description = randomDescription();
        final String PAINTING_CONTENT = "img/painting.jpg";

        final guru.qa.grpc.rococo.Museum museumRequest = guru.qa.grpc.rococo.Museum.newBuilder()
                .setId(museum.id().toString())
                .build();

        guru.qa.grpc.rococo.Artist artistRequest = guru.qa.grpc.rococo.Artist.newBuilder()
                .setId(artist.id().toString())
                .build();

        final guru.qa.grpc.rococo.Painting request = guru.qa.grpc.rococo.Painting.newBuilder()
                .setTitle(title)
                .setDescription(description)
                .setContent(ImgUtils.convertImageToBase64(PAINTING_CONTENT))
                .setMuseum(museumRequest)
                .setArtist(artistRequest)
                .build();

        final guru.qa.grpc.rococo.Painting response = paintingStub.createPainting(request);

        step("Check created painting in response", () -> {
            check("expected id",
                    response.getId(), notNullValue());
            check("painting title",
                    response.getTitle(), equalTo(title));
            check("painting description",
                    response.getDescription(), equalTo(description));
            check("painting content",
                    response.getContent(), equalTo(ImgUtils.convertImageToBase64(PAINTING_CONTENT)));
            check("painting museumId",
                    response.getMuseum().getId(), equalTo(museum.id().toString()));
            check("painting artistId",
                    response.getArtist().getId(), equalTo(artist.id().toString()));
        });
    }

    @Test
    @Artist
    @Museum
    @Painting
    @DisplayName("Should update painting")
    void shouldUpdateMuseum(@NotNull PaintingJson painting, @NotNull MuseumJson museum, @NotNull ArtistJson artist) {
        final String newTitle = randomPaintingTitle();
        final String newDescription = randomDescription();
        final String PAINTING_CONTENT_NEW = "img/painting_new.jpg";

        final guru.qa.grpc.rococo.Museum museumRequest = guru.qa.grpc.rococo.Museum.newBuilder()
                .setId(museum.id().toString())
                .build();

        guru.qa.grpc.rococo.Artist artistRequest = guru.qa.grpc.rococo.Artist.newBuilder()
                .setId(artist.id().toString())
                .build();

        final guru.qa.grpc.rococo.Painting request = guru.qa.grpc.rococo.Painting.newBuilder()
                .setId(painting.id().toString())
                .setTitle(newTitle)
                .setDescription(newDescription)
                .setContent(ImgUtils.convertImageToBase64(PAINTING_CONTENT_NEW))
                .setMuseum(museumRequest)
                .setArtist(artistRequest)
                .build();

        final guru.qa.grpc.rococo.Painting response = paintingStub.updatePainting(request);

        step("Check created painting in response", () -> {
            check("updated painting id",
                    response.getId(), equalTo(painting.id().toString()));
            check("updated painting title",
                    response.getTitle(), equalTo(newTitle));
            check("updated painting description",
                    response.getDescription(), equalTo(newDescription));
            check("updated painting content",
                    response.getContent(), equalTo(ImgUtils.convertImageToBase64(PAINTING_CONTENT_NEW)));
            check("updated painting museumId",
                    response.getMuseum().getId(), equalTo(museum.id().toString()));
            check("updated painting artistId",
                    response.getArtist().getId(), equalTo(artist.id().toString()));
        });
    }

}
