package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.entity.geo.CountryEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface GeoRepository {

    @Nonnull
    Optional<CountryEntity> getCountryById(UUID countryId);

    @Nonnull
    CountryEntity getCountryByName(String countryName);

    @Nonnull
    List<CountryEntity> getAllCountry();
}
