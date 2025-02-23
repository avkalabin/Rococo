package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.Painting;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.service.PaintingClient;
import guru.qa.rococo.service.impl.PaintingDbClient;
import guru.qa.rococo.utils.ImgUtils;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class PaintingExtension implements BeforeEachCallback, ParameterResolver {

    private final static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(PaintingExtension.class);
    private final static String PAINTING_PHOTO_PATH = "images/painting.jpg";

    private final PaintingClient paintingClient = new PaintingDbClient();


    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Painting.class)
                .ifPresent(paintingAnno -> {
                    String title = paintingAnno.title().isEmpty() ? RandomDataUtils.randomPaintingTitle() : paintingAnno.title();
                    String description = paintingAnno.description().isEmpty() ? RandomDataUtils.randomDescription() : paintingAnno.description();

                    MuseumJson museum = context.getStore(MuseumExtension.NAMESPACE).get(context.getUniqueId(), MuseumJson.class);
                    ArtistJson artist = context.getStore(ArtistExtension.NAMESPACE).get(context.getUniqueId(), ArtistJson.class);

                    if (museum == null || artist == null) {
                        throw new IllegalStateException("Annotations @Artist and @Museum must be present if @Painting is used!");
                    }

                    PaintingJson createdPainting = paintingClient.createPainting(
                            new PaintingJson(
                                    null,
                                    title,
                                    description,
                                    ImgUtils.convertImageToBase64(PAINTING_PHOTO_PATH),
                                    museum,
                                    artist));

                    context.getStore(NAMESPACE).put(context.getUniqueId(), createdPainting);
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(PaintingJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        PaintingJson painting = extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), PaintingJson.class);

        if (painting == null) {
            throw new IllegalStateException("No painting created for test: " + extensionContext.getUniqueId());
        }
        return painting;
    }
}
