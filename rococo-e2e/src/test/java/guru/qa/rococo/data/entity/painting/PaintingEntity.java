package guru.qa.rococo.data.entity.painting;

import guru.qa.rococo.model.PaintingJson;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "painting")
public class PaintingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "MEDIUMBLOB")
    private byte[] content;

    @Column(name = "museum_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID museumId;

    @Column(name = "artist_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID artistId;

    public static PaintingEntity fromJson(PaintingJson painting) {
        PaintingEntity entity = new PaintingEntity();
        entity.setId(painting.id());
        entity.setTitle(painting.title());
        entity.setDescription(painting.description());
        entity.setContent(painting.content().getBytes());
        entity.setMuseumId(painting.museum().id());
        entity.setArtistId(painting.artist().id());
        return entity;
    }
}
