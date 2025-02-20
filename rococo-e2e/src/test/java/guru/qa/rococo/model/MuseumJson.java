package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.grpc.rococo.Museum;

import java.util.UUID;

public record MuseumJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("title")
        String title,
        @JsonProperty("description")
        String description,
        @JsonProperty("photo")
        String photo,
        @JsonProperty("geo")
        GeoJson geo) {

    public static MuseumJson fromGrpc(Museum response) {

        GeoJson geoJson = new GeoJson(
                response.getGeo().getCity(),
                CountryJson.fromGrpc(response.getGeo().getCountry())
        );
        return new MuseumJson(
                UUID.fromString(response.getId()),
                response.getTitle(),
                response.getDescription(),
                response.getPhoto(),
                geoJson
        );
    }


}
