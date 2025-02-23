package guru.qa.rococo.service;

import guru.qa.rococo.model.ArtistJson;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ArtistClient {

    @Nonnull
    ArtistJson createArtist(ArtistJson artist);
}
