package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class MuseumPage extends BasePage<MuseumPage> {

    private final SelenideElement pageContent = $("#page-content");

    @Step("Check that museum page is loaded")
    @Override
    @Nonnull
    public MuseumPage  checkThatPageLoaded() {
        pageContent.shouldHave(text("Художники"));
        return this;
    }
}
