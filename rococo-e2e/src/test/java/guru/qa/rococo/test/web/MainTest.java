package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.page.MainPage;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.sleep;

public class MainTest {

    private static final Config CFG = Config.getInstance();

    @Test
    void mainPageShouldBeOpened() {

        Selenide.open(CFG.frontUrl(), MainPage.class)
                .checkPageContentContainsText("Ваши любимые картины и художники всегда рядом");
        sleep(20000);
    }
}
