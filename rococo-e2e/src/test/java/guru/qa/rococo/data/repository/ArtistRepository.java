package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.entity.artist.ArtistEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface ArtistRepository {

    @Nonnull
    ArtistEntity create(ArtistEntity artist);

    @Nonnull
    ArtistEntity findArtistById(UUID id);
}
