package guru.qa.rococo.service;

import guru.qa.grpc.rococo.*;
import guru.qa.rococo.data.CountryEntity;
import guru.qa.rococo.data.repository.CountryRepository;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

import static io.grpc.Status.NOT_FOUND;

@GrpcService
public class GrpcGeoService extends RococoGeoServiceGrpc.RococoGeoServiceImplBase {

    private final CountryRepository countryRepository;

    @Autowired
    public GrpcGeoService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }


    @Override
    public void getAllCountries(@Nonnull AllCountriesRequest request,
                                @Nonnull StreamObserver<AllCountriesResponse> responseObserver) {
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());
        Page<CountryEntity> countryEntities = countryRepository.findAll(pageRequest);
        responseObserver.onNext(
                AllCountriesResponse.newBuilder()
                        .addAllCountries(
                                countryEntities.stream()
                                        .map(this::toGrpc)
                                        .toList()
                        )
                        .setTotalCount(countryEntities.getTotalElements())
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void getCountryById(@Nonnull CountryRequest request,
                               StreamObserver<Country> responseObserver) {
        countryRepository.findById(UUID.fromString(request.getId()))
                .ifPresentOrElse(
                        countryEntity -> {
                            Country country = toGrpc(countryEntity);
                            responseObserver.onNext(country);
                            responseObserver.onCompleted();
                        },
                        () -> responseObserver.onError(NOT_FOUND.withDescription("Country not found by id: " + request.getId()).asRuntimeException())
                );
    }

    @Nonnull
    private Country toGrpc(@Nonnull CountryEntity countryEntity) {
        return Country
                .newBuilder()
                .setId(countryEntity.getId().toString())
                .setName(countryEntity.getName())
                .build();
    }
}
