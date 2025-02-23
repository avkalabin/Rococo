package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.grpc.rococo.Museum;
import guru.qa.rococo.data.entity.museum.MuseumEntity;

import java.nio.charset.StandardCharsets;
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

    public static MuseumJson fromEntity(MuseumEntity entity) {

        GeoJson geoJson = new GeoJson(
                entity.getCity(),
                new CountryJson(entity.getGeoId(), null)
        );
        return new MuseumJson(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getPhoto() != null ? new String(entity.getPhoto(), StandardCharsets.UTF_8) : null,
                geoJson);
    }


}
