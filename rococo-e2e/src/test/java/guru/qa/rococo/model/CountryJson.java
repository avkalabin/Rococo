package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.grpc.rococo.Country;
import guru.qa.rococo.data.entity.geo.CountryEntity;

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

    public static CountryJson fromEntity(CountryEntity entity) {
        return new CountryJson(entity.getId(), entity.getName());
    }
}
