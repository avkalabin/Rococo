package guru.qa.rococo.controller;

import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.service.api.GrpcPaintingClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/painting")
public class PaintingController {

    private final GrpcPaintingClient grpcPaintingClient;

    public PaintingController(GrpcPaintingClient grpcPaintingClient) {
        this.grpcPaintingClient = grpcPaintingClient;
    }

    public Page<PaintingJson> getAll(@RequestParam(required = false) String title,
                                     @PageableDefault Pageable pageable) {
        return grpcPaintingClient.getAllPainting(title, pageable);
    }
}
