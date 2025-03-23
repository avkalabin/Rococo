package guru.qa.rococo.service.api;

import guru.qa.grpc.rococo.Country;
import guru.qa.grpc.rococo.CountryRequest;
import guru.qa.grpc.rococo.RococoGeoServiceGrpc;
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
public class GrpcGeoClient {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcGeoClient.class);

    @GrpcClient("grpcGeoClient")
    private RococoGeoServiceGrpc.RococoGeoServiceBlockingStub rococoGeoServiceStub;

    public Country getCountryById(@Nonnull UUID geoId) {
        try {
            return rococoGeoServiceStub.getCountryById(
                    CountryRequest.newBuilder()
                            .setId(geoId.toString())
                            .build());
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Country with ID " + geoId + " not found", e);
            } else {
                LOG.error("### Error while calling gRPC server ", e);
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
            }
        }
    }
}
