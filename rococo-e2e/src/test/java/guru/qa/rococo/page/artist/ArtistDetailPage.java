package guru.qa.rococo.page.artist;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.BasePage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ArtistDetailPage extends BasePage<ArtistDetailPage> {
    private static final String PAGE_URL = CFG.frontUrl() + "artist";
    private final String artistId;

    private final SelenideElement name = $(".card-header");
    private final SelenideElement biography = $(".col-span-2");
    private final SelenideElement photo = $("[data-testid=avatar]");
    private final SelenideElement editArtistBtn = $("[data-testid=edit-artist]");


    public ArtistDetailPage(String artistId) {
        this.artistId = artistId;
    }

    public ArtistDetailPage() {
        this.artistId = "";
    }

    protected String getPageUrl() {
        return PAGE_URL + "/" + this.artistId;
    }

    @Step("Open artist detail page")
    public ArtistDetailPage openPage() {
        Selenide.open(getPageUrl());
        return this;
    }

    @Override
    @Step("Check that artist detail page is loaded")
    public ArtistDetailPage checkThatPageLoaded() {
        name.shouldBe(visible);
        biography.shouldBe(visible);
        photo.shouldBe(visible);
        return this;
    }

    @Step("Edit artist")
    public ArtistModal editArtist() {
        editArtistBtn.click();
        return new ArtistModal();
    }




}
