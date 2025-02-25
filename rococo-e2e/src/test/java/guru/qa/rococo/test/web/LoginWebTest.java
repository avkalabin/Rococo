package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.UserJson;
import guru.qa.rococo.page.MainPage;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
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
                .checkThatPageLoaded();
    }
}
