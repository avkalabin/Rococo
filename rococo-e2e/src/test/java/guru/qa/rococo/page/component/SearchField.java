package guru.qa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Selenide.$;

public class SearchField extends BaseComponent<SearchField> {

    public SearchField(@Nonnull SelenideElement self) {
        super(self);
    }

    public SearchField() {
        super($("input[type='search']"));
    }

    @Step("Perform search for query {query}")
    @Nonnull
    public SearchField search(String query) {
        self.setValue(query).pressEnter();
        return this;
    }

}
