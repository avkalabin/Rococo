package guru.qa.rococo.utils;

import io.qameta.allure.Allure;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;

import static org.hamcrest.MatcherAssert.assertThat;

public class CustomAssert {

    public static <T> void check(String description, @NotNull T actual, @NotNull Matcher<? super T> matcher) {
        Allure.step(
                "Check %s. \nExpected: %s \n Actual: %s "
                        .formatted(
                                description,
                                trimLongString(matcher.toString()),
                                trimLongString(actual.toString())
                        ),
                () -> assertThat(actual, matcher)
        );
    }

    private static String trimLongString(@NotNull String str) {
        int maxLen = 255;
        return str.length() > maxLen
                ? "%s...".formatted(str.substring(0, maxLen))
                : str;
    }
}
