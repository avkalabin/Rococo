package guru.qa.rococo.service.api;

import guru.qa.rococo.model.MuseumJson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GrpcMuseumClient {

    public Page<MuseumJson> getAllMuseum(String title, Pageable pageable) {
        return null;
    }

    public MuseumJson getMuseumById(UUID id) {
        return null;
    }
}
