package guru.qa.rococo.data.repository.impl;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.artist.ArtistEntity;
import guru.qa.rococo.data.entity.painting.PaintingEntity;
import guru.qa.rococo.data.repository.PaintingRepository;
import jakarta.persistence.EntityManager;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static guru.qa.rococo.data.jpa.EntityManagers.em;

public class PaintingRepositoryHibernate implements PaintingRepository {

    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.paintingJdbcUrl());

    @NotNull
    @Override
    public PaintingEntity create(@NotNull PaintingEntity painting) {
        entityManager.joinTransaction();
        entityManager.persist(painting);
        return painting;
    }

    @NotNull
    @Override
    public PaintingEntity findPaintingById(@NotNull UUID id) {
        return entityManager.find(PaintingEntity.class, id);
    }
}
