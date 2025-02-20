package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class MainPage extends BasePage<MainPage>{

    public static final String URL = CFG.frontUrl();

    private final SelenideElement pageContent = $("#page-content");

    public void checkPageContentContainsText(String text) {
        pageContent.shouldHave(text(text));

    }

    @Step("Check that page is loaded")
    @Override
    @Nonnull
    public MainPage checkThatPageLoaded() {
        pageContent.shouldHave(text("Ваши любимые картины и художники всегда рядом"));
        return this;
    }
}
