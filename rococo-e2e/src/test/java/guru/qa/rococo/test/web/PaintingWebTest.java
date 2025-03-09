package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.data.entity.painting.PaintingEntity;
import guru.qa.rococo.data.repository.PaintingRepository;
import guru.qa.rococo.data.repository.impl.PaintingRepositoryHibernate;
import guru.qa.rococo.jupiter.annotation.*;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.page.MainPage;
import guru.qa.rococo.page.artist.ArtistDetailPage;
import guru.qa.rococo.page.painting.PaintingDetailPage;
import guru.qa.rococo.page.painting.PaintingPage;
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
@DisplayName("Painting web test")
public class PaintingWebTest {

    PaintingRepository paintingRepository = new PaintingRepositoryHibernate();

    @Test
    @User
    @ApiLogin
    @Artist
    @Museum
    @DisplayName("Should be able to add new painting")
    void shouldAddNewPainting(@NotNull ArtistJson artist, @NotNull MuseumJson museum) {
        String title = randomPaintingTitle();
        String description = randomDescription();
        final String PHOTO_PAINTING = "img/painting.jpg";

        PaintingPage paintingPage = Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .toPaintingPage();
        paintingPage.checkThatPageLoaded()
                .addNewPainting()
                .setTitle(title)
                .setContent(PHOTO_PAINTING)
                .selectArtist(artist.name())
                .setDescription(description)
                .selectMuseum(museum.title())
                .successSubmitModal();
        paintingPage
                .checkAlertMessage("Добавлена картины: " + title)
                .openPaintingCard(title)
                .checkThatPageLoaded();
    }

    @Test
    @User
    @ApiLogin
    @Artist
    @Museum
    @DisplayName("Should be able to add new painting from artist detail page")
    void shouldAddNewPaintingFromArtistDetailPage(@NotNull ArtistJson artist, @NotNull MuseumJson museum) {

        final String PHOTO_PAINTING = "img/painting.jpg";
        final String title = randomPaintingTitle();
        final String description = randomDescription();

        ArtistDetailPage artistDetailPage = new ArtistDetailPage(artist.id().toString());
        artistDetailPage
                .openPage()
                .addNewPainting()
                .checkThatPageLoaded()
                .setTitle(title)
                .setContent(PHOTO_PAINTING)
                .setDescription(description)
                .selectMuseum(museum.title())
                .successSubmitModal();
        artistDetailPage
                .checkAlertMessage("Добавлена картина: " + title)
                .openPaintingCard(title)
                .checkThatPageLoaded();
    }

    @Test
    @User
    @ApiLogin
    @Artist
    @Museum
    @Painting(title = "Mona Lisa", description = "It should come as no surprise that the most famous painting" +
            " in the world is that mysterious woman with the enigmatic smile")
    @DisplayName("Should be able to edit painting")
    void shouldEditPainting(@NotNull PaintingJson painting) {

        final String newTitle = randomPaintingTitle();
        final String newDescription = randomDescription();
        final String PHOTO_PAINTING_NEW = "img/painting_new.jpg";
        PaintingDetailPage paintingDetailPage = new PaintingDetailPage(painting.id().toString());

        paintingDetailPage
                .openPage()
                .checkThatPageLoaded()
                .editPainting()
                .setContent(PHOTO_PAINTING_NEW)
                .setTitle(newTitle)
                .setDescription(newDescription)
                .successSubmitModal();
        paintingDetailPage
                .checkAlertMessage("Обновлена картина: " + newTitle);

        PaintingEntity paintingEntity = paintingRepository.findPaintingById(painting.id());
        check("expected new title in db",
                paintingEntity.getTitle(), equalTo(newTitle));
        check("expected new description in db",
                paintingEntity.getDescription(), equalTo(newDescription));
        check("expected new content in db",
                new String(paintingEntity.getContent(), UTF_8),
                equalTo(ImgUtils.convertImageToBase64(PHOTO_PAINTING_NEW)));
        check("expected artistId in db",
                paintingEntity.getArtistId(), equalTo(painting.artist().id()));
        check("expected museumId in db",
                paintingEntity.getMuseumId(), equalTo(painting.museum().id()));
    }

    @Test
    @User
    @ApiLogin
    @Artist
    @Museum
    @Painting
    @DisplayName("Should show one painting after filter")
    void shouldShowOneArtistAfterFilter(@NotNull PaintingJson painting) {
        PaintingPage paintingPage = Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .toPaintingPage();
        paintingPage.checkThatPageLoaded()
                .getSearchField()
                .search(painting.title());
        paintingPage.checkPaintingListSize(1);
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Notification should be displayed when paintings are not found")
    void shouldShowNotificationWhenPaintingsNotFound() {

        PaintingPage paintingPage = Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .toPaintingPage();
        paintingPage.getSearchField()
                .search(randomPaintingTitle());
        paintingPage.checkPaintingListHaveText("Картины не найдены")
                .checkPaintingListHaveText("Для указанного вами фильтра мы не смогли не найти ни одной картины");
    }

    @Test
    @User
    @ApiLogin
    @Artist
    @Museum
    @DisplayName("Should show error if painting title and description is large than maximal characters count")
    void shouldShowErrorIfPaintingTitleAndDescriptionIsLargeThanMaximalCharactersCount(@NotNull ArtistJson artist, @NotNull MuseumJson museum) {
        final String longWord = generateRandomWord(256);
        final String veryLongWord = generateRandomWord(2001);
        final String PHOTO_PAINTING = "img/painting.jpg";

        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .toPaintingPage()
                .addNewPainting()
                .setTitle(longWord)
                .setContent(PHOTO_PAINTING)
                .selectArtist(artist.name())
                .setDescription(veryLongWord)
                .selectMuseum(museum.title())
                .errorSubmit()
                .checkTitleError("Название не может быть длиннее 255 символов")
                .checkDescriptionError("Описание не может быть длиннее 2000 символов");
    }
}
