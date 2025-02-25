package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.annotation.*;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.page.MainPage;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.sleep;

@WebTest()
public class MainTest {


    @ApiLogin(username = "qwe", password = "111")
    @Test
    @Artist(name = "Рома")
    @Museum(title = "Современный музей", country = "Россия", city = "Москва")
    @Painting(title = "Первая работа", description = "Рисунок сделанный в детском саду")
    void mainPageShouldBeOpened() {

        Selenide.open(MainPage.URL, MainPage.class)
                .checkThatPageLoaded();
        sleep(10000);
    }
}
