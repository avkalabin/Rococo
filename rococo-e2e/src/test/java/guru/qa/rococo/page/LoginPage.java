package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.component.Input;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {

    public static final String URL = CFG.authUrl() + "login";

    private final Input usernameInput = new Input($("[name='username']"));
    private final Input passwordInput = new Input($("[name='password']"));
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement registerButton = $("a[href='/register']");
    private final SelenideElement errorContainer = $(".form__error");

    @Step("Do register")
    @Nonnull
    public RegisterPage doRegister() {
        registerButton.click();
        return new RegisterPage();
    }

    @Step("Fill login page with credentials: username: {0}, password: {1}")
    @Nonnull
    public LoginPage fillLoginPage(String login, String password) {
        setUsername(login);
        setPassword(password);
        return this;
    }

    @Step("Set username: {0}")
    @Nonnull
    public LoginPage setUsername(String username) {
        usernameInput.getSelf().setValue(username);
        return this;
    }

    @Step("Set password: {0}")
    @Nonnull
    public LoginPage setPassword(String password) {
        passwordInput.getSelf().setValue(password);
        return this;
    }

    @Step("Submit login")
    @Nonnull
    public <T extends BasePage<?>> T submit(T expectedPage) {
        submitButton.click();
        return expectedPage;
    }

    @Step("Check error on page: {error}")
    @Nonnull
    public LoginPage checkError(String error) {
        errorContainer.shouldHave(text(error));
        return this;
    }

    @Step("Check that page is loaded")
    @Override
    @Nonnull
    public LoginPage checkThatPageLoaded() {
        usernameInput.getSelf().should(visible);
        passwordInput.getSelf().should(visible);
        return this;
    }
}
