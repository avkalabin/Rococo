package guru.qa.rococo.service.impl;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.repository.GeoRepository;
import guru.qa.rococo.data.repository.impl.GeoRepositoryHibernate;
import guru.qa.rococo.data.tpl.XaTransactionTemplate;
import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.service.GeoClient;
import io.qameta.allure.Step;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public class GeoDbClient implements GeoClient {
    private static final Config CFG = Config.getInstance();
    private final GeoRepository geoRepository = new GeoRepositoryHibernate();
    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.artistJdbcUrl());

    @Step("Fetch country using SQL")
    @NotNull
    @Override
    public CountryJson getCountryByName(@NotNull String countryName) {
        return requireNonNull(
                xaTransactionTemplate.execute(
                        () -> CountryJson.fromEntity(
                                geoRepository.getCountryByName(countryName)
                        )
                )
        );
    }
}
