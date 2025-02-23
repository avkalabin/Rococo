package guru.qa.rococo.data.repository.impl;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.artist.ArtistEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import jakarta.persistence.EntityManager;
import org.jetbrains.annotations.NotNull;

import static guru.qa.rococo.data.jpa.EntityManagers.em;

public class ArtistRepositoryHibernate implements ArtistRepository {

    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.artistJdbcUrl());

    @NotNull
    @Override
    public ArtistEntity create(@NotNull ArtistEntity artist) {
        entityManager.joinTransaction();
        entityManager.persist(artist);
        return artist;
    }
}
