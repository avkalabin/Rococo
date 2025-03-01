package guru.qa.rococo.page.museum;

import guru.qa.rococo.page.BasePage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;

public class MuseumPage extends BasePage<MuseumPage> {

    public static final String URL = CFG.authUrl() + "museum";

    @Step("Check that museum page is loaded")
    @Override
    @Nonnull
    public MuseumPage checkThatPageLoaded() {
        pageContent.shouldHave(text("Художники"));
        return this;
    }
}
