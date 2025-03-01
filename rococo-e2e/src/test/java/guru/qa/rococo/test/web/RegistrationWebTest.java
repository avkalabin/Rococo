package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.UserJson;
import guru.qa.rococo.page.MainPage;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.rococo.utils.RandomDataUtils.*;

@WebTest
@DisplayName("Registration web test")
public class RegistrationWebTest {

    @Test
    @DisplayName("User can successfully register")
    void shouldRegisterNewUser() {
        String newUsername = randomUsername();
        String password = generateRandomPassword();
        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .toLoginPage()
                .doRegister()
                .fillRegisterPage(newUsername, password, password)
                .successSubmit()
                .getHeader()
                .toLoginPage()
                .fillLoginPage(newUsername, password)
                .submit(new MainPage())
                .checkThatPageLoaded();
    }

    @Test
    @User
    @DisplayName("User cannot register with existing username")
    void shouldNotRegisterUserWithExistingUsername(@NotNull UserJson user) {

        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .toLoginPage()
                .doRegister()
                .fillRegisterPage(user.username(), user.testData().password(), user.testData().password())
                .errorSubmit()
                .checkAlertMessage("Username `" + user.username() + "` already exists");
    }

    @Test
    @DisplayName("User cannot register if password and confirm password are not equal")
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        String newUsername = randomUsername();
        String password = generateRandomPassword();

        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .toLoginPage()
                .doRegister()
                .fillRegisterPage(newUsername, password, "bad password submit")
                .errorSubmit()
                .checkAlertMessage("Passwords should be equal");
    }

    @Test
    @DisplayName("User cannot register if username is large than maximal characters count")
    void shouldShowErrorIfUsernameIsLargeThanMaximalCharactersCount() {
        String longWord = generateRandomWord(51);
        String password = generateRandomPassword();

        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .toLoginPage()
                .doRegister()
                .fillRegisterPage(longWord, password, password)
                .errorSubmit()
                .checkAlertMessage("Allowed username length should be from 3 to 50 characters");
    }

    @Test
    @DisplayName("User cannot register if password is less than minimal characters count")
    void shouldShowErrorIfPasswordIsLessThanMinimalCharactersCount() {
        String username = randomUsername();
        String password = generateRandomPassword(1, 2);

        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .toLoginPage()
                .doRegister()
                .fillRegisterPage(username, password, password)
                .errorSubmit()
                .checkAlertMessage("Allowed password length should be from 3 to 12 characters");
    }

    @Test
    @DisplayName("User cannot register if password is more than max characters count")
    void shouldShowErrorIfPasswordIsMoreThanMinimalCharactersCount() {
        String username = randomUsername();
        String password = generateRandomPassword(13, 14);

        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .toLoginPage()
                .doRegister()
                .fillRegisterPage(username, password, password)
                .errorSubmit()
                .checkAlertMessage("Allowed password length should be from 3 to 12 characters");
    }


}
