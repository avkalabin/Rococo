package guru.qa.rococo.service;

import guru.qa.grpc.rococo.*;
import guru.qa.rococo.data.ArtistEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.model.EventType;
import guru.qa.rococo.model.LogJson;
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
public class GrpcArtistService extends RococoArtistServiceGrpc.RococoArtistServiceImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcArtistService.class);

    private final KafkaTemplate<String, LogJson> kafkaTemplate;
    private final ArtistRepository artistRepository;

    @Autowired
    public GrpcArtistService(ArtistRepository artistRepository,
                             KafkaTemplate<String, LogJson> kafkaTemplate) {
        this.artistRepository = artistRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void getAllArtists(@Nonnull AllArtistsRequest request,
                              @Nonnull StreamObserver<AllArtistsResponse> responseObserver) {
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());
        Page<ArtistEntity> artistEntities = request.getName().isEmpty() ? artistRepository.findAll(pageRequest)
                : artistRepository.findAllByNameContainsIgnoreCase(request.getName(), pageRequest);
        responseObserver.onNext(
                AllArtistsResponse.newBuilder()
                        .addAllArtists(
                                artistEntities.stream()
                                        .map(this::toGrpc)
                                        .toList()
                        )
                        .setTotalCount(artistEntities.getTotalElements())
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void getArtistById(@Nonnull ArtistRequest request,
                              StreamObserver<Artist> responseObserver) {
        artistRepository.findById(UUID.fromString(request.getId()))
                .ifPresentOrElse(
                        artistEntity -> {
                            Artist artist = toGrpc(artistEntity);
                            responseObserver.onNext(artist);
                            responseObserver.onCompleted();
                        },
                        () -> responseObserver.onError(
                                NOT_FOUND.withDescription("Artist not found by id: " + request.getId())
                                        .asRuntimeException()
                        )
                );
    }

    @Override
    public void createArtist(Artist request,
                             @Nonnull StreamObserver<Artist> responseObserver) {
        ArtistEntity artistEntity = toEntity(request);
        artistRepository.save(artistEntity);
        responseObserver.onNext(toGrpc(artistEntity));
        responseObserver.onCompleted();

        LogJson logJson = new LogJson(
                EventType.ARTIST_CREATED,
                artistEntity.getId(),
                "Artist " + artistEntity.getName() + " successfully created",
                Instant.now());
        kafkaTemplate.send("artist", logJson);
        LOG.info("### Kafka topic [artist] sent message: {}", logJson);
    }


    @Override
    public void updateArtist(@Nonnull Artist request,
                             StreamObserver<Artist> responseObserver) {
        artistRepository.findById(UUID.fromString(request.getId()))
                .ifPresentOrElse(
                        artistEntity -> {
                            artistEntity.setName(request.getName());
                            artistEntity.setBiography(request.getBiography());
                            artistEntity.setPhoto(request.getPhoto().getBytes(UTF_8));
                            artistRepository.save(artistEntity);
                            Artist artist = toGrpc(artistEntity);
                            responseObserver.onNext(artist);
                            responseObserver.onCompleted();

                            LogJson logJson = new LogJson(
                                    EventType.ARTIST_UPDATED,
                                    artistEntity.getId(),
                                    "Artist " + artistEntity.getName() + " successfully updated",
                                    Instant.now());
                            kafkaTemplate.send("artist", logJson);
                            LOG.info("### Kafka topic [artist] sent message: {}", logJson);
                        },
                        () -> responseObserver.onError(
                                NOT_FOUND.withDescription("Artist not found by id: " + request.getId())
                                        .asRuntimeException()
                        )
                );
    }

    @Nonnull
    private Artist toGrpc(@Nonnull ArtistEntity artistEntity) {
        return Artist
                .newBuilder()
                .setId(artistEntity.getId().toString())
                .setName(artistEntity.getName())
                .setBiography(artistEntity.getBiography())
                .setPhoto(new String(artistEntity.getPhoto(), UTF_8))
                .build();
    }

    @Nonnull
    private ArtistEntity toEntity(@Nonnull Artist request) {
        ArtistEntity artistEntity = new ArtistEntity();
        artistEntity.setId(request.getId().isEmpty() ? null : UUID.fromString(request.getId()));
        artistEntity.setName(request.getName());
        artistEntity.setBiography(request.getBiography());
        artistEntity.setPhoto(request.getPhoto().getBytes(UTF_8));
        return artistEntity;
    }
}
