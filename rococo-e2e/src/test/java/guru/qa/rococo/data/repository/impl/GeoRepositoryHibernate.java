package guru.qa.rococo.data.repository.impl;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.geo.CountryEntity;
import guru.qa.rococo.data.repository.GeoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.rococo.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class GeoRepositoryHibernate implements GeoRepository {


    @NotNull
    @Override
    public CountryEntity getCountryById(UUID countryId) {
        return null;
    }

    @NotNull
    @Override
    public CountryEntity getCountryByName(String countryName) {
        return null;
    }

    @NotNull
    @Override
    public List<CountryEntity> getAllCountry() {
        return List.of();
    }
}
