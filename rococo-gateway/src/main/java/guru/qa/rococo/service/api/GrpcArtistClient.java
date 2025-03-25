package guru.qa.rococo.service.api;

import guru.qa.grpc.rococo.*;
import guru.qa.rococo.model.ArtistJson;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nullable;
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

import static guru.qa.rococo.model.ArtistJson.fromGrpc;

@Component
public class GrpcArtistClient {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcArtistClient.class);

    @GrpcClient("grpcArtistClient")
    private RococoArtistServiceGrpc.RococoArtistServiceBlockingStub rococoArtistServiceStub;

    public Page<ArtistJson> getAllArtists(@Nullable String name, @Nonnull Pageable pageable) {
        AllArtistsRequest request = AllArtistsRequest.newBuilder()
                .setPage(pageable.getPageNumber())
                .setSize(pageable.getPageSize())
                .setName(name == null ? "" : name)
                .build();

        try {
            AllArtistsResponse response = rococoArtistServiceStub.getAllArtists(request);
            List<ArtistJson> artistJsonList = response.getArtistsList()
                    .stream()
                    .map(ArtistJson::fromGrpc)
                    .toList();
            LOG.info("Successfully fetched {} artists", artistJsonList.size());
            return new PageImpl<>(artistJsonList, pageable, response.getTotalCount());
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    public ArtistJson getArtistById(@Nonnull UUID id) {
        ArtistRequest request = ArtistRequest.newBuilder()
                .setId(id.toString())
                .build();
        try {
            Artist response = rococoArtistServiceStub.getArtistById(request);
            return fromGrpc(response);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist with id " + id + " not found", e);
            } else {
                LOG.error("### Error while calling gRPC server ", e);
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
            }
        }
    }

    public ArtistJson createArtist(ArtistJson artist) {
        Artist request = toGrpc(artist);
        try {
            Artist response = rococoArtistServiceStub.createArtist(request);
            return fromGrpc(response);
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    public ArtistJson updateArtist(ArtistJson artist) {
        Artist request = toGrpc(artist);
        try {
            Artist response = rococoArtistServiceStub.updateArtist(request);
            return fromGrpc(response);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getStatus().getDescription(), e);
            }
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    @Nonnull
    private Artist toGrpc(@Nonnull ArtistJson artist) {
        return Artist.newBuilder()
                .setId(artist.id() != null ? artist.id().toString() : "")
                .setName(artist.name())
                .setBiography(artist.biography())
                .setPhoto(artist.photo())
                .build();
    }
}
