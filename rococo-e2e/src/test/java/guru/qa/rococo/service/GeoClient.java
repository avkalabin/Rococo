package guru.qa.rococo.service;

import guru.qa.rococo.model.CountryJson;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface GeoClient {

    @Nonnull
    CountryJson getCountryByName(String countryName);
}
