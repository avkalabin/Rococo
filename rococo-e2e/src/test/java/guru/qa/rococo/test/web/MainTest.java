package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.page.MainPage;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.sleep;

@WebTest()
public class MainTest {

    private static final Config CFG = Config.getInstance();

    @ApiLogin(username = "qwe", password = "111")
    @Test
    void mainPageShouldBeOpened() {

        Selenide.open(CFG.frontUrl(), MainPage.class)
                .checkThatPageLoaded();
        sleep(20000);
    }
}
