package guru.qa.rococo.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.page.component.Header;
import guru.qa.rococo.page.component.SearchField;
import io.qameta.allure.Step;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
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
//    private final ElementsCollection formErrors = $$("p.Mui-error, .input__helper-text");

    public abstract T checkThatPageLoaded();

    @Step("Success submit modal form")
    public void successSubmitModal() {
        submitFormButton.click();
        modalPage.shouldNotBe(visible);
    }

    @Step("Submit form with error")
    @SuppressWarnings("unchecked")
    public T errorSubmit() {
        submitFormButton.click();
        return (T) this;
    }

    @Step("Check that alert message appears: {expectedText}")
    @SuppressWarnings("unchecked")
    @Nonnull
    public T checkAlertMessage(String expectedText) {
        alert.should(Condition.visible).should(Condition.text(expectedText));
        return (T) this;
    }
//
//    @Step("Check that form error message appears: {expectedText}")
//    @SuppressWarnings("unchecked")
//    @Nonnull
//    public T checkFormErrorMessage(String... expectedText) {
//        formErrors.should(CollectionCondition.textsInAnyOrder(expectedText));
//        return (T) this;
//    }
}