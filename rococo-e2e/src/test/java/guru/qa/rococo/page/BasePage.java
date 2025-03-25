package guru.qa.rococo.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.page.artist.ArtistModal;
import guru.qa.rococo.page.component.Header;
import guru.qa.rococo.page.component.SearchField;
import guru.qa.rococo.page.painting.PaintingDetailPage;
import guru.qa.rococo.page.painting.PaintingModal;
import io.qameta.allure.Step;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byTagAndText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;

@ParametersAreNonnullByDefault
@SuppressWarnings("unchecked")
public abstract class BasePage<T extends BasePage<?>> {

    protected static final Config CFG = Config.getInstance();

    @Getter
    protected final Header header = new Header();
    @Getter
    protected final SearchField searchField = new SearchField();

    private final SelenideElement alert = $(".toast");
    private final SelenideElement submitFormButton = $("button[type=submit]");
    protected final SelenideElement pageContent = $("#page-content");
    protected final SelenideElement modalPage = $("[data-testid=modal-component]");
    protected final SelenideElement addPaintingButton = $(".ml-4");
    private final SelenideElement addNewArtistBtn = $(".btn");

    public abstract T checkThatPageLoaded();

    @Step("Success submit modal form")
    public void successSubmitModal() {
        sleep(1500);
        submitFormButton.click();
        modalPage.shouldNotBe(visible);
    }

    @Step("Submit form with error")
    public T errorSubmit() {
        sleep(1000);
        submitFormButton.click();
        return (T) this;
    }

    @Step("Check that alert message appears: {expectedText}")
    @Nonnull
    public T checkAlertMessage(String expectedText) {
        alert.should(visible).should(text(expectedText));
        return (T) this;
    }


    public void scrollToElement(String option, ElementsCollection options) {

        int initialOptionsCount = options.size();

        while (true) {
            SelenideElement requiredOption = options.find(text(option));
            if (requiredOption.exists()) {
                requiredOption.click();
                return;
            }

            options.last().scrollIntoView(true).click();
            sleep(2000);
            options.shouldHave(sizeGreaterThan(initialOptionsCount)
                    .because("Option with text: '" + option + "' not found. " +
                            "Timed out waiting for new options to be loaded. Current options count:" + options.size()));
            initialOptionsCount = options.size();
        }
    }

    @Step("Add new painting")
    public PaintingModal addNewPainting() {
        addPaintingButton.click();
        return new PaintingModal();
    }


    @Step("Add new artist")
    public ArtistModal addNewArtist() {
        addNewArtistBtn.click();
        return new ArtistModal();
    }

    @Step("Open painting card with title: {title}")
    public PaintingDetailPage openPaintingCard(String title) {
        $(byTagAndText("div", title)).click();
        return new PaintingDetailPage();
    }
}