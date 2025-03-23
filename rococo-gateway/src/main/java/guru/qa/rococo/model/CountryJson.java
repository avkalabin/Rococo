package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.grpc.rococo.Country;

import javax.annotation.Nonnull;
import java.util.UUID;

public record CountryJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("name")
        String name) {

    @Nonnull
    public static CountryJson fromGrpc(@Nonnull Country response) {
        return new CountryJson(
                UUID.fromString(response.getId()),
                response.getName()
        );
    }
}
