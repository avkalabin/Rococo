package guru.qa.rococo.service;

import guru.qa.rococo.model.MuseumJson;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface MuseumClient {

    @Nonnull
    MuseumJson createMuseum(MuseumJson museum);
}
