package guru.qa.rococo.test.rest;

import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Token;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.model.UserJson;
import guru.qa.rococo.service.impl.GatewayApiClient;
import guru.qa.rococo.utils.ImgUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.Response;

import javax.annotation.Nonnull;

import static guru.qa.rococo.utils.RandomDataUtils.generateRandomWord;
import static guru.qa.rococo.utils.RandomDataUtils.randomUsername;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

@RestTest
@Tag("rest")
@DisplayName("REST | Userdata tests")
public class UserdataRestTest {

    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();

    GatewayApiClient gatewayApiClient = new GatewayApiClient();

    @Test
    @ApiLogin
    @User
    @DisplayName("Should return user")
    void shouldReturnUser(@Token String token, @Nonnull UserJson user) {

        Response<UserJson> response = gatewayApiClient.getUser(token);

        step("Check user in response", () -> {
            assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
            UserJson responseBody = response.body();
            assertNotNull(responseBody, "Response body should not be null");

            assertAll(
                    () -> assertEquals(user.id(), responseBody.id(),
                            String.format("User id mismatch! Expected: '%s', Actual: '%s'", user.id(), responseBody.id())),
                    () -> assertEquals(user.username(), responseBody.username(),
                            String.format("User name mismatch! Expected: '%s', Actual: '%s'", user.username(), responseBody.username())));
        });
    }

    @Test
    @ApiLogin
    @User
    @DisplayName("Should update user")
    void shouldUpdateUser(@Token String token, @Nonnull UserJson user) {

        UserJson updatedUser = new UserJson(
                user.id(),
                randomUsername(),
                generateRandomWord(10),
                generateRandomWord(15),
                ImgUtils.convertImageToBase64("img/user.jpg"),
                null
        );

        Response<UserJson> response = gatewayApiClient.updateUser(token, updatedUser);
        step("Check user in response", () -> {
            assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
            UserJson responseBody = response.body();
            assertNotNull(responseBody, "Response body should not be null");

            assertAll(
                    () -> assertEquals(user.id(), responseBody.id(),
                            String.format("User id mismatch! Expected: '%s', Actual: '%s'", user.id(), responseBody.id())),
                    () -> assertEquals(updatedUser.username(), responseBody.username(),
                            String.format("User name mismatch! Expected: '%s', Actual: '%s'", updatedUser.username(), responseBody.username())),
                    () -> assertEquals(updatedUser.firstname(), responseBody.firstname(),
                            String.format("User firstname mismatch! Expected: '%s', Actual: '%s'", updatedUser.firstname(), responseBody.firstname())),
                    () -> assertEquals(updatedUser.lastname(), responseBody.lastname(),
                            String.format("User lastname mismatch! Expected: '%s', Actual: '%s'", updatedUser.lastname(), responseBody.lastname())),
                    () -> assertEquals(updatedUser.avatar(), responseBody.avatar(),
                            String.format("User avatar mismatch! Expected: '%s', Actual: '%s'", updatedUser.avatar(), responseBody.avatar()))
            );
        });
    }
}
