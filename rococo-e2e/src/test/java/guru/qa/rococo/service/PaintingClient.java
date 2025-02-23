package guru.qa.rococo.service;

import guru.qa.rococo.model.PaintingJson;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface PaintingClient {

    @Nonnull
    PaintingJson createPainting(PaintingJson painting);
}
