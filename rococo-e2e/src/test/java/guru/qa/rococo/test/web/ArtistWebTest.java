package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.data.entity.artist.ArtistEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.data.repository.impl.ArtistRepositoryHibernate;
import guru.qa.rococo.jupiter.annotation.*;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.page.MainPage;
import guru.qa.rococo.page.artist.ArtistDetailPage;
import guru.qa.rococo.page.artist.ArtistPage;
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
@DisplayName("WEB | Artist tests")
public class ArtistWebTest {

    private final ArtistRepository artistRepository = new ArtistRepositoryHibernate();

    @Test
    @User
    @ApiLogin
    @DisplayName("Should be able to add new artist")
    void shouldAddNewArtist() {
        final String name = randomUsername();
        final String biography = randomBiography();
        final String PHOTO_ARTIST = "img/artist.jpg";

        ArtistPage artistPage = Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .toArtistPage();
        artistPage.checkThatPageLoaded()
                .addNewArtist()
                .setName(name)
                .setBiography(biography)
                .setPhoto(PHOTO_ARTIST)
                .successSubmitModal();
        artistPage
                .checkAlertMessage("Добавлен художник: " + name)
                .checkThatPageLoaded()
                .getSearchField()
                .search(name);
        artistPage.checkArtistListHaveText(name);
    }

    @Test
    @User
    @ApiLogin
    @Artist
    @DisplayName("Should be able to edit artist")
    void shouldEditArtist(@NotNull ArtistJson artist) {
        final String newName = randomUsername();
        final String newBiography = randomBiography();
        final String PHOTO_ARTIST_NEW = "img/artist_new.jpg";

        ArtistPage artistPage = Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .toArtistPage();
        artistPage.checkThatPageLoaded()
                .getSearchField()
                .search(artist.name());
        artistPage.openArtist(artist.name())
                .editArtist()
                .setName(newName)
                .setBiography(newBiography)
                .setPhoto(PHOTO_ARTIST_NEW)
                .successSubmitModal();
        artistPage
                .checkAlertMessage("Обновлен художник: " + newName);

        ArtistEntity artistEntity = artistRepository.findArtistById(artist.id());
        check("expected new name in db",
                artistEntity.getName(), equalTo(newName));
        check("expected new biography in db",
                artistEntity.getBiography(), equalTo(newBiography));
        check("expected new photo in db",
                new String(artistEntity.getPhoto(), UTF_8),
                equalTo(ImgUtils.convertImageToBase64(PHOTO_ARTIST_NEW))
        );
    }

    @Test
    @User
    @ApiLogin
    @Artist
    @Museum
    @Painting
    @DisplayName("On the artist detail page, the artist's painting is displayed")
    void artistPaintingShouldBeDisplayedOnArtistDetailPage(@NotNull PaintingJson paintingJson, @NotNull ArtistJson artist) {
        ArtistDetailPage artistDetailPage = new ArtistDetailPage(artist.id().toString());

        artistDetailPage
                .openPage()
                .checkThatPageLoaded()
                .openPaintingCard(paintingJson.title())
                .checkThatPageLoaded();
    }

    @Test
    @User
    @ApiLogin
    @Artist
    @DisplayName("Should show one artist after filter")
    void shouldShowOneArtistAfterFilter(@NotNull ArtistJson artist) {
        ArtistPage artistPage = Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .toArtistPage();
        artistPage.checkThatPageLoaded()
                .getSearchField()
                .search(artist.name());
        artistPage.checkArtistListSize();
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Notification should be displayed when artists are not found")
    void shouldShowNotificationWhenArtistsNotFound() {

        ArtistPage artistPage = Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .toArtistPage();
        artistPage.getSearchField()
                .search(randomUsername());
        artistPage.checkArtistListHaveText("Художники не найдены")
                .checkArtistListHaveText("Для указанного вами фильтра мы не смогли найти художников");
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Should show error if artist name and biography is large than maximal characters count")
    void shouldShowErrorIfArtistNameAndBiographyIsLargeThanMaximalCharactersCount() {
        final String longWord = generateRandomWord(256);
        final String veryLongWord = generateRandomWord(2001);
        final String PHOTO_ARTIST = "img/artist.jpg";

        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .toArtistPage()
                .addNewArtist()
                .setName(longWord)
                .setPhoto(PHOTO_ARTIST)
                .setBiography(veryLongWord)
                .errorSubmit()
                .checkNameError("Имя не может быть длиннее 255 символов")
                .checkBiographyError("Биография не может быть длиннее 2000 символов");
    }
}
