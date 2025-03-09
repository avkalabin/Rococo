package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.data.entity.geo.CountryEntity;
import guru.qa.rococo.data.entity.museum.MuseumEntity;
import guru.qa.rococo.data.repository.GeoRepository;
import guru.qa.rococo.data.repository.MuseumRepository;
import guru.qa.rococo.data.repository.impl.GeoRepositoryHibernate;
import guru.qa.rococo.data.repository.impl.MuseumRepositoryHibernate;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.page.MainPage;
import guru.qa.rococo.page.museum.MuseumDetailPage;
import guru.qa.rococo.page.museum.MuseumPage;
import guru.qa.rococo.utils.ImgUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static guru.qa.rococo.utils.CustomAssert.check;
import static guru.qa.rococo.utils.RandomDataUtils.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.equalTo;

@WebTest
@Tag("web")
@DisplayName("Museum web test")
public class MuseumWebTest {

    MuseumRepository museumRepository = new MuseumRepositoryHibernate();
    GeoRepository geoRepository = new GeoRepositoryHibernate();

    @Test
    @User
    @ApiLogin
    @DisplayName("Should be able to add new museum")
    void shouldBeAbleToAddNewMuseum() {
        final String title = randomMuseumTitle();
        final String country = randomCountry();
        final String city = randomCity();
        final String PHOTO_MUSEUM = "img/museum.jpg";
        final String description = randomDescription();

        MuseumPage museumPage = Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .toMuseumPage();
        museumPage.checkThatPageLoaded()
                .addNewMuseum()
                .setTitle(title)
                .selectCountry(country)
                .setCity(city)
                .setPhoto(PHOTO_MUSEUM)
                .setDescription(description)
                .successSubmitModal();
        museumPage
                .checkAlertMessage("Добавлен музей: " + title)
                .checkThatPageLoaded()
                .getSearchField()
                .search(title);
        museumPage.checkMuseumIsExist(title, city, country);
    }

    @Test
    @User
    @ApiLogin
    @Museum(title = "Русский музей", country = "Россия", city = "Санкт-Петербург")
    @DisplayName("Should be able edit museum")
    void shouldEditMuseum(@NotNull MuseumJson museum) {
        final String newTitle = randomMuseumTitle();
        final String newCountry = randomCountry();
        final String newCity = randomCity();
        final String newDescription = randomDescription();
        final String PHOTO_MUSEUM_NEW = "img/museum_new.jpg";

        MuseumDetailPage museumDetailPage = new MuseumDetailPage(museum.id().toString());

        museumDetailPage
                .openPage()
                .checkThatPageLoaded()
                .editMuseum()
                .setPhoto(PHOTO_MUSEUM_NEW)
                .setTitle(newTitle)
                .selectCountry(newCountry)
                .setCity(newCity)
                .setDescription(newDescription)
                .successSubmitModal();
        museumDetailPage
                .checkAlertMessage("Обновлен музей: " + newTitle);

        MuseumEntity museumEntity = museumRepository.findMuseumById(museum.id());
        CountryEntity countryEntity = geoRepository.getCountryById(museumEntity.getGeoId()).get();

        check("expected title in db",
                museumEntity.getTitle(), equalTo(newTitle));
        check("expected country in db",
                countryEntity.getName(), equalTo(newCountry));
        check("expected city in db",
                museumEntity.getCity(), equalTo(newCity));
        check("expected description in db",
                museumEntity.getDescription(), equalTo(newDescription));
        check("expected photo in db",
                new String(museumEntity.getPhoto(), UTF_8), equalTo(ImgUtils.convertImageToBase64(PHOTO_MUSEUM_NEW)));
    }

    @Test
    @User
    @ApiLogin
    @Museum
    @DisplayName("Should show one museum after filter")
    void shouldShowOneMuseumAfterFilter(@NotNull MuseumJson museum) {
        MuseumPage museumPage = Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .toMuseumPage();
        museumPage.checkThatPageLoaded()
                .getSearchField()
                .search(museum.title());
        museumPage.checkArtistListSize(1);
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Notification should be displayed when museums are not found")
    void shouldShowNotificationWhenMuseumsNotFound() {

        MuseumPage museumPage = Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .toMuseumPage();
        museumPage.getSearchField()
                .search(randomMuseumTitle());
        museumPage.checkMuseumListHaveText("Музеи не найдены")
                .checkMuseumListHaveText("Для указанного вами фильтра мы не смогли не найти ни одного музея");
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Should show error if museum name, city and description is large than maximal characters count")
    void shouldShowErrorIfMuseumNameCityAndDescriptionIsLargeThanMaximalCharactersCount() {
        final String longWord = generateRandomWord(256);
        final String veryLongWord = generateRandomWord(2001);
        final String PHOTO_MUSEUM = "img/museum.jpg";

        MuseumPage museumPage = Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .toMuseumPage();

        museumPage
                .checkThatPageLoaded()
                .addNewMuseum()
                .setTitle(longWord)
                .selectCountry("Австралия")
                .setCity(longWord)
                .setPhoto(PHOTO_MUSEUM)
                .setDescription(veryLongWord)
                .errorSubmit()
                .checkTitleError("Название не может быть длиннее 255 символов")
                .checkCityError("Город не может быть длиннее 255 символов")
                .checkDescriptionError("Описание не может быть длиннее 2000 символов");
    }
}
