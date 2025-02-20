package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public record TestData(
        @JsonIgnore @Nonnull String password) {
}
