package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.page.MainPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
@DisplayName("Navigation web tests")
public class NavigateWebTest {

    @DisplayName("General navigation test")
    @Test
    public void generalNavigationTest() {
        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .toPaintingPage()
                .checkThatPageLoaded()
                .getHeader()
                .toArtistPage()
                .checkThatPageLoaded()
                .getHeader()
                .toMuseumPage()
                .checkThatPageLoaded()
                .getHeader()
                .toMainPage()
                .checkThatPageLoaded();
    }

    @DisplayName("Toggle dark mode test")
    @Test
    public void toggleDarkModeTest() {
        Selenide.open(MainPage.URL, MainPage.class)
                .checkThatPageInLightMode()
                .getHeader()
                .checkLogoOnLightMode()
                .getHeader()
                .switchLight()
                .checkLogoOnDarkMode()
                .checkThatPageInDarkMode();
    }

}
