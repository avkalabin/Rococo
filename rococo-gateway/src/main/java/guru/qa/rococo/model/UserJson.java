package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.grpc.rococo.User;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("username")
        String username,
        @JsonProperty("firstname")
        @Size(max = 30, message = "First name must be less than 30 characters")
        String firstname,
        @JsonProperty("lastname")
        @Size(max = 50, message = "Last name must be less than 50 characters")
        String lastname,
        @JsonProperty("avatar")
        String avatar
) {
    public static UserJson fromGrpc(User user) {
        return new UserJson(
                UUID.fromString(user.getId()),
                user.getUsername(),
                user.getFirstname(),
                user.getLastname(),
                user.getAvatar()
        );
    }
}
