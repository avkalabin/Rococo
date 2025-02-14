package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.grpc.rococo.Artist;

import java.util.UUID;

public record ArtistJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("name")
        String name,
        @JsonProperty("biography")
        String biography,
        @JsonProperty("photo")
        String photo) {

    public static ArtistJson fromGrpc(Artist response) {
        return new ArtistJson(
                UUID.fromString(response.getId()),
                response.getName(),
                response.getBiography(),
                response.getPhoto()
        );
    }
}


