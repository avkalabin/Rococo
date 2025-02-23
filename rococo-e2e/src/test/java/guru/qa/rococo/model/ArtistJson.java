package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.grpc.rococo.Artist;
import guru.qa.rococo.data.entity.artist.ArtistEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
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
    public static ArtistJson fromGrpc(@NotNull Artist response) {
        return new ArtistJson(

                UUID.fromString(response.getId()),
                response.getName(),
                response.getBiography(),
                response.getPhoto()
        );
    }

    @Nonnull
    public static ArtistJson fromEntity(@NotNull ArtistEntity entity) {
        return new ArtistJson(entity.getId(),
                entity.getName(),
                entity.getBiography(),
                entity.getPhoto() != null ? new String(entity.getPhoto(), StandardCharsets.UTF_8) : null);
    }
}


