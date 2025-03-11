package guru.qa.rococo.test.grpc;

import guru.qa.grpc.rococo.*;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.annotation.meta.GrpcTest;
import guru.qa.rococo.model.CountryJson;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.qameta.allure.grpc.AllureGrpc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static guru.qa.rococo.model.CountryJson.fromGrpc;
import static guru.qa.rococo.utils.CustomAssert.check;
import static guru.qa.rococo.utils.RandomDataUtils.randomCountry;
import static io.qameta.allure.Allure.step;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@GrpcTest
@DisplayName("Geo gRPC tests")
public class GeoGrpcTest {

    private static final Config CFG = Config.getInstance();
    private static final Channel geoChannel;

    static {
        geoChannel = ManagedChannelBuilder
                .forAddress(CFG.geoGrpcAddress(), CFG.geoGrpcPort())
                .intercept(new AllureGrpc())
                .usePlaintext()
                .build();
    }

    static final RococoGeoServiceGrpc.RococoGeoServiceBlockingStub geoStub
            = RococoGeoServiceGrpc.newBlockingStub(geoChannel);

    @Test
    @DisplayName("Should return all countries based on pagination")
    void shouldReturnAllCountries() {

        AllCountriesRequest request = AllCountriesRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .build();

        AllCountriesResponse response =  geoStub.getAllCountries(request);

        step("Check countries in response", () -> {
            check("country count in response should be 10",
                    response.getCountriesList().size(), equalTo(10));
        });
    }

    @Test
    @DisplayName("Should return country by ID")
    void shouldReturnCountryById() {
       final CountryJson country = randomCountry();
        CountryRequest request = CountryRequest.newBuilder()
                .setId(country.id().toString())
                .build();

        final Country response = geoStub.getCountryById(request);

        step("Check country in response", () -> assertEquals(country, fromGrpc(response)));
    }

    @Test
    @DisplayName("Should return NOT_FOUND error if send not existing country id")
    void shouldReturnNotFoundErrorIfSendNotExistingCountryId() {
        String notExistingId = UUID.randomUUID().toString();
        CountryRequest request = CountryRequest.newBuilder()
                .setId((notExistingId))
                .build();

        final StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> geoStub.getCountryById(request));

        step("Check exception status code and description", () -> {
            assertEquals(Status.NOT_FOUND.getCode(), exception.getStatus().getCode());
            assertEquals("Country not found by id: " + notExistingId, exception.getStatus().getDescription());
        });
    }
}
