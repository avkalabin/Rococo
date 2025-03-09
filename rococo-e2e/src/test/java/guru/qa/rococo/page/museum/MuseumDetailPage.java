package guru.qa.rococo.page.museum;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.BasePage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MuseumDetailPage extends BasePage<MuseumDetailPage> {

    private static final String PAGE_URL = CFG.frontUrl() + "museum";
    private final String museumId;

    private final SelenideElement title = $(".card-header");
    private final SelenideElement geo = $(".w-56").preceding(0);
    private final SelenideElement description = $(".w-56").sibling(0);
    private final SelenideElement photo = $(".my-4");
    private final SelenideElement editMuseumBtn = $("[data-testid=edit-museum]");

    public MuseumDetailPage(String museumId) {
        this.museumId = museumId;
    }

    public MuseumDetailPage() {
        this.museumId = "";
    }

    protected String getPageUrl() {
        return PAGE_URL + "/" + this.museumId;
    }

    @Step("Open museum detail page")
    public MuseumDetailPage openPage() {
        Selenide.open(getPageUrl());
        return this;
    }

    @Override
    @Step("Check that museum detail page is loaded")
    public MuseumDetailPage checkThatPageLoaded() {
        title.shouldBe(visible);
        description.shouldBe(visible);
        photo.shouldBe(visible);
        return this;
    }

    @Step("Edit museum")
    public MuseumModal editMuseum() {
        editMuseumBtn.click();
        return new MuseumModal();
    }
}
