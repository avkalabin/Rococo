package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.entity.painting.PaintingEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface PaintingRepository {

    @Nonnull
    PaintingEntity create(PaintingEntity painting);

    @Nonnull
    PaintingEntity findPaintingById(@Nonnull UUID id);

}
