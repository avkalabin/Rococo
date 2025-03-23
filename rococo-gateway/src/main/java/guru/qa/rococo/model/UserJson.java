package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.grpc.rococo.User;
import jakarta.validation.constraints.Size;

import javax.annotation.Nonnull;
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
    @Nonnull
    public static UserJson fromGrpc(@Nonnull User user) {
        return new UserJson(
                UUID.fromString(user.getId()),
                user.getUsername(),
                user.getFirstname(),
                user.getLastname(),
                user.getAvatar()
        );
    }
}
