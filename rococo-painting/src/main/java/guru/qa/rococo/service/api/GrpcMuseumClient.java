package guru.qa.rococo.service.api;

import guru.qa.grpc.rococo.Museum;
import guru.qa.grpc.rococo.MuseumRequest;
import guru.qa.grpc.rococo.RococoMuseumServiceGrpc;
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
public class GrpcMuseumClient extends RococoMuseumServiceGrpc.RococoMuseumServiceImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcMuseumClient.class);

    @GrpcClient("grpcMuseumClient")
    private RococoMuseumServiceGrpc.RococoMuseumServiceBlockingStub rococoMuseumServiceStub;

    public Museum getMuseumById(@Nonnull UUID id) {
        try {
            return rococoMuseumServiceStub.getMuseumById(
                    MuseumRequest.newBuilder()
                            .setId(id.toString()).build());
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Museum with ID " + id + " not found", e);
            } else {
                LOG.error("### Error while calling gRPC server ", e);
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
            }
        }
    }
}
