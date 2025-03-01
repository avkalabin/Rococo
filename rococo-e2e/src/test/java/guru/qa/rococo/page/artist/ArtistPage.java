package guru.qa.rococo.page.artist;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.BasePage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byTagAndText;
import static com.codeborne.selenide.Selenide.$;

public class ArtistPage extends BasePage<ArtistPage> {

    public static final String URL = CFG.authUrl() + "artist";

    private final SelenideElement pageContent = $("#page-content");
    private final SelenideElement addNewArtistBtn = $(".btn");
    private final SelenideElement artistList = $(".grid-cols-1");


    @Step("Check that artist page is loaded")
    @Override
    @Nonnull
    public ArtistPage checkThatPageLoaded() {
        pageContent.shouldHave(text("Художники"));
        return this;
    }

    @Step("Add new artist")
    public ArtistModal addNewArtist() {
        addNewArtistBtn.click();
        return new ArtistModal();
    }

    @Step("Check that artist {name} is exist")
    public ArtistPage checkArtistIsExist(String name) {
        artistList.shouldHave(text(name));
        return this;
    }

    @Step("Click on artist")
    public ArtistDetailPage clickOnArtist(String artistName) {
        $(byTagAndText("span", artistName)).click();
        return new ArtistDetailPage();
    }

}
