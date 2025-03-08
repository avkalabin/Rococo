package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.entity.museum.MuseumEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface MuseumRepository {

    @Nonnull
    MuseumEntity create(MuseumEntity artist);

    @Nonnull
    MuseumEntity findMuseumById(@Nonnull UUID id);

}
