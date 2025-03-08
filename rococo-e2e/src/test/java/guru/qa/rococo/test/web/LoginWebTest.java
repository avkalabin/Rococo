package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.UserJson;
import guru.qa.rococo.page.LoginPage;
import guru.qa.rococo.page.MainPage;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.rococo.utils.RandomDataUtils.randomUsername;

@WebTest
@DisplayName("Login web test")
public class LoginWebTest {

    @User
    @Test
    @DisplayName("Main page should be displayed after successful login")
    void mainPageShouldBeDisplayedAfterSuccessLogin(@NotNull UserJson user) {
        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .toLoginPage()
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .checkThatPageLoaded()
                .getHeader()
                .checkUserIsAuthorized();
    }

    @Test
    @DisplayName("User should stay on login page after login with bad credentials")
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .toLoginPage()
                .fillLoginPage(randomUsername(), "BAD")
                .submit(new LoginPage())
                .checkError("Неверные учетные данные пользователя");
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("User should be able to logout")
    void shouldBeAbleToLogout() {
        Selenide.open(MainPage.URL, MainPage.class)
                .checkThatPageLoaded()
                .getHeader()
                .openProfileModal()
                .checkThatPageLoaded()
                .logout()
                .checkAlertMessage("Сессия завершена")
                .getHeader()
                .checkUserIsNotAuthorized();
    }
}
