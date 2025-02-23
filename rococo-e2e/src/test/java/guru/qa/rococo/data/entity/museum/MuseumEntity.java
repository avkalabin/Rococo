package guru.qa.rococo.data.entity.museum;

import guru.qa.rococo.model.MuseumJson;
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
@Table(name = "museum")
public class MuseumEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    UUID id;

    @Column(name = "title", nullable = false, length = 50)
    String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    String description;

    @Lob
    @Column(name = "photo", nullable = false, columnDefinition = "MEDIUMBLOB")
    private byte[] photo;

    @Column(name = "city", nullable = false, length = 50)
    String city;

    @Column(name = "geo_id", nullable = false, columnDefinition = "BINARY(16)")
    UUID geoId;

    public static MuseumEntity fromJson(MuseumJson museum) {
        MuseumEntity museumEntity = new MuseumEntity();
        museumEntity.setId(museum.id());
        museumEntity.setTitle(museum.title());
        museumEntity.setDescription(museum.description());
        museumEntity.setPhoto(museum.photo().getBytes(StandardCharsets.UTF_8));
        museumEntity.setCity(museum.geo().city());
        museumEntity.setGeoId(museum.geo().country().id());
        return museumEntity;
    }
}


