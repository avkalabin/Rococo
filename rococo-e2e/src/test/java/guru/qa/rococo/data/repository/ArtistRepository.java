package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.entity.artist.ArtistEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ArtistRepository {

    @Nonnull
    ArtistEntity create(ArtistEntity artist);

}
