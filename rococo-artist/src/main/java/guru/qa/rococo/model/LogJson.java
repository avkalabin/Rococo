package guru.qa.rococo.model;

import java.time.Instant;
import java.util.UUID;

public record LogJson(
        EventType eventType,
        UUID entityId,
        String description,
        Instant eventDate
) {
}
