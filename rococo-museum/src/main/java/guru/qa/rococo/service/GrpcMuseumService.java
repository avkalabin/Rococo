package guru.qa.rococo.service;

import guru.qa.grpc.rococo.*;
import guru.qa.rococo.data.MuseumEntity;
import guru.qa.rococo.data.repository.MuseumRepository;
import guru.qa.rococo.service.api.GrpcGeoClient;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

import static io.grpc.Status.NOT_FOUND;
import static java.nio.charset.StandardCharsets.UTF_8;

@GrpcService
public class GrpcMuseumService extends RococoMuseumServiceGrpc.RococoMuseumServiceImplBase {

    private final MuseumRepository museumRepository;
    private final GrpcGeoClient grpcGeoClient;

    @Autowired
    public GrpcMuseumService(MuseumRepository museumRepository, GrpcGeoClient grpcGeoClient) {
        this.museumRepository = museumRepository;
        this.grpcGeoClient = grpcGeoClient;
    }

    @Override
    public void getAllMuseums(AllMuseumsRequest request, StreamObserver<AllMuseumsResponse> responseObserver) {
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());
        Page<MuseumEntity> museumEntities = request.getTitle().isEmpty() ? museumRepository.findAll(pageRequest)
                : museumRepository.findAllByTitleContainsIgnoreCase(request.getTitle(), pageRequest);
        responseObserver.onNext(
                AllMuseumsResponse.newBuilder()
                        .addAllMuseums(
                                museumEntities.stream()
                                        .map(this::toGrpc)
                                        .toList()
                        )
                        .setTotalCount(museumEntities.getTotalElements())
                        .build());
        responseObserver.onCompleted();
    }

    @Override
    public void getMuseumById(MuseumRequest request, StreamObserver<Museum> responseObserver) {
        museumRepository.findById(UUID.fromString(request.getId()))
                .ifPresentOrElse(
                        museumEntity -> {
                            Museum museum = toGrpc(museumEntity);
                            responseObserver.onNext(museum);
                            responseObserver.onCompleted();
                        },
                        () -> responseObserver.onError(NOT_FOUND.withDescription("Museum not found by id: " + request.getId()).asRuntimeException())
                );
    }

    @Override
    public void createMuseum(Museum request, StreamObserver<Museum> responseObserver) {
        MuseumEntity museumEntity = toEntity(request);
        museumRepository.save(museumEntity);
        responseObserver.onNext(toGrpc(museumEntity));
        responseObserver.onCompleted();
    }

    @Override
    public void updateMuseum(Museum request, StreamObserver<Museum> responseObserver) {
        museumRepository.findById(UUID.fromString(request.getId()))
                .ifPresentOrElse(
                        museumEntity -> {
                            museumEntity.setTitle(request.getTitle());
                            museumEntity.setDescription(request.getDescription());
                            museumEntity.setPhoto(request.getPhoto().getBytes(UTF_8));
                            museumEntity.setCity(request.getGeo().getCity());
                            museumEntity.setGeoId(UUID.fromString(request.getGeo().getCountry().getId()));
                            museumRepository.save(museumEntity);
                            Museum museum = toGrpc(museumEntity);
                            responseObserver.onNext(museum);
                            responseObserver.onCompleted();
                        },
                        () -> responseObserver.onError(
                                NOT_FOUND.withDescription("Museum not found by id: " + request.getId())
                                        .asRuntimeException()
                        )
                );
    }

    private Museum toGrpc(MuseumEntity museumEntity) {
        Country country = grpcGeoClient.getCountryById(museumEntity.getGeoId());

        Geo geo = Geo.newBuilder()
                .setCity(museumEntity.getCity())
                .setCountry(country)
                .build();

        return Museum
                .newBuilder()
                .setId(museumEntity.getId().toString())
                .setTitle(museumEntity.getTitle())
                .setDescription(museumEntity.getDescription())
                .setPhoto(new String(museumEntity.getPhoto(), UTF_8))
                .setGeo(geo)
                .build();
    }

    private MuseumEntity toEntity(Museum request) {
        MuseumEntity museumEntity = new MuseumEntity();
        museumEntity.setId(request.getId().isEmpty() ? null : UUID.fromString(request.getId()));
        museumEntity.setTitle(request.getTitle());
        museumEntity.setDescription(request.getDescription());
        museumEntity.setPhoto(request.getPhoto().getBytes(UTF_8));
        museumEntity.setCity(request.getGeo().getCity());
        museumEntity.setGeoId(UUID.fromString(request.getGeo().getCountry().getId()));
        return museumEntity;
    }
}
