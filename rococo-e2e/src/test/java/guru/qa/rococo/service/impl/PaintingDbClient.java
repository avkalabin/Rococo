package guru.qa.rococo.service.impl;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.painting.PaintingEntity;
import guru.qa.rococo.data.repository.PaintingRepository;
import guru.qa.rococo.data.repository.impl.PaintingRepositoryHibernate;
import guru.qa.rococo.data.tpl.XaTransactionTemplate;
import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.service.PaintingClient;
import io.qameta.allure.Step;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

public class PaintingDbClient implements PaintingClient {

    private static final Config CFG = Config.getInstance();
    private final PaintingRepository paintingRepository = new PaintingRepositoryHibernate();
    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.paintingJdbcUrl());

    @Step("Create painting using SQL")
    @Nonnull
    @Override
    public PaintingJson createPainting(@NotNull PaintingJson painting) {
        return requireNonNull(
                xaTransactionTemplate.execute(
                        () -> PaintingJson.fromEntity(
                                paintingRepository.create(
                                        PaintingEntity.fromJson(painting)
                                )
                        )
                )
        );
    }
}
