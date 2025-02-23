package guru.qa.rococo.data.repository.impl;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.museum.MuseumEntity;
import guru.qa.rococo.data.repository.MuseumRepository;
import jakarta.persistence.EntityManager;
import org.jetbrains.annotations.NotNull;

import static guru.qa.rococo.data.jpa.EntityManagers.em;

public class MuseumRepositoryHibernate implements MuseumRepository {

    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.museumJdbcUrl());

    @NotNull
    @Override
    public MuseumEntity create(@NotNull MuseumEntity museum) {
        entityManager.joinTransaction();
        entityManager.persist(museum);
        return museum;
    }
}
