package guru.qa.rococo.service.api;

import guru.qa.rococo.model.ArtistJson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public class GrpcArtistClient {

    public Page<ArtistJson> getAllArtist(String name, Pageable pageable) {
        return null;
    }

    public ArtistJson getArtistById(UUID id) {
        return null;
    }
}
