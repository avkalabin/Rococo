package guru.qa.rococo.model;

import javax.annotation.Nonnull;
import java.time.Instant;

public record SessionJson(
        String username,
        Instant issuedAt,
        Instant expiresAt
) {

    @Nonnull
    public static SessionJson emptySession() {
        return new SessionJson(null, null, null);
    }
}
