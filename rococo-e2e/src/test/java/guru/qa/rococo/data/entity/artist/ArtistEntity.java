package guru.qa.rococo.data.entity.artist;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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
}

