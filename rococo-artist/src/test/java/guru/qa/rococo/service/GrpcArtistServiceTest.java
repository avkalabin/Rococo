package guru.qa.rococo.service;

import guru.qa.grpc.rococo.AllArtistsRequest;
import guru.qa.grpc.rococo.AllArtistsResponse;
import guru.qa.grpc.rococo.Artist;
import guru.qa.grpc.rococo.ArtistRequest;
import guru.qa.rococo.data.ArtistEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrpcArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private GrpcArtistService grpcArtistService;

    private final UUID artistId = UUID.randomUUID();
    private ArtistEntity artistEntity;

    @BeforeEach
    void setUp() {
        artistEntity = new ArtistEntity();
        artistEntity.setId(artistId);
        artistEntity.setName("Test Artist");
        artistEntity.setBiography("Test Biography");
        artistEntity.setPhoto("photoData".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void getAllArtistsShouldReturnArtistsList() {
        AllArtistsRequest request = AllArtistsRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<ArtistEntity> artistPage = new PageImpl<>(List.of(artistEntity));

        when(artistRepository.findAll(pageRequest)).thenReturn(artistPage);

        StreamObserver<AllArtistsResponse> responseObserver = mock(StreamObserver.class);
        grpcArtistService.getAllArtists(request, responseObserver);

        ArgumentCaptor<AllArtistsResponse> responseCaptor = ArgumentCaptor.forClass(AllArtistsResponse.class);
        verify(responseObserver, times(1)).onNext(responseCaptor.capture());
        verify(responseObserver, times(1)).onCompleted();

        AllArtistsResponse response = responseCaptor.getValue();
        assertEquals(1, response.getArtistsCount());
        assertEquals("Test Artist", response.getArtists(0).getName());
    }

    @Test
    void getArtistByIdShouldReturnArtistWhenFound() {
        ArtistRequest request = ArtistRequest.newBuilder()
                .setId(artistId.toString())
                .build();
        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artistEntity));

        StreamObserver<Artist> responseObserver = mock(StreamObserver.class);
        grpcArtistService.getArtistById(request, responseObserver);

        ArgumentCaptor<Artist> artistCaptor = ArgumentCaptor.forClass(Artist.class);
        verify(responseObserver, times(1)).onNext(artistCaptor.capture());
        verify(responseObserver, times(1)).onCompleted();

        Artist artist = artistCaptor.getValue();
        assertEquals(artistId.toString(), artist.getId());
        assertEquals("Test Artist", artist.getName());
        assertEquals("Test Biography", artist.getBiography());
    }

    @Test
    void getArtistByIdShouldReturnNotFoundWhenArtistDoesNotExist() {
        ArtistRequest request = ArtistRequest.newBuilder()
                .setId(UUID.randomUUID().toString())
                .build();
        when(artistRepository.findById(any())).thenReturn(Optional.empty());

        StreamObserver<Artist> responseObserver = mock(StreamObserver.class);
        grpcArtistService.getArtistById(request, responseObserver);

        ArgumentCaptor<Throwable> throwableCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(responseObserver, times(1)).onError(throwableCaptor.capture());

        Throwable throwable = throwableCaptor.getValue();
        assertTrue(throwable instanceof StatusRuntimeException);
        StatusRuntimeException statusRuntimeException = (StatusRuntimeException) throwable;
        assertEquals(Status.NOT_FOUND.getCode(), statusRuntimeException.getStatus().getCode());
    }

    @Test
    void createArtistShouldSaveAndReturnArtist() {
        Artist request = Artist.newBuilder()
                .setName("New Artist")
                .setBiography("New Biography")
                .setPhoto("newPhoto")
                .build();

        when(artistRepository.save(any())).thenAnswer(invocation -> {
            ArtistEntity entity = invocation.getArgument(0);
            entity.setId(UUID.randomUUID());
            return entity;
        });

        StreamObserver<Artist> responseObserver = mock(StreamObserver.class);
        grpcArtistService.createArtist(request, responseObserver);

        ArgumentCaptor<ArtistEntity> artistEntityCaptor = ArgumentCaptor.forClass(ArtistEntity.class);
        verify(artistRepository, times(1)).save(artistEntityCaptor.capture());

        ArtistEntity savedEntity = artistEntityCaptor.getValue();
        assertEquals("New Artist", savedEntity.getName());
        assertEquals("New Biography", savedEntity.getBiography());
        assertEquals("newPhoto", new String(savedEntity.getPhoto(), StandardCharsets.UTF_8));

        ArgumentCaptor<Artist> artistCaptor = ArgumentCaptor.forClass(Artist.class);
        verify(responseObserver, times(1)).onNext(artistCaptor.capture());
        verify(responseObserver, times(1)).onCompleted();

        Artist responseArtist = artistCaptor.getValue();
        assertEquals("New Artist", responseArtist.getName());
        assertEquals("New Biography", responseArtist.getBiography());
        assertEquals("newPhoto", responseArtist.getPhoto());
    }

    @Test
    void updateArtistShouldUpdateAndReturnArtistWhenFound() {
        Artist request = Artist.newBuilder()
                .setId(artistId.toString())
                .setName("Updated Name")
                .setBiography("Updated Biography")
                .setPhoto("updatedPhoto")
                .build();

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artistEntity));
        when(artistRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        StreamObserver<Artist> responseObserver = mock(StreamObserver.class);
        grpcArtistService.updateArtist(request, responseObserver);

        ArgumentCaptor<ArtistEntity> artistEntityCaptor = ArgumentCaptor.forClass(ArtistEntity.class);
        verify(artistRepository, times(1)).save(artistEntityCaptor.capture());

        ArtistEntity updatedEntity = artistEntityCaptor.getValue();
        assertEquals("Updated Name", updatedEntity.getName());
        assertEquals("Updated Biography", updatedEntity.getBiography());
        assertEquals("updatedPhoto", new String(updatedEntity.getPhoto(), StandardCharsets.UTF_8));

        ArgumentCaptor<Artist> artistCaptor = ArgumentCaptor.forClass(Artist.class);
        verify(responseObserver, times(1)).onNext(artistCaptor.capture());
        verify(responseObserver, times(1)).onCompleted();

        Artist responseArtist = artistCaptor.getValue();
        assertEquals("Updated Name", responseArtist.getName());
        assertEquals("Updated Biography", responseArtist.getBiography());
        assertEquals("updatedPhoto", responseArtist.getPhoto());
    }

    @Test
    void updateArtistShouldReturnNotFoundWhenArtistDoesNotExist() {
        Artist request = Artist.newBuilder()
                .setId(UUID.randomUUID().toString())
                .build();
        when(artistRepository.findById(any())).thenReturn(Optional.empty());

        StreamObserver<Artist> responseObserver = mock(StreamObserver.class);
        grpcArtistService.updateArtist(request, responseObserver);

        ArgumentCaptor<Throwable> throwableCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(responseObserver, times(1)).onError(throwableCaptor.capture());

        Throwable throwable = throwableCaptor.getValue();
        assertTrue(throwable instanceof StatusRuntimeException);
        StatusRuntimeException statusRuntimeException = (StatusRuntimeException) throwable;
        assertEquals(Status.NOT_FOUND.getCode(), statusRuntimeException.getStatus().getCode());
    }
}
