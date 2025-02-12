package guru.qa.rococo.service.api;

import guru.qa.rococo.model.PaintingJson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GrpcPaintingClient {

    public Page<PaintingJson> getAllPainting(String title, Pageable pageable) {
        return null;
    }

    public Page<PaintingJson> getPaintingByArtist(UUID id, Pageable pageable) {
        return null;
    }

    public PaintingJson getPaintingById(UUID id) {
        return null;
    }
}
