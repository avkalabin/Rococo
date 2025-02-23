package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.grpc.rococo.Painting;
import guru.qa.rococo.data.entity.painting.PaintingEntity;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
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

    @NotNull
    public static PaintingJson fromGrpc(@NotNull Painting painting) {
        return new PaintingJson(
                UUID.fromString(painting.getId()),
                painting.getTitle(),
                painting.getDescription(),
                painting.getContent(),
                MuseumJson.fromGrpc(painting.getMuseum()),
                ArtistJson.fromGrpc(painting.getArtist())
        );
    }


    public static PaintingJson fromEntity(PaintingEntity paintingEntity) {
        MuseumJson museumJson = new MuseumJson(
                paintingEntity.getMuseumId(),
                null,
                null,
                null,
                null
        );

        ArtistJson artistJson = new ArtistJson(
                paintingEntity.getArtistId(),
                null,
                null,
                null
        );
        return new PaintingJson(
                paintingEntity.getId(),
                paintingEntity.getTitle(),
                paintingEntity.getDescription(),
                paintingEntity.getContent() != null ? new String(paintingEntity.getContent(), StandardCharsets.UTF_8) : null,
                museumJson,
                artistJson);
    }
}
