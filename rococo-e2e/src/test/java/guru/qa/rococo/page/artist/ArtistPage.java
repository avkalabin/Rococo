package guru.qa.rococo.page.artist;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.BasePage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byTagAndText;
import static com.codeborne.selenide.Selenide.$;

public class ArtistPage extends BasePage<ArtistPage> {

    public static final String URL = CFG.authUrl() + "artist";

    private final SelenideElement pageContent = $("#page-content");
    private final SelenideElement artistList = $(".grid-cols-1");

    @Step("Check that artist page is loaded")
    @Override
    @Nonnull
    public ArtistPage checkThatPageLoaded() {
        pageContent.shouldHave(text("Художники"));
        return this;
    }

    @Step("Check that artist list have {text}")
    public ArtistPage checkArtistListHaveText(String text) {
        artistList.shouldHave(text(text));
        return this;
    }

    @Step("Open artist card with name: {artistName}")
    public ArtistDetailPage openArtist(String artistName) {
        $(byTagAndText("span", artistName)).click();
        return new ArtistDetailPage();
    }

    @Step("Artist list should have {size} artist")
    public ArtistPage checkArtistListSize(int size) {
        artistList.$$("li").shouldHave(size(size));
        return this;
    }



}
