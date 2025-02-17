package guru.qa.rococo.service.api;

import guru.qa.grpc.rococo.*;
import guru.qa.rococo.model.PaintingJson;
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

import java.util.List;
import java.util.UUID;

@Component
public class GrpcPaintingClient {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcPaintingClient.class);

    @GrpcClient("grpcPaintingClient")
    private RococoPaintingServiceGrpc.RococoPaintingServiceBlockingStub rococoPaintingServiceStub;

    public Page<PaintingJson> getAllPainting(String title, Pageable pageable) {

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

    public Page<PaintingJson> getPaintingByArtist(UUID id, Pageable pageable) {
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

    public PaintingJson getPaintingById(UUID id) {
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

    private Painting toGrpc(PaintingJson painting) {

        Museum museum = Museum.newBuilder()
                .setId(painting.museum().id().toString())
                .setTitle(painting.museum().title())
                .setDescription(painting.museum().description())
                .setPhoto(painting.museum().photo())
                .setGeo(Geo.newBuilder()
                        .setCity(painting.museum().geo().city())
                        .setCountry(Country.newBuilder()
                                .setId(painting.museum().geo().country().id().toString())
                                .setName(painting.museum().geo().country().name())
                                .build())
                        .build())
                .build();

        Artist artist = Artist.newBuilder()
                .setId(painting.artist().id().toString())
                .setName(painting.artist().name())
                .setBiography(painting.artist().biography())
                .setPhoto(painting.artist().photo())
                .build();

        return Painting.newBuilder()
                .setId(painting.id().toString())
                .setTitle(painting.title())
                .setDescription(painting.description())
                .setContent(painting.content())
                .setMuseum(museum)
                .setArtist(artist)
                .build();
    }
}
