package guru.qa.rococo.test.rest;

import guru.qa.rococo.jupiter.annotation.*;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.model.rest.pageable.RestResponsePage;
import guru.qa.rococo.service.impl.GatewayApiClient;
import guru.qa.rococo.utils.ImgUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.Response;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

import static guru.qa.rococo.utils.CustomAssert.check;
import static guru.qa.rococo.utils.RandomDataUtils.randomDescription;
import static guru.qa.rococo.utils.RandomDataUtils.randomMuseumTitle;
import static io.qameta.allure.Allure.step;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@RestTest
@Tag("rest")
@DisplayName("REST | Painting tests")
public class PaintingRestTest {

    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();

    GatewayApiClient gatewayApiClient = new GatewayApiClient();

    @Test
    @Artist
    @Museum
    @Painting(title = "The Starry Night")
    @DisplayName("Should return painting")
    void shouldReturnPainting(@Nonnull PaintingJson painting) {
        Response<RestResponsePage<PaintingJson>> response = gatewayApiClient.getAllPaintings(painting.title(), 0, 10);

        step("Check painting in response", () -> {
            assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
            assertNotNull(response.body(), "Response body should not be null");

            List<PaintingJson> paintingList = response.body().getContent();
            assertFalse(paintingList.isEmpty(), "Expected at least one painting in the response");
        });
    }

    @Test
    @Artist
    @Museum
    @Painting
    @DisplayName("Should return painting by artist")
    void shouldReturnPaintingByArtist(@Nonnull PaintingJson painting) {
        Response<RestResponsePage<PaintingJson>> response = gatewayApiClient.getPaintingByArtist(painting.artist().id(), 0, 10);

        step("Check painting in response", () -> {
            assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
            assertNotNull(response.body(), "Response body should not be null");

            List<PaintingJson> paintingList = response.body().getContent();
            assertFalse(paintingList.isEmpty(), "Expected at least one painting in the response");
            assertEquals(painting.title(), paintingList.getFirst().title(), "Returned painting title does not match the search query");
        });
    }

    @Test
    @Artist
    @Museum
    @Painting
    @DisplayName("Should return painting by ID")
    void shouldReturnPaintingById(@Nonnull PaintingJson painting) {
        Response<PaintingJson> response = gatewayApiClient.getPaintingById(painting.id());

        step("Check painting in response", () -> {
            assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
            assertNotNull(response.body(), "Response body should not be null");

            PaintingJson responseBody = response.body();
            assertEquals(painting.title(), responseBody.title(), "Painting title mismatch! Expected: '%s', Actual: '%s'");
        });
    }

    @Test
    @ApiLogin
    @User
    @Artist
    @Museum
    @DisplayName("Should create new painting")
    void shouldCreateNewPainting(@Token String token, @Nonnull ArtistJson artistJson, @Nonnull MuseumJson museumJson) {
        PaintingJson painting = new PaintingJson(
                null,
                randomMuseumTitle(),
                randomDescription(),
                ImgUtils.convertImageToBase64("img/painting.jpg"),
                museumJson,
                artistJson
        );

        Response<PaintingJson> response = gatewayApiClient.createPainting(token, painting);

        step("Check created painting in response", () -> {
            assertEquals(201, response.code(), "Expected HTTP status 201 but got " + response.code());
            assertNotNull(response.body(), "Response body should not be null");

            PaintingJson responseBody = response.body();
            check("expected id",
                    responseBody.id(), notNullValue());
            check("expected title",
                    responseBody.title(), equalTo(painting.title()));
            check("expected description",
                    responseBody.description(), equalTo(painting.description()));
            check("expected content",
                    responseBody.content(), equalTo(painting.content()));
            check("expected museum id",
                    responseBody.museum().id(), equalTo(museumJson.id()));
            check("expected artist",
                    responseBody.artist().id(), equalTo(artistJson.id()));
        });
    }

    @Test
    @Artist
    @Museum
    @DisplayName("Should get unauthorized response when create new painting without login")
    void shouldGetUnauthorizedWhenCreateNewPaintingWithoutLogin(@Nonnull ArtistJson artistJson, @Nonnull MuseumJson museumJson) {
        PaintingJson painting = new PaintingJson(
                null,
                randomMuseumTitle(),
                randomDescription(),
                ImgUtils.convertImageToBase64("img/painting.jpg"),
                museumJson,
                artistJson
        );

        Response<PaintingJson> response = gatewayApiClient.createPainting("token", painting);

        step("Check response status code", () -> {
            assertEquals(401, response.code(), "Expected HTTP status 401 but got " + response.code());
        });
    }

    @Test
    @Artist
    @Museum
    @Painting
    @ApiLogin
    @User
    @DisplayName("Should update museum")
    void shouldUpdateMuseum(@Token String token, @Nonnull MuseumJson museumJson,
                            @Nonnull ArtistJson artistJson, @Nonnull PaintingJson painting) {
        PaintingJson updatedPainting = new PaintingJson(
                painting.id(),
                randomMuseumTitle(),
                randomDescription(),
                ImgUtils.convertImageToBase64("img/painting_new.jpg"),
                museumJson,
                artistJson
        );

        Response<PaintingJson> response = gatewayApiClient.updatePainting(token, updatedPainting);

        step("Check updated painting in response", () -> {
            assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
            assertNotNull(response.body(), "Response body should not be null");

            PaintingJson responseBody = response.body();
            check("expected id",
                    responseBody.id(), equalTo(updatedPainting.id()));
            check("expected title",
                    responseBody.title(), equalTo(updatedPainting.title()));
            check("expected description",
                    responseBody.description(), equalTo(updatedPainting.description()));
            check("expected content",
                    responseBody.content(), equalTo(updatedPainting.content()));
            check("expected museum id",
                    responseBody.museum().id(), equalTo(museumJson.id()));
            check("expected artist",
                    responseBody.artist().id(), equalTo(artistJson.id()));
        });
    }

    @Test
    @ApiLogin
    @User
    @Artist
    @Museum
    @DisplayName("Should get NOT_FOUND response when update nonexistent painting")
    void shouldGetNotFoundWhenUpdateNonexistentMuseum(@Token String token, @Nonnull ArtistJson artistJson,
                                                      @Nonnull MuseumJson museumJson) {
        PaintingJson nonexistentPainting = new PaintingJson(
                UUID.randomUUID(),
                randomMuseumTitle(),
                randomDescription(),
                ImgUtils.convertImageToBase64("img/painting_new.jpg"),
                museumJson,
                artistJson
        );

        Response<PaintingJson> response = gatewayApiClient.updatePainting(token, nonexistentPainting);

        step("Check updated painting in response", () -> {
            assertEquals(404, response.code(), "Expected HTTP status 404 but got " + response.code());
        });
    }
}