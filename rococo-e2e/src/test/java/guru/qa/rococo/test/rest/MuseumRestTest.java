package guru.qa.rococo.test.rest;

import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.jupiter.annotation.Token;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.model.GeoJson;
import guru.qa.rococo.model.MuseumJson;
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
import static guru.qa.rococo.utils.RandomDataUtils.*;
import static io.qameta.allure.Allure.step;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@RestTest
@Tag("rest")
@DisplayName("REST | Museum tests")
public class MuseumRestTest {

    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();

    GatewayApiClient gatewayApiClient = new GatewayApiClient();

    @Test
    @Museum
    @DisplayName("Should return museum")
    void shouldReturnMuseum(@Nonnull MuseumJson museum) {
        Response<RestResponsePage<MuseumJson>> response = gatewayApiClient.getAllMuseums(museum.title(), 0, 10);

        step("Check museum in response", () -> {
            assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
            assertNotNull(response.body(), "Response body should not be null");

            List<MuseumJson> museumList = response.body().getContent();
            assertFalse(museumList.isEmpty(), "Expected at least one museum in the response");
        });
    }

    @Test
    @Museum
    @DisplayName("Should return museum by ID")
    void shouldReturnMuseumById(@Nonnull MuseumJson museum) {
        Response<MuseumJson> response = gatewayApiClient.getMuseumById(museum.id());

        step("Check museum in response", () -> {
            assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
            assertNotNull(response.body(), "Response body should not be null");

            MuseumJson responseBody = response.body();
            assertEquals(museum.title(), responseBody.title(), "Museum title mismatch! Expected: '%s', Actual: '%s'");
        });
    }

    @Test
    @ApiLogin
    @User
    @DisplayName("Should create new museum")
    void shouldCreateNewMuseum(@Token String token) {
        MuseumJson museum = new MuseumJson(
                null,
                randomMuseumTitle(),
                randomDescription(),
                ImgUtils.convertImageToBase64("img/museum.jpg"),
                new GeoJson(
                        randomCity(),
                        randomCountry())
        );

        Response<MuseumJson> response = gatewayApiClient.createMuseum(token, museum);

        step("Check created museum in response", () -> {
            assertEquals(201, response.code(), "Expected HTTP status 201 but got " + response.code());
            assertNotNull(response.body(), "Response body should not be null");

            MuseumJson responseBody = response.body();
            check("expected id",
                    responseBody.id(), notNullValue());
            check("expected title",
                    responseBody.title(), equalTo(museum.title()));
            check("expected description",
                    responseBody.description(), equalTo(museum.description()));
            check("expected photo",
                    responseBody.photo(), equalTo(museum.photo()));
            check("expected geo",
                    responseBody.geo(), equalTo(museum.geo()));
        });
    }

    @Test
    @DisplayName("Should get unauthorized response when create new museum without login")
    void shouldGetUnauthorizedWhenCreateNewMuseumWithoutLogin() {
        MuseumJson museum = new MuseumJson(
                null,
                randomMuseumTitle(),
                randomDescription(),
                ImgUtils.convertImageToBase64("img/museum.jpg"),
                new GeoJson(
                        randomCity(),
                        randomCountry())
        );

        Response<MuseumJson> response = gatewayApiClient.createMuseum("token", museum);

        step("Check response status code", () -> {
            assertEquals(401, response.code(), "Expected HTTP status 401 but got " + response.code());
        });
    }

    @Test
    @Museum
    @ApiLogin
    @User
    @DisplayName("Should update museum")
    void shouldUpdateMuseum(@Token String token, MuseumJson museum) {
        MuseumJson updatedMuseum = new MuseumJson(
                museum.id(),
                randomMuseumTitle(),
                randomDescription(),
                ImgUtils.convertImageToBase64("img/museum.jpg"),
                new GeoJson(
                        randomCity(),
                        randomCountry()));

        Response<MuseumJson> response = gatewayApiClient.updateMuseum(token, updatedMuseum);

        step("Check updated museum in response", () -> {
            assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
            assertNotNull(response.body(), "Response body should not be null");

            MuseumJson responseBody = response.body();
            check("expected id",
                    responseBody.id(), equalTo(updatedMuseum.id()));
            check("expected title",
                    responseBody.title(), equalTo(updatedMuseum.title()));
            check("expected description",
                    responseBody.description(), equalTo(updatedMuseum.description()));
            check("expected photo",
                    responseBody.photo(), equalTo(updatedMuseum.photo()));
        });
    }

    @Test
    @ApiLogin
    @User
    @DisplayName("Should get NOT_FOUND response when update nonexistent museum")
    void shouldGetNotFoundWhenUpdateNonexistentMuseum(@Token String token) {
        MuseumJson nonexistentMuseum = new MuseumJson(
                UUID.randomUUID(),
                randomMuseumTitle(),
                randomDescription(),
                ImgUtils.convertImageToBase64("img/museum.jpg"),
                new GeoJson(
                        randomCity(),
                        randomCountry()));

        Response<MuseumJson> response = gatewayApiClient.updateMuseum(token, nonexistentMuseum);

        step("Check updated museum in response", () -> {
            assertEquals(404, response.code(), "Expected HTTP status 404 but got " + response.code());
        });
    }
}