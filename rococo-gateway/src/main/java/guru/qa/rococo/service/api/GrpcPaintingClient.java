package guru.qa.rococo.service.api;

import guru.qa.rococo.model.PaintingJson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class GrpcPaintingClient {

    public Page<PaintingJson> getAllPainting(String title, Pageable pageable) {
        return null;
    }
}
