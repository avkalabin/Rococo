package guru.qa.rococo.service.api;

import guru.qa.grpc.rococo.AllCountriesRequest;
import guru.qa.grpc.rococo.AllCountriesResponse;
import guru.qa.grpc.rococo.RococoGeoServiceGrpc;
import guru.qa.rococo.model.CountryJson;
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

@Component
public class GrpcGeoClient {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcGeoClient.class);

    @GrpcClient("grpcGeoClient")
    private RococoGeoServiceGrpc.RococoGeoServiceBlockingStub rococoGeoServiceStub;

    public Page<CountryJson> getAllCountry(Pageable pageable) {
        AllCountriesRequest request = AllCountriesRequest.newBuilder()
                .setPage(pageable.getPageNumber())
                .setSize(pageable.getPageSize())
                .build();

        try {
            AllCountriesResponse response = rococoGeoServiceStub.getAllCountries(request);
            List<CountryJson> countryJsonList = response.getCountriesList()
                    .stream()
                    .map(CountryJson::fromGrpc)
                    .toList();
            LOG.info("Successfully fetched {} artists", countryJsonList.size());
            return new PageImpl<>(countryJsonList, pageable, response.getTotalCount());
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }
}
