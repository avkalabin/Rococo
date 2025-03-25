package guru.qa.rococo.test.rest;

import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.jupiter.annotation.Token;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.model.ArtistJson;
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
import static guru.qa.rococo.utils.RandomDataUtils.randomBiography;
import static guru.qa.rococo.utils.RandomDataUtils.randomUsername;
import static io.qameta.allure.Allure.step;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@RestTest
@Tag("rest")
@DisplayName("REST | Artist tests")
public class ArtistRestTest {

    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();

    GatewayApiClient gatewayApiClient = new GatewayApiClient();

    @Test
    @Artist
    @DisplayName("Should return artist")
    void shouldReturnArtist(@Nonnull ArtistJson artist) {
        Response<RestResponsePage<ArtistJson>> response = gatewayApiClient.getAllArtists(artist.name(), 0, 10);

        step("Check artist in response", () -> {
            assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
            assertNotNull(response.body(), "Response body should not be null");

            List<ArtistJson> artistList = response.body().getContent();
            assertFalse(artistList.isEmpty(), "Expected at least one artist in the response");
        });
    }

    @Test
    @Artist
    @DisplayName("Should return artist by ID")
    void shouldReturnArtistById(@Nonnull ArtistJson artist) {
        Response<ArtistJson> response = gatewayApiClient.getArtistById(artist.id());

        step("Check artist in response", () -> {
            assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
            assertNotNull(response.body(), "Response body should not be null");

            ArtistJson responseBody = response.body();
            assertEquals(artist.name(), responseBody.name(), "Artist name mismatch! Expected: '%s', Actual: '%s'");
        });
    }

    @Test
    @ApiLogin
    @User
    @DisplayName("Should create new artist")
    void shouldCreateNewArtist(@Token String token) {
        ArtistJson artist = new ArtistJson(
                null,
                randomUsername(),
                randomBiography(),
                ImgUtils.convertImageToBase64("img/artist.jpg")
        );

        Response<ArtistJson> response = gatewayApiClient.createArtist(token, artist);

        step("Check created artist in response", () -> {
            assertEquals(201, response.code(), "Expected HTTP status 201 but got " + response.code());
            assertNotNull(response.body(), "Response body should not be null");

            ArtistJson responseBody = response.body();
            check("expected id",
                    responseBody.id(), notNullValue());
            check("expected name",
                    responseBody.name(), equalTo(artist.name()));
            check("expected biography",
                    responseBody.biography(), equalTo(artist.biography()));
            check("expected photo",
                    responseBody.photo(), equalTo(artist.photo()));
        });
    }

    @Test
    @DisplayName("Should get unauthorized response when create new artist without login")
    void shouldGetUnauthorizedWhenCreateNewArtistWithoutLogin() {
        ArtistJson artist = new ArtistJson(
                null,
                randomUsername(),
                randomBiography(),
                ImgUtils.convertImageToBase64("img/artist.jpg")
        );

        Response<ArtistJson> response = gatewayApiClient.createArtist("token", artist);

        step("Check response status code", () -> {
            assertEquals(401, response.code(), "Expected HTTP status 401 but got " + response.code());
        });
    }

    @Test
    @Artist
    @ApiLogin
    @User
    @DisplayName("Should update artist")
    void shouldUpdateArtist(@Token String token, @Nonnull ArtistJson artist) {
        ArtistJson updatedArtist = new ArtistJson(
                artist.id(),
                randomUsername(),
                randomBiography(),
                ImgUtils.convertImageToBase64("img/artist_new.jpg"));

        Response<ArtistJson> response = gatewayApiClient.updateArtist(token, updatedArtist);

        step("Check updated artist in response", () -> {
            assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
            assertNotNull(response.body(), "Response body should not be null");

            ArtistJson responseBody = response.body();
            check("expected id",
                    responseBody.id(), equalTo(updatedArtist.id()));
            check("expected name",
                    responseBody.name(), equalTo(updatedArtist.name()));
            check("expected biography",
                    responseBody.biography(), equalTo(updatedArtist.biography()));
            check("expected photo",
                    responseBody.photo(), equalTo(updatedArtist.photo()));
        });
    }

    @Test
    @ApiLogin
    @User
    @DisplayName("Should get NOT_FOUND response when update nonexistent artist")
    void shouldGetNotFoundWhenUpdateNonexistentArtist(@Token String token) {
        ArtistJson nonexistentArtist = new ArtistJson(
                UUID.randomUUID(),
                randomUsername(),
                randomBiography(),
                ImgUtils.convertImageToBase64("img/artist_new.jpg"));

        Response<ArtistJson> response = gatewayApiClient.updateArtist(token, nonexistentArtist);

        step("Check updated artist in response", () -> {
            assertEquals(404, response.code(), "Expected HTTP status 404 but got " + response.code());
        });
    }
}