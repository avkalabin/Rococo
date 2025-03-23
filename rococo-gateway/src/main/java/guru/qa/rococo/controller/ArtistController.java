package guru.qa.rococo.controller;

import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.service.api.GrpcArtistClient;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/artist")
public class ArtistController {

    private final GrpcArtistClient grpcArtistClient;

    @Autowired
    public ArtistController(GrpcArtistClient grpcArtistClient) {
        this.grpcArtistClient = grpcArtistClient;
    }

    @GetMapping
    public Page<ArtistJson> getAllArtists(@RequestParam(required = false) String name,
                                          @PageableDefault Pageable pageable) {
        return grpcArtistClient.getAllArtists(name, pageable);
    }

    @GetMapping("/{id}")
    public ArtistJson getArtistById(@PathVariable UUID id) {
        return grpcArtistClient.getArtistById(id);
    }

    @PostMapping
    public ArtistJson createArtist(@Valid @RequestBody ArtistJson artist) {
        return grpcArtistClient.createArtist(artist);
    }

    @PatchMapping
    public ArtistJson updateArtist(@Valid @RequestBody ArtistJson artist) {
        return grpcArtistClient.updateArtist(artist);
    }
}
