package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.cssValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class MainPage extends BasePage<MainPage>{

    public static final String URL = CFG.frontUrl();

    private final SelenideElement pageContent = $("#page-content");

    @Step("Check that main page is loaded")
    @Override
    @Nonnull
    public MainPage checkThatPageLoaded() {
        pageContent.shouldHave(text("Ваши любимые картины и художники всегда рядом"));
        return this;
    }

    @Step("Check that background content in dark mode")
    @Nonnull
    public MainPage checkThatPageInDarkMode() {
        pageContent.shouldHave(cssValue("color", "rgba(250, 248, 252, 1)"));
        return this;
    }

    @Step("Check that background content in light mode")
    @Nonnull
    public MainPage checkThatPageInLightMode() {
        pageContent.shouldHave(cssValue("color", "rgba(18, 11, 24, 1)"));
        return this;
    }
}
