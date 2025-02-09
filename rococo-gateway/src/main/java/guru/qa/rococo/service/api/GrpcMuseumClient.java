package guru.qa.rococo.service.api;

import guru.qa.rococo.model.MuseumJson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public class GrpcMuseumClient {

    public Page<MuseumJson> getAllMuseum(String title, Pageable pageable) {
        return null;
    }

    public MuseumJson getMuseumById(UUID id) {
        return null;
    }
}
