package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.service.ArtistClient;
import guru.qa.rococo.service.impl.ArtistDbClient;
import guru.qa.rococo.utils.ImgUtils;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class ArtistExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ArtistExtension.class);
    private static final String ARTIST_PHOTO_PATH = "img/artist.jpg";
    private final ArtistClient artistClient = new ArtistDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Artist.class)
                .ifPresent(artistAnno -> {
                    String name = artistAnno.name().isEmpty() ? RandomDataUtils.randomArtistName() : artistAnno.name();
                    ArtistJson createdArtist = artistClient.createArtist(
                            new ArtistJson(
                                    null,
                                    name,
                                    RandomDataUtils.randomBiography(),
                                    ImgUtils.convertImageToBase64(ARTIST_PHOTO_PATH)));

                    context.getStore(NAMESPACE).put(context.getUniqueId(), createdArtist);
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(ArtistJson.class);
    }

    @Override
    public ArtistJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        ArtistJson artist = extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), ArtistJson.class);

        if (artist == null) {
            throw new IllegalStateException("No artist created for test: " + extensionContext.getUniqueId());
        }
        return artist;
    }
}
