package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.grpc.rococo.Country;
import guru.qa.rococo.data.entity.geo.CountryEntity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record CountryJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("name")
        String name) {

    @NotNull
    public static CountryJson fromGrpc(@NotNull Country response) {
        return new CountryJson(
                UUID.fromString(response.getId()),
                response.getName()
        );
    }

    @NotNull
    public static CountryJson fromEntity(@NotNull CountryEntity entity) {
        return new CountryJson(
                entity.getId(),
                entity.getName());
    }
}
