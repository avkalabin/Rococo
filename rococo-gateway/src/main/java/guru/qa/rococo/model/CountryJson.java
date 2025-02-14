package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.grpc.rococo.Country;

import java.util.UUID;

public record CountryJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("name")
        String name) {

    public static CountryJson fromGrpc(Country response) {
        return new CountryJson(
                UUID.fromString(response.getId()),
                response.getName()
        );
    }
}
