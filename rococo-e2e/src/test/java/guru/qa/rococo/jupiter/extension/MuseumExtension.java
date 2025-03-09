package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.model.GeoJson;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.service.GeoClient;
import guru.qa.rococo.service.MuseumClient;
import guru.qa.rococo.service.impl.GeoDbClient;
import guru.qa.rococo.service.impl.MuseumDbClient;
import guru.qa.rococo.utils.ImgUtils;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class MuseumExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(MuseumExtension.class);
    private static final String MUSEUM_PHOTO_PATH = "img/museum.jpg";
    private final GeoClient geoClient = new GeoDbClient();
    private final MuseumClient museumClient = new MuseumDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Museum.class)
                .ifPresent(museumAnno -> {
                    String title = museumAnno.title().isEmpty() ? RandomDataUtils.randomMuseumTitle() : museumAnno.title();
                    String countryName = museumAnno.country().isEmpty()
                            ? RandomDataUtils.randomCountry().name()
                            : museumAnno.country();
                    String city = museumAnno.city().isEmpty() ? RandomDataUtils.randomCity() : museumAnno.city();

                    GeoJson geoJson = new GeoJson(city, geoClient.getCountryByName(countryName));

                    MuseumJson createdMuseum = museumClient.createMuseum(
                            new MuseumJson(
                                    null,
                                    title,
                                    RandomDataUtils.randomDescription(),
                                    ImgUtils.convertImageToBase64(MUSEUM_PHOTO_PATH),
                                    geoJson));

                    context.getStore(NAMESPACE).put(context.getUniqueId(), createdMuseum);
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(MuseumJson.class);
    }

    @Override
    public MuseumJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        MuseumJson museum = extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), MuseumJson.class);

        if (museum == null) {
            throw new IllegalStateException("No museum created for test: " + extensionContext.getUniqueId());
        }
        return museum;
    }
}
