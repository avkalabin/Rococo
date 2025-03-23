package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.grpc.rococo.Artist;

import javax.annotation.Nonnull;
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

    @Nonnull
    public static ArtistJson fromGrpc(@Nonnull Artist response) {
        return new ArtistJson(

                UUID.fromString(response.getId()),
                response.getName(),
                response.getBiography(),
                response.getPhoto()
        );
    }
}


