package guru.qa.rococo.page.painting;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.BasePage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class PaintingDetailPage extends BasePage<PaintingDetailPage> {

    private static final String PAGE_URL = CFG.frontUrl() + "painting";
    private final String paintingId;
    private final SelenideElement title = $(".card-header");
    private final SelenideElement photo = $(".my-4");
    private final SelenideElement editPaintingButton = $("[data-testid=edit-painting]");

    public PaintingDetailPage(String paintingId) {
        this.paintingId = paintingId;
    }

    public PaintingDetailPage() {
        paintingId = "";
    }

    protected String getPageUrl() {
        return PAGE_URL + "/" + paintingId;
    }

    @Step("Open painting detail page")
    public PaintingDetailPage openPage() {
        Selenide.open(getPageUrl());
        return this;
    }

    @Step("Check that painting detail page is loaded")
    @Override
    public PaintingDetailPage checkThatPageLoaded() {
        title.shouldBe(visible);
        photo.shouldBe(visible);
        return this;
    }

    @Step("Edit painting")
    public PaintingModal editPainting() {
        editPaintingButton.click();
        return new PaintingModal();
    }
}
