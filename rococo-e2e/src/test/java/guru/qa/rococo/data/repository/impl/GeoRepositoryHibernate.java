package guru.qa.rococo.data.repository.impl;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.geo.CountryEntity;
import guru.qa.rococo.data.repository.GeoRepository;
import jakarta.persistence.EntityManager;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.rococo.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class GeoRepositoryHibernate implements GeoRepository {

    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.geoJdbcUrl());

    @NotNull
    @Override
    public Optional<CountryEntity> getCountryById(UUID countryId) {
        return Optional.ofNullable(
                entityManager.find(CountryEntity.class, countryId));
    }

    @NotNull
    @Override
    public CountryEntity getCountryByName(String countryName) {
        return entityManager.createQuery("FROM CountryEntity WHERE name = :name", CountryEntity.class)
                .setParameter("name", countryName).getSingleResult();
    }

    @NotNull
    @Override
    public List<CountryEntity> getAllCountry() {
        return entityManager.createQuery("FROM CountryEntity", CountryEntity.class).getResultList();
    }
}
