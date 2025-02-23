package guru.qa.rococo.data.entity.artist;

import guru.qa.rococo.model.ArtistJson;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "artist")
public class ArtistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "biography", nullable = false, columnDefinition = "TEXT")
    private String biography;

    @Lob
    @Column(name = "photo", nullable = false, columnDefinition = "MEDIUMBLOB")
    private byte[] photo;

    public static ArtistEntity fromJson(ArtistJson artist) {
        ArtistEntity artistEntity = new ArtistEntity();
        artistEntity.setId(artist.id());
        artistEntity.setName(artist.name());
        artistEntity.setBiography(artist.biography());
        artistEntity.setPhoto(artist.photo().getBytes(StandardCharsets.UTF_8));
        return artistEntity;
    }
}

