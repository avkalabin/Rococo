package guru.qa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.*;
import guru.qa.rococo.page.ProfileModal;
import guru.qa.rococo.page.artist.ArtistPage;
import guru.qa.rococo.page.museum.MuseumPage;
import guru.qa.rococo.page.painting.PaintingPage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class Header extends BaseComponent<Header> {

    public Header() {
        super($("#shell-header"));
    }

    private final SelenideElement mainPageLink = self.$("a[href*='/']");
    private final SelenideElement paintingPageLink = self.$("a[href*='/painting']");
    private final SelenideElement artistPageLink = self.$("a[href*='/artist']");
    private final SelenideElement museumPageLink = self.$("a[href*='/museum']");
    private final SelenideElement lightSwitch = self.$(".lightswitch-thumb");
    private final SelenideElement loginBtn = self.$(".btn");
    private final SelenideElement avatar = $("[data-testid=avatar]");
    private final SelenideElement cocoLogo = self.$(".text-primary-500");


    @Step("Go to Main page")
    @Nonnull
    public MainPage toMainPage() {
        mainPageLink.click();
        return new MainPage();
    }

    @Step("Go to Painting page")
    @Nonnull
    public PaintingPage toPaintingPage() {
        paintingPageLink.click();
        return new PaintingPage();
    }

    @Step("Go to Artist page")
    @Nonnull
    public ArtistPage toArtistPage() {
        artistPageLink.click();
        return new ArtistPage();
    }

    @Step("Go to Museum page")
    @Nonnull
    public MuseumPage toMuseumPage() {
        museumPageLink.click();
        return new MuseumPage();
    }

    @Step("Go to Login page")
    @Nonnull
    public LoginPage toLoginPage() {
        loginBtn.click();
        return new LoginPage();
    }

    @Step("Toggle Light Mode")
    @Nonnull
    public Header switchLight() {
        lightSwitch.click();
        return this;
    }

    @Step("Check logo is on dark mode")
    @Nonnull
    public MainPage checkLogoOnDarkMode() {
        cocoLogo.shouldHave(cssValue("color", "rgba(230, 200, 51, 1)"));
        return new MainPage();
    }

    @Step("Check logo is on light mode")
    @Nonnull
    public MainPage checkLogoOnLightMode() {
        cocoLogo.shouldHave(cssValue("color", "rgba(116, 74, 161, 1)"));
        return new MainPage();
    }

    @Step("Check that user is authorized")
    public Header checkUserIsAuthorized() {
        loginBtn.shouldNotBe(visible);
        avatar.shouldBe(visible);
        return this;
    }

    @Step("Check that user is not authorized")
    public Header checkUserIsNotAuthorized() {
        loginBtn.shouldBe(visible);
        avatar.shouldNotBe(visible);
        return this;
    }

    @Step("Go to Profile page")
    @Nonnull
    public ProfileModal openProfileModal() {
        avatar.click();
        return new ProfileModal();
    }
}

