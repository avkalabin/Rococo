package guru.qa.rococo.page.painting;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.BasePage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class PaintingPage extends BasePage<PaintingPage> {

    public static final String URL = CFG.authUrl() + "painting";

    private final SelenideElement pageContent = $("#page-content");
    private final SelenideElement paintingList = $(".grid-cols-1");

    @Step("Check that painting page is loaded")
    @Override
    @Nonnull
    public PaintingPage checkThatPageLoaded() {
        pageContent.shouldHave(text("Картины"));
        return this;
    }

    @Step("Painting list should have {size} painting")
    public PaintingPage checkPaintingListSize(int size) {
        paintingList.$$("li").shouldHave(size(size));
        return this;
    }

    @Step("Check that painting list have {text}")
    public PaintingPage checkPaintingListHaveText(String text) {
        paintingList.shouldHave(text(text));
        return this;
    }
}
