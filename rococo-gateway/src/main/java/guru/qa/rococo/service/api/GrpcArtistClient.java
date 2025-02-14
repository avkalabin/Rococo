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

import java.util.List;
import java.util.UUID;

import static guru.qa.rococo.model.ArtistJson.fromGrpc;

@Component
public class GrpcArtistClient {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcArtistClient.class);

    @GrpcClient("grpcArtistClient")
    private RococoArtistServiceGrpc.RococoArtistServiceBlockingStub rococoArtistServiceStub;

    public Page<ArtistJson> getAllArtist(@Nullable String name, Pageable pageable) {
        AllArtistsRequest.Builder requestBuilder = AllArtistsRequest.newBuilder()
                .setPage(pageable.getPageNumber())
                .setSize(pageable.getPageSize());

        if (name != null) {
            requestBuilder.setName(name);
        }

        try {
            AllArtistsResponse response = rococoArtistServiceStub.getAllArtists(requestBuilder.build());
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

    public ArtistJson getArtistById(UUID id) {
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
        Artist request = Artist.newBuilder()
                .setName(artist.name())
                .setBiography(artist.biography())
                .setPhoto(artist.photo())
                .build();
        Artist response = rococoArtistServiceStub.createArtist(request);
        return fromGrpc(response);
    }

    public ArtistJson updateArtist(ArtistJson artist) {
        Artist request = Artist.newBuilder()
                .setId(artist.id().toString())
                .setName(artist.name())
                .setBiography(artist.biography())
                .setPhoto(artist.photo())
                .build();
        Artist response = rococoArtistServiceStub.updateArtist(request);
        return fromGrpc(response);
    }
}
