package guru.qa.rococo.service.impl;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.artist.ArtistEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.data.repository.impl.ArtistRepositoryHibernate;
import guru.qa.rococo.data.tpl.XaTransactionTemplate;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.service.ArtistClient;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class ArtistDbClient implements ArtistClient {

    private static final Config CFG = Config.getInstance();
    private final ArtistRepository artistRepository = new ArtistRepositoryHibernate();
    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.artistJdbcUrl());

    @Step("Create artist using SQL")
    @Nonnull
    @Override
    public ArtistJson createArtist(ArtistJson artist) {
        return requireNonNull(
                xaTransactionTemplate.execute(
                        () -> ArtistJson.fromEntity(
                                artistRepository.create(
                                        ArtistEntity.fromJson(artist)
                                )
                        )
                )
        );
    }
}
