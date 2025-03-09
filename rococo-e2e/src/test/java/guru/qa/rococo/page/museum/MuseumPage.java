package guru.qa.rococo.page.museum;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.BasePage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byTagAndText;
import static com.codeborne.selenide.Selenide.$;

public class MuseumPage extends BasePage<MuseumPage> {

    public static final String URL = CFG.authUrl() + "museum";

    private final SelenideElement addNewMuseumBtn = $(".btn");
    private final SelenideElement museumList = $(".grid-cols-1");

    @Step("Check that museum page is loaded")
    @Override
    @Nonnull
    public MuseumPage checkThatPageLoaded() {
        pageContent.shouldHave(text("Музеи"));
        return this;
    }

    @Step("Add new museum")
    public MuseumModal addNewMuseum() {
        addNewMuseumBtn.click();
        return new MuseumModal();
    }

    @Step("Check that museum is exist with title: {title}, city: {city}, country: {country}")
    public MuseumPage checkMuseumIsExist(String title, String city, String country) {
        $(byTagAndText("div", title)).sibling(0).shouldHave((text(city + ", " + country)));
        return this;
    }

    @Step("Museum list should have {size} museum")
    public MuseumPage checkArtistListSize(int size) {
        museumList.$$("li").shouldHave(size(size));
        return this;
    }

    @Step("Check that painting list have {text}")
    public MuseumPage checkMuseumListHaveText(String text) {
        museumList.shouldHave(text(text));
        return this;
    }
}
