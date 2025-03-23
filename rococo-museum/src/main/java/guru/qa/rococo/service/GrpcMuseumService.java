package guru.qa.rococo.service;

import guru.qa.grpc.rococo.*;
import guru.qa.rococo.data.MuseumEntity;
import guru.qa.rococo.data.repository.MuseumRepository;
import guru.qa.rococo.model.EventType;
import guru.qa.rococo.model.LogJson;
import guru.qa.rococo.service.api.GrpcGeoClient;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.UUID;

import static io.grpc.Status.NOT_FOUND;
import static java.nio.charset.StandardCharsets.UTF_8;

@GrpcService
public class GrpcMuseumService extends RococoMuseumServiceGrpc.RococoMuseumServiceImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcMuseumService.class);

    private final KafkaTemplate<String, LogJson> kafkaTemplate;
    private final MuseumRepository museumRepository;
    private final GrpcGeoClient grpcGeoClient;

    @Autowired
    public GrpcMuseumService(KafkaTemplate<String, LogJson> kafkaTemplate,
                             MuseumRepository museumRepository,
                             GrpcGeoClient grpcGeoClient) {
        this.kafkaTemplate = kafkaTemplate;
        this.museumRepository = museumRepository;
        this.grpcGeoClient = grpcGeoClient;
    }

    @Override
    public void getAllMuseums(@Nonnull AllMuseumsRequest request,
                              @Nonnull StreamObserver<AllMuseumsResponse> responseObserver) {
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
    public void getMuseumById(@Nonnull MuseumRequest request,
                              StreamObserver<Museum> responseObserver) {
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
    public void createMuseum(Museum request,
                             @Nonnull StreamObserver<Museum> responseObserver) {
        MuseumEntity museumEntity = toEntity(request);
        museumRepository.save(museumEntity);
        responseObserver.onNext(toGrpc(museumEntity));
        responseObserver.onCompleted();

        LogJson logJson = new LogJson(
                EventType.MUSEUM_CREATED,
                museumEntity.getId(),
                "Museum " + museumEntity.getTitle() + " successfully created",
                Instant.now());
        kafkaTemplate.send("museum", logJson);
        LOG.info("### Kafka topic [museum] sent message: {}", logJson);
    }

    @Override
    public void updateMuseum(@Nonnull Museum request,
                             StreamObserver<Museum> responseObserver) {
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

                            LogJson logJson = new LogJson(
                                    EventType.MUSEUM_UPDATED,
                                    museumEntity.getId(),
                                    "Museum " + museumEntity.getTitle() + " successfully updated",
                                    Instant.now());
                            kafkaTemplate.send("museum", logJson);
                            LOG.info("### Kafka topic [museum] sent message: {}", logJson);
                        },
                        () -> responseObserver.onError(
                                NOT_FOUND.withDescription("Museum not found by id: " + request.getId())
                                        .asRuntimeException()
                        )
                );
    }

    @Nonnull
    private Museum toGrpc(@Nonnull MuseumEntity museumEntity) {
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

    @Nonnull
    private MuseumEntity toEntity(@Nonnull Museum request) {
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
