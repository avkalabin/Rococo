package guru.qa.rococo.service.api;

import guru.qa.rococo.model.MuseumJson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class GrpcMuseumClient {

    public Page<MuseumJson> getAllMuseum(String title, Pageable pageable) {
        return null;
    }
}
