package guru.qa.rococo.service.api;

import guru.qa.grpc.rococo.*;
import guru.qa.rococo.model.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

@Component
public class GrpcPaintingClient {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcPaintingClient.class);

    @GrpcClient("grpcPaintingClient")
    private RococoPaintingServiceGrpc.RococoPaintingServiceBlockingStub rococoPaintingServiceStub;

    public Page<PaintingJson> getAllPainting(String title, @Nonnull Pageable pageable) {

        AllPaintingsRequest request = AllPaintingsRequest.newBuilder()
                .setPage(pageable.getPageNumber())
                .setSize(pageable.getPageSize())
                .setTitle(title == null ? "" : title)
                .build();
        try {
            AllPaintingsResponse response = rococoPaintingServiceStub.getAllPaintings(request);
            List<PaintingJson> paintingJsonList = response.getPaintingsList()
                    .stream()
                    .map(PaintingJson::fromGrpc)
                    .toList();
            LOG.info("Successfully fetched {} paintings", paintingJsonList.size());
            return new PageImpl<>(paintingJsonList, pageable, response.getTotalCount());
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    public Page<PaintingJson> getPaintingByArtist(UUID id, @Nonnull Pageable pageable) {
        PaintingByArtistRequest request = PaintingByArtistRequest.newBuilder()
                .setPage(pageable.getPageNumber())
                .setSize(pageable.getPageSize())
                .setArtist(ArtistRequest.newBuilder().setId(id.toString()).build())
                .build();
        try {
            AllPaintingsResponse response = rococoPaintingServiceStub.getPaintingByArtist(request);
            List<PaintingJson> paintingJsonList = response.getPaintingsList()
                    .stream()
                    .map(PaintingJson::fromGrpc)
                    .toList();
            LOG.info("Successfully fetched {} paintings", paintingJsonList.size());
            return new PageImpl<>(paintingJsonList, pageable, response.getTotalCount());
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    public PaintingJson getPaintingById(@Nonnull UUID id) {
        PaintingRequest request = PaintingRequest.newBuilder()
                .setId(id.toString())
                .build();
        try {
            Painting response = rococoPaintingServiceStub.getPaintingById(request);
            return PaintingJson.fromGrpc(response);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Painting with id " + id + " not found", e);
            } else {
                LOG.error("### Error while calling gRPC server ", e);
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
            }
        }
    }

    public PaintingJson createPainting(PaintingJson painting) {
        Painting request = toGrpc(painting);
        try {
            Painting response = rococoPaintingServiceStub.createPainting(request);
            return PaintingJson.fromGrpc(response);
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    public PaintingJson updatePainting(PaintingJson painting) {
        Painting request = toGrpc(painting);
        try {
            Painting response = rococoPaintingServiceStub.updatePainting(request);
            return PaintingJson.fromGrpc(response);
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    @Nonnull
    private Painting toGrpc(@Nonnull PaintingJson painting) {
        MuseumJson museumJson = painting.museum();
        GeoJson geoJson = (museumJson != null) ? museumJson.geo() : null;
        CountryJson countryJson = (geoJson != null) ? geoJson.country() : null;

        Museum.Builder museumBuilder = Museum.newBuilder();
        if (museumJson != null) {
            museumBuilder.setId(museumJson.id().toString());
            if (museumJson.title() != null) museumBuilder.setTitle(museumJson.title());
            if (museumJson.description() != null) museumBuilder.setDescription(museumJson.description());
            if (museumJson.photo() != null) museumBuilder.setPhoto(museumJson.photo());

            if (geoJson != null) {
                Geo.Builder geoBuilder = Geo.newBuilder().setCity(geoJson.city() != null ? geoJson.city() : "");
                if (countryJson != null) {
                    geoBuilder.setCountry(Country.newBuilder()
                            .setId(countryJson.id().toString())
                            .setName(countryJson.name() != null ? countryJson.name() : "")
                            .build());
                }
                museumBuilder.setGeo(geoBuilder.build());
            }
        }

        ArtistJson artistJson = painting.artist();
        Artist.Builder artistBuilder = Artist.newBuilder();
        if (artistJson != null) {
            artistBuilder.setId(artistJson.id().toString());
            if (artistJson.name() != null) artistBuilder.setName(artistJson.name());
            if (artistJson.biography() != null) artistBuilder.setBiography(artistJson.biography());
            if (artistJson.photo() != null) artistBuilder.setPhoto(artistJson.photo());
        }

        Painting.Builder paintingBuilder = Painting.newBuilder()
                .setId(painting.id() != null ? painting.id().toString() : "")
                .setTitle(painting.title() != null ? painting.title() : "")
                .setDescription(painting.description() != null ? painting.description() : "")
                .setContent(painting.content() != null ? painting.content() : "")
                .setMuseum(museumBuilder.build())
                .setArtist(artistBuilder.build());

        return paintingBuilder.build();
    }
}
