package guru.qa.rococo.service.api;

import guru.qa.grpc.rococo.Artist;
import guru.qa.grpc.rococo.ArtistRequest;
import guru.qa.grpc.rococo.RococoArtistServiceGrpc;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Nonnull;
import java.util.UUID;

@Component
public class GrpcArtistClient extends RococoArtistServiceGrpc.RococoArtistServiceImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcArtistClient.class);

    @GrpcClient("grpcArtistClient")
    RococoArtistServiceGrpc.RococoArtistServiceBlockingStub rococoArtistServiceStub;

    public Artist getArtistById(@Nonnull UUID id) {
        try {
            return rococoArtistServiceStub.getArtistById(
                    ArtistRequest.newBuilder()
                            .setId(id.toString()).build());
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist with ID " + id + " not found", e);
            } else {
                LOG.error("### Error while calling gRPC server ", e);
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
            }
        }
    }
}
