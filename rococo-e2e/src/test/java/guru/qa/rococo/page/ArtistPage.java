package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class ArtistPage extends BasePage<ArtistPage> {

    private final SelenideElement pageContent = $("#page-content");

    @Step("Check that artist page is loaded")
    @Override
    @Nonnull
    public ArtistPage checkThatPageLoaded() {
        pageContent.shouldHave(text("Художники"));
        return this;
    }

}
