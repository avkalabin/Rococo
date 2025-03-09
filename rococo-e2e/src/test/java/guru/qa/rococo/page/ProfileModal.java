package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.component.Input;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byTagAndText;
import static com.codeborne.selenide.Selenide.$;

public class ProfileModal extends BasePage<ProfileModal> {

    private final SelenideElement pageTitle = $(byTagAndText("header", "Профиль"));
    private final SelenideElement logoutBtn = $(byTagAndText("button", "Выйти"));
    private final SelenideElement username = $(".modal-form").$("h4");
    private final Input firstnameInput = new Input($("[name='firstname']"));
    private final Input surnameInput = new Input($("[name='surname']"));
    private final SelenideElement avatar = $("[name=content]");

    @Step("Check that profile modal loaded")
    @Override
    public ProfileModal checkThatPageLoaded() {
        pageTitle.shouldBe(visible);
        return this;
    }

    @Step("Logout")
    public MainPage logout() {
        logoutBtn.click();
        return new MainPage();
    }

    @Step("Check username is: {username}")
    public ProfileModal checkUsername(String username) {
        this.username.shouldHave(text("@" + username));
        return this;
    }

    @Step("Set first name: {firstName}")
    public ProfileModal setFirstname(String firstname) {
        firstnameInput.getSelf().setValue(firstname);
        return this;
    }

    @Step("Set surname: {surname}")
    public ProfileModal setSurname(String surname) {
        surnameInput.getSelf().setValue(surname);
        return this;
    }

    @Step("Set avatar photo")
    public ProfileModal setAvatar(String filepath) {
        avatar.uploadFromClasspath(filepath);
        return this;
    }

    @Step("Check first name error is: {error}")
    public ProfileModal checkFirstnameError(String error) {
        firstnameInput.checkError(error);
        return this;
    }

    @Step("Check surname error is: {error}")
    public ProfileModal checkSurnameError(String error) {
        surnameInput.checkError(error);
        return this;
    }
}
