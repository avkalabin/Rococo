package guru.qa.rococo.controller;

import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.service.api.GrpcMuseumClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/museum")
public class MuseumController {

    private final GrpcMuseumClient grpcMuseumClient;

    @Autowired
    public MuseumController(GrpcMuseumClient grpcMuseumClient) {
        this.grpcMuseumClient = grpcMuseumClient;
    }

    public Page<MuseumJson> getAll(@RequestParam(required = false) String title,
                                   @PageableDefault Pageable pageable) {
        return grpcMuseumClient.getAllMuseum(title, pageable);
    }
}
