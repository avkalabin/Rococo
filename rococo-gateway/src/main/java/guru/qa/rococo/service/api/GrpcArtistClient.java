package guru.qa.rococo.service.api;

import guru.qa.rococo.model.ArtistJson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GrpcArtistClient {

    public Page<ArtistJson> getAllArtist(String name, Pageable pageable) {
        return null;
    }

    public ArtistJson getArtistById(UUID id) {
        return null;
    }
}
