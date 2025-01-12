package guru.qa.rococo.controller;

import guru.qa.rococo.model.Artist;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/artist")
public class ArtistController {

    @GetMapping("/all")
    public List<Artist> getAllArtists() {
        return null;
    }
}
