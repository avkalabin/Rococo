package guru.qa.rococo.controller;

import guru.qa.rococo.model.UserJson;
import guru.qa.rococo.service.api.GrpcUserdataClient;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final GrpcUserdataClient grpcUserdataClient;

    @Autowired
    public UserController(GrpcUserdataClient grpcUserdataClient) {
        this.grpcUserdataClient = grpcUserdataClient;
    }

    @GetMapping
    public UserJson getUser(@AuthenticationPrincipal Jwt principal) {
        String username = principal.getClaim("sub");
        return grpcUserdataClient.getUser(username);
    }

    @PatchMapping
    public UserJson updateUserInfo(@Valid @RequestBody UserJson user) {
        return grpcUserdataClient.updateUser(user);
    }

}
