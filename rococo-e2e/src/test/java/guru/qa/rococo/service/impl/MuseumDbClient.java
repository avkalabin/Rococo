package guru.qa.rococo.service.impl;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.museum.MuseumEntity;
import guru.qa.rococo.data.repository.MuseumRepository;
import guru.qa.rococo.data.repository.impl.MuseumRepositoryHibernate;
import guru.qa.rococo.data.tpl.XaTransactionTemplate;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.service.MuseumClient;
import io.qameta.allure.Step;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

public class MuseumDbClient implements MuseumClient {

    private static final Config CFG = Config.getInstance();
    private final MuseumRepository museumRepository = new MuseumRepositoryHibernate();
    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.museumJdbcUrl());

    @Step("Create museum using SQL")
    @Nonnull
    @Override
    public MuseumJson createMuseum(@NotNull MuseumJson museum) {
        return requireNonNull(
                xaTransactionTemplate.execute(
                        () -> MuseumJson.fromEntity(
                                museumRepository.create(
                                        MuseumEntity.fromJson(museum)
                                )
                        )
                )
        );
    }
}

