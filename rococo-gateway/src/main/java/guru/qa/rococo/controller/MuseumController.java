package guru.qa.rococo.controller;

import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.service.api.GrpcMuseumClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/museum")
public class MuseumController {

    private final GrpcMuseumClient grpcMuseumClient;

    @Autowired
    public MuseumController(GrpcMuseumClient grpcMuseumClient) {
        this.grpcMuseumClient = grpcMuseumClient;
    }

    @GetMapping
    public Page<MuseumJson> getAllMuseums(@RequestParam(required = false) String title,
                                          @PageableDefault Pageable pageable) {
        return grpcMuseumClient.getAllMuseums(title, pageable);
    }

    @GetMapping("/{id}")
    public MuseumJson getMuseumById(@PathVariable UUID id) {
        return grpcMuseumClient.getMuseumById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MuseumJson createMuseum(@RequestBody MuseumJson museum) {
        return grpcMuseumClient.createMuseum(museum);
    }

    @PatchMapping
    public MuseumJson updateMuseum(@RequestBody MuseumJson museum) {
        return grpcMuseumClient.updateMuseum(museum);
    }
}
