package guru.qa.rococo.service;

import guru.qa.grpc.rococo.*;
import guru.qa.rococo.data.ArtistEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

import static io.grpc.Status.NOT_FOUND;
import static java.nio.charset.StandardCharsets.UTF_8;

@GrpcService
public class GrpcArtistService extends RococoArtistServiceGrpc.RococoArtistServiceImplBase {

    private final ArtistRepository artistRepository;

    @Autowired
    public GrpcArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    public void getAllArtists(AllArtistsRequest request, StreamObserver<AllArtistsResponse> responseObserver) {
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
    public void getArtistById(ArtistRequest request, StreamObserver<Artist> responseObserver) {
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
    public void createArtist(Artist request, StreamObserver<Artist> responseObserver) {
        ArtistEntity artistEntity = toEntity(request);
        artistRepository.save(artistEntity);
        responseObserver.onNext(toGrpc(artistEntity));
        responseObserver.onCompleted();
    }


    @Override
    public void updateArtist(Artist request, StreamObserver<Artist> responseObserver) {
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
                        },
                        () -> responseObserver.onError(
                                NOT_FOUND.withDescription("Artist not found by id: " + request.getId())
                                        .asRuntimeException()
                        )
                );
    }

    private Artist toGrpc(ArtistEntity artistEntity) {
        return Artist
                .newBuilder()
                .setId(artistEntity.getId().toString())
                .setName(artistEntity.getName())
                .setBiography(artistEntity.getBiography())
                .setPhoto(new String(artistEntity.getPhoto(), UTF_8))
                .build();
    }

    private ArtistEntity toEntity(Artist request) {
        ArtistEntity artistEntity = new ArtistEntity();
        artistEntity.setId(request.getId().isEmpty() ? null : UUID.fromString(request.getId()));
        artistEntity.setName(request.getName());
        artistEntity.setBiography(request.getBiography());
        artistEntity.setPhoto(request.getPhoto().getBytes(UTF_8));
        return artistEntity;
    }
}
