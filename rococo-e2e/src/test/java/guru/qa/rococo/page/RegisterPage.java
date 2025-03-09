package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.component.Input;
import io.qameta.allure.Step;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage extends BasePage<RegisterPage> {

    public static final String URL = CFG.authUrl() + "register";

    private final Input usernameInput = new Input($("[name='username']"));
    private final Input passwordInput = new Input($("[name='password']"));
    private final Input passwordSubmitInput = new Input($("input[name='passwordSubmit']"));
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement proceedLoginButton = $(".form__submit");
    private final SelenideElement errorContainer = $(".form__error");

    @Step("Fill register page with credentials: username: {0}, password: {1}, submit password: {2}")
    @Nonnull
    public RegisterPage fillRegisterPage(String login, String password, String passwordSubmit) {
        setUsername(login);
        setPassword(password);
        setPasswordSubmit(passwordSubmit);
        return this;
    }

    @Step("Set username: {0}")
    @Nonnull
    public RegisterPage setUsername(String username) {
        usernameInput.getSelf().setValue(username);
        return this;
    }

    @Step("Set password: {0}")
    @Nonnull
    public RegisterPage setPassword(String password) {
        passwordInput.getSelf().setValue(password);
        return this;
    }

    @Step("Confirm password: {0}")
    @Nonnull
    public RegisterPage setPasswordSubmit(String password) {
        passwordSubmitInput.getSelf().setValue(password);
        return this;
    }

    @Step("Submit register")
    @Nonnull
    public MainPage successSubmit() {
        submitButton.click();
        proceedLoginButton.click();
        return new MainPage();
    }

    @Step("Check that page is loaded")
    @Override
    @Nonnull
    public RegisterPage checkThatPageLoaded() {
        usernameInput.getSelf().should(visible);
        passwordInput.getSelf().should(visible);
        passwordSubmitInput.getSelf().should(visible);
        return this;
    }

    @Nonnull
    public RegisterPage checkAlertMessage(@NotNull String errorMessage) {
        errorContainer.shouldHave(text(errorMessage));
        return this;
    }
}
