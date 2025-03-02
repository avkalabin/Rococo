package guru.qa.rococo.page.artist;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.BasePage;
import guru.qa.rococo.page.component.Input;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PaintingModal extends BasePage<PaintingModal> {

    private final Input titleInput = new Input($("[name=title]"));
    private final SelenideElement photoInput = $("[name=content]");
    private final SelenideElement artistSelect = $("[name=authorId]");
    private final Input descriptionInput = new Input($("[name=description]"));
    private final SelenideElement museumSelect = ($("[name=museumId]"));
    private final ElementsCollection options = $$("option");


    @Step("Check that painting modal page is loaded")
    @Override
    public PaintingModal checkThatPageLoaded() {
        modalPage.shouldHave(text("Название картины"));
        return this;
    }

    @Step("Set painting title: {title}")
    public PaintingModal setTitle(String title) {
        titleInput.getSelf().setValue(title);
        return this;
    }

    @Step("Set painting photo")
    public PaintingModal setPhoto(String filepath) {
        photoInput.uploadFromClasspath(filepath);
        return this;
    }


    @Step("Select painting's artist: {artist}")
    public PaintingModal selectArtist(String artist) {
        artistSelect.selectOption(artist);
        return this;
    }

    @Step("Set painting description: {description}")
    public PaintingModal setDescription(String description) {
        descriptionInput.getSelf().setValue(description);
        return this;
    }

    @Step("Select painting's museum: {museum}")
    public PaintingModal selectMuseum(String museum) {
        scrollToElement(museum, options);
        museumSelect.selectOption(museum);
        return this;
    }
}
