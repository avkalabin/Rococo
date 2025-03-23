package guru.qa.rococo.service;

import guru.qa.grpc.rococo.*;
import guru.qa.rococo.data.PaintingEntity;
import guru.qa.rococo.data.repository.PaintingRepository;
import guru.qa.rococo.model.EventType;
import guru.qa.rococo.model.LogJson;
import guru.qa.rococo.service.api.GrpcArtistClient;
import guru.qa.rococo.service.api.GrpcMuseumClient;
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
public class GrpcPaintingService extends RococoPaintingServiceGrpc.RococoPaintingServiceImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcPaintingService.class);

    private final KafkaTemplate<String, LogJson> kafkaTemplate;
    private final PaintingRepository paintingRepository;
    private final GrpcMuseumClient grpcMuseumClient;
    private final GrpcArtistClient grpcArtistClient;

    @Autowired
    public GrpcPaintingService(KafkaTemplate<String, LogJson> kafkaTemplate,
                               PaintingRepository paintingRepository,
                               GrpcMuseumClient grpcMuseumClient,
                               GrpcArtistClient grpcArtistClient) {
        this.kafkaTemplate = kafkaTemplate;
        this.paintingRepository = paintingRepository;
        this.grpcMuseumClient = grpcMuseumClient;
        this.grpcArtistClient = grpcArtistClient;
    }

    @Override
    public void getAllPaintings(@Nonnull AllPaintingsRequest request,
                                @Nonnull StreamObserver<AllPaintingsResponse> responseObserver) {
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());
        Page<PaintingEntity> paintingPage = request.getTitle().isEmpty() ? paintingRepository.findAll(pageRequest)
                : paintingRepository.findAllByTitleContainsIgnoreCase(request.getTitle(), pageRequest);
        responseObserver.onNext(
                AllPaintingsResponse.newBuilder()
                        .addAllPaintings(
                                paintingPage.stream()
                                        .map(this::toGrpc)
                                        .toList()
                        )
                        .setTotalCount(paintingPage.getTotalElements())
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void getPaintingById(@Nonnull PaintingRequest request,
                                StreamObserver<Painting> responseObserver) {
        paintingRepository.findById(UUID.fromString(request.getId()))
                .ifPresentOrElse(
                        paintingEntity -> {
                            Painting painting = toGrpc(paintingEntity);
                            responseObserver.onNext(painting);
                            responseObserver.onCompleted();
                        },
                        () -> responseObserver.onError(NOT_FOUND.withDescription("Painting not found by id: " + request.getId()).asRuntimeException())
                );
    }

    @Override
    public void getPaintingByArtist(@Nonnull PaintingByArtistRequest request,
                                    @Nonnull StreamObserver<AllPaintingsResponse> responseObserver) {
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());
        Page<PaintingEntity> paintingEntities = paintingRepository.findAllByArtistId(UUID.fromString(request.getArtist().getId()), pageRequest);
        responseObserver.onNext(
                AllPaintingsResponse.newBuilder()
                        .addAllPaintings(
                                paintingEntities.stream()
                                        .map(this::toGrpc)
                                        .toList()
                        )
                        .setTotalCount(paintingEntities.getTotalElements())
                        .build()
        );
        responseObserver.onCompleted();

    }

    @Override
    public void createPainting(Painting request,
                               @Nonnull StreamObserver<Painting> responseObserver) {
        PaintingEntity paintingEntity = toEntity(request);
        paintingRepository.save(paintingEntity);
        responseObserver.onNext(toGrpc(paintingEntity));
        responseObserver.onCompleted();

        LogJson logJson = new LogJson(
                EventType.PAINTING_CREATED,
                paintingEntity.getId(),
                "Painting " + paintingEntity.getTitle() + " successfully created",
                Instant.now());
        kafkaTemplate.send("painting", logJson);
        LOG.info("### Kafka topic [painting] sent message: {}", logJson);
    }

    @Override
    public void updatePainting(@Nonnull Painting request,
                               StreamObserver<Painting> responseObserver) {
        paintingRepository.findById(UUID.fromString(request.getId()))
                .ifPresentOrElse(
                        paintingEntity -> {
                            paintingEntity.setTitle(request.getTitle());
                            paintingEntity.setDescription(request.getDescription());
                            paintingEntity.setContent(request.getContent().getBytes(UTF_8));
                            paintingEntity.setMuseumId(UUID.fromString(request.getMuseum().getId()));
                            paintingEntity.setArtistId(UUID.fromString(request.getArtist().getId()));
                            paintingRepository.save(paintingEntity);
                            Painting painting = toGrpc(paintingEntity);
                            responseObserver.onNext(painting);
                            responseObserver.onCompleted();

                            LogJson logJson = new LogJson(
                                    EventType.PAINTING_UPDATED,
                                    paintingEntity.getId(),
                                    "Painting " + paintingEntity.getTitle() + " successfully updated",
                                    Instant.now());
                            kafkaTemplate.send("painting", logJson);
                            LOG.info("### Kafka topic [painting] sent message: {}", logJson);
                        },
                        () -> responseObserver.onError(NOT_FOUND.withDescription("Painting not found by id: " + request.getId()).asRuntimeException())
                );
    }

    @Nonnull
    private Painting toGrpc(@Nonnull PaintingEntity paintingEntity) {

        Museum museum = grpcMuseumClient.getMuseumById(paintingEntity.getMuseumId());
        Artist artist = grpcArtistClient.getArtistById(paintingEntity.getArtistId());

        return Painting.newBuilder()
                .setId(paintingEntity.getId().toString())
                .setTitle(paintingEntity.getTitle())
                .setDescription(paintingEntity.getDescription())
                .setContent(new String(paintingEntity.getContent(), UTF_8))
                .setMuseum(museum)
                .setArtist(artist)
                .build();
    }

    @Nonnull
    private PaintingEntity toEntity(@Nonnull Painting request) {
        PaintingEntity paintingEntity = new PaintingEntity();
        paintingEntity.setId(request.getId().isEmpty() ? null : UUID.fromString(request.getId()));
        paintingEntity.setTitle(request.getTitle());
        paintingEntity.setDescription(request.getDescription());
        paintingEntity.setContent(request.getContent().getBytes(UTF_8));
        paintingEntity.setMuseumId(UUID.fromString(request.getMuseum().getId()));
        paintingEntity.setArtistId(UUID.fromString(request.getArtist().getId()));
        return paintingEntity;
    }
}
