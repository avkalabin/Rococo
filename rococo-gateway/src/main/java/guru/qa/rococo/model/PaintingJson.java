package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.grpc.rococo.Painting;

import javax.annotation.Nonnull;
import java.util.UUID;

public record PaintingJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("title")
        String title,
        @JsonProperty("description")
        String description,
        @JsonProperty("content")
        String content,
        @JsonProperty("museum")
        MuseumJson museum,
        @JsonProperty("artist")
        ArtistJson artist
) {

    @Nonnull
    public static PaintingJson fromGrpc(@Nonnull Painting painting) {
        return new PaintingJson(
                UUID.fromString(painting.getId()),
                painting.getTitle(),
                painting.getDescription(),
                painting.getContent(),
                MuseumJson.fromGrpc(painting.getMuseum()),
                ArtistJson.fromGrpc(painting.getArtist())
        );
    }
}
