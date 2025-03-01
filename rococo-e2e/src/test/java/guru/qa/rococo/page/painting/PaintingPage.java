package guru.qa.rococo.page.painting;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.BasePage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class PaintingPage extends BasePage<PaintingPage> {

    public static final String URL = CFG.authUrl() + "painting";

    private final SelenideElement pageContent = $("#page-content");

    @Step("Check that painting page is loaded")
    @Override
    @Nonnull
    public PaintingPage  checkThatPageLoaded() {
        pageContent.shouldHave(text("Картины"));
        return this;
    }
}
