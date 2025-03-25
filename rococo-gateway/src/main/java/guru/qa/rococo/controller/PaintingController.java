package guru.qa.rococo.controller;

import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.service.api.GrpcPaintingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/painting")
public class PaintingController {

    private final GrpcPaintingClient grpcPaintingClient;

    @Autowired
    public PaintingController(GrpcPaintingClient grpcPaintingClient) {
        this.grpcPaintingClient = grpcPaintingClient;
    }

    @GetMapping
    public Page<PaintingJson> getAllPaintings(@RequestParam(required = false) String title,
                                     @PageableDefault Pageable pageable) {
        return grpcPaintingClient.getAllPaintings(title, pageable);
    }

    @GetMapping("/author/{id}")
    public Page<PaintingJson> getPaintingByArtist(@PathVariable UUID id, Pageable pageable) {
        return grpcPaintingClient.getPaintingByArtist(id, pageable);
    }

    @GetMapping("/{id}")
    public PaintingJson getPaintingById(@PathVariable UUID id) {
        return grpcPaintingClient.getPaintingById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaintingJson createPainting(@RequestBody PaintingJson painting) {
        return grpcPaintingClient.createPainting(painting);
    }

    @PatchMapping
    public PaintingJson updatePainting(@RequestBody PaintingJson painting) {
        return grpcPaintingClient.updatePainting(painting);
    }
}
