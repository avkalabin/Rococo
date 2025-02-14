package guru.qa.rococo.service;

import guru.qa.grpc.rococo.*;
import guru.qa.rococo.data.CountryEntity;
import guru.qa.rococo.data.repository.CountryRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@GrpcService
public class GrpcGeoService extends RococoGeoServiceGrpc.RococoGeoServiceImplBase {

    private final CountryRepository countryRepository;

    @Autowired
    public GrpcGeoService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }


    @Override
    public void getAllCountries(AllCountriesRequest request, StreamObserver<AllCountriesResponse> responseObserver) {
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

    private Country toGrpc(CountryEntity countryEntity) {
        return Country
                .newBuilder()
                .setId(countryEntity.getId().toString())
                .setName(countryEntity.getName())
                .build();
    }
}
