package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.data.entity.userdata.UserdataEntity;
import guru.qa.rococo.data.repository.UserdataRepository;
import guru.qa.rococo.data.repository.impl.UserdataRepositoryHibernate;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.UserJson;
import guru.qa.rococo.page.MainPage;
import guru.qa.rococo.utils.ImgUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static guru.qa.rococo.utils.CustomAssert.check;
import static guru.qa.rococo.utils.RandomDataUtils.generateRandomWord;
import static guru.qa.rococo.utils.RandomDataUtils.randomUsername;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.equalTo;

@WebTest
@Tag("web")
@DisplayName("Profile web test")
public class ProfileWebTest {

    UserdataRepository userdataRepository = new UserdataRepositoryHibernate();

    @Test
    @User
    @ApiLogin()
    @DisplayName("Should show username at profile modal")
    void shouldShowUsernameAtProfileModal(@NotNull UserJson user) {
        Selenide.open(MainPage.URL, MainPage.class)
                .checkThatPageLoaded()
                .getHeader()
                .openProfileModal()
                .checkUsername(user.username());
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Should be able to update profile info")
    void shouldUpdateProfileInfo(@NotNull UserJson user) {
        String firstname = randomUsername();
        String surname = generateRandomWord(10);
        final String PHOTO_USER = "img/user.jpg";

        MainPage mainPage = Selenide.open(MainPage.URL, MainPage.class);
        mainPage
                .getHeader()
                .openProfileModal()
                .setFirstname(firstname)
                .setSurname(surname)
                .setAvatar(PHOTO_USER)
                .successSubmitModal();
        mainPage
                .checkAlertMessage("Профиль обновлен");

        UserdataEntity userdataEntity = userdataRepository.findById(user.id());

        check("expected firstname in db",
                userdataEntity.getFirstname(), equalTo(firstname));
        check("expected surname in db",
                userdataEntity.getLastname(), equalTo(surname));
        check("expected avatar in db",
                new String(userdataEntity.getAvatar(), UTF_8),
                equalTo(ImgUtils.convertImageToBase64(PHOTO_USER))
        );
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Should show error if firstname or surname is large than maximal characters count")
    void shouldShowErrorIfFirstnameOrSurnameIsLargeThanMaximalCharactersCount() {
        String longWord = generateRandomWord(256);

        Selenide.open(MainPage.URL, MainPage.class)
                .checkThatPageLoaded()
                .getHeader()
                .openProfileModal()
                .setFirstname(longWord)
                .setSurname(longWord)
                .errorSubmit()
                .checkFirstnameError("Имя не может быть длиннее 255 символов")
                .checkSurnameError("Фамилия не может быть длиннее 255 символов");
    }
}
