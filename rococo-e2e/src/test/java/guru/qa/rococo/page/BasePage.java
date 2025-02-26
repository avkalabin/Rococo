package guru.qa.rococo.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.page.component.Header;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {

    protected static final Config CFG = Config.getInstance();

    protected final Header header = new Header();

  private final SelenideElement alert = $(".toast");
//    private final ElementsCollection formErrors = $$("p.Mui-error, .input__helper-text");

    public Header getHeader() {
        return header;
    }

    public abstract T checkThatPageLoaded();



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