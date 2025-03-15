package guru.qa.rococo.service;

import guru.qa.grpc.rococo.*;
import guru.qa.rococo.data.PaintingEntity;
import guru.qa.rococo.data.repository.PaintingRepository;
import guru.qa.rococo.service.api.GrpcArtistClient;
import guru.qa.rococo.service.api.GrpcMuseumClient;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrpcPaintingServiceTest {

    @Mock
    private PaintingRepository paintingRepository;

    @Mock
    private GrpcMuseumClient grpcMuseumClient;

    @Mock
    private GrpcArtistClient grpcArtistClient;

    @InjectMocks
    private GrpcPaintingService grpcPaintingService;

    private final UUID paintingId = UUID.randomUUID();
    private final UUID museumId = UUID.randomUUID();
    private final UUID artistId = UUID.randomUUID();
    private PaintingEntity paintingEntity;

    @BeforeEach
    void setUp() {
        paintingEntity = new PaintingEntity();
        paintingEntity.setId(paintingId);
        paintingEntity.setTitle("Test Painting");
        paintingEntity.setDescription("Test Description");
        paintingEntity.setContent("contentData".getBytes(StandardCharsets.UTF_8));
        paintingEntity.setMuseumId(museumId);
        paintingEntity.setArtistId(artistId);
    }

    @Test
    void getAllPaintingsShouldReturnPaintingsList() {
        AllPaintingsRequest request = AllPaintingsRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .setTitle("Test")
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<PaintingEntity> paintingPage = new PageImpl<>(List.of(paintingEntity));

        when(paintingRepository.findAllByTitleContainsIgnoreCase("Test", pageRequest)).thenReturn(paintingPage);

        Museum museum = Museum.newBuilder()
                .setId(museumId.toString())
                .setTitle("Test Museum")
                .build();
        when(grpcMuseumClient.getMuseumById(museumId)).thenReturn(museum);

        Artist artist = Artist.newBuilder()
                .setId(artistId.toString())
                .setName("Test Artist")
                .build();
        when(grpcArtistClient.getArtistById(artistId)).thenReturn(artist);

        StreamObserver<AllPaintingsResponse> responseObserver = mock(StreamObserver.class);

        grpcPaintingService.getAllPaintings(request, responseObserver);

        ArgumentCaptor<AllPaintingsResponse> responseCaptor = ArgumentCaptor.forClass(AllPaintingsResponse.class);
        verify(responseObserver, times(1)).onNext(responseCaptor.capture());
        verify(responseObserver, times(1)).onCompleted();

        AllPaintingsResponse response = responseCaptor.getValue();
        assertEquals(1, response.getPaintingsCount());
        assertEquals("Test Painting", response.getPaintings(0).getTitle());
        assertEquals("Test Description", response.getPaintings(0).getDescription());
        assertEquals("Test Museum", response.getPaintings(0).getMuseum().getTitle());
        assertEquals("Test Artist", response.getPaintings(0).getArtist().getName());
        assertEquals(1, response.getTotalCount());
    }

    @Test
    void getPaintingByIdShouldReturnPaintingWhenFound() {
        PaintingRequest request = PaintingRequest.newBuilder()
                .setId(paintingId.toString())
                .build();
        when(paintingRepository.findById(paintingId)).thenReturn(Optional.of(paintingEntity));

        Museum museum = Museum.newBuilder()
                .setId(museumId.toString())
                .setTitle("Test Museum")
                .build();
        when(grpcMuseumClient.getMuseumById(museumId)).thenReturn(museum);

        Artist artist = Artist.newBuilder()
                .setId(artistId.toString())
                .setName("Test Artist")
                .build();
        when(grpcArtistClient.getArtistById(artistId)).thenReturn(artist);

        StreamObserver<Painting> responseObserver = mock(StreamObserver.class);

        grpcPaintingService.getPaintingById(request, responseObserver);

        ArgumentCaptor<Painting> paintingCaptor = ArgumentCaptor.forClass(Painting.class);
        verify(responseObserver, times(1)).onNext(paintingCaptor.capture());
        verify(responseObserver, times(1)).onCompleted();

        Painting responsePainting = paintingCaptor.getValue();
        assertEquals(paintingId.toString(), responsePainting.getId());
        assertEquals("Test Painting", responsePainting.getTitle());
        assertEquals("Test Description", responsePainting.getDescription());
        assertEquals("Test Museum", responsePainting.getMuseum().getTitle());
        assertEquals("Test Artist", responsePainting.getArtist().getName());
    }

    @Test
    void getPaintingByIdShouldReturnNotFoundWhenPaintingDoesNotExist() {
        PaintingRequest request = PaintingRequest.newBuilder()
                .setId(UUID.randomUUID().toString())
                .build();
        when(paintingRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        StreamObserver<Painting> responseObserver = mock(StreamObserver.class);

        grpcPaintingService.getPaintingById(request, responseObserver);

        ArgumentCaptor<Throwable> throwableCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(responseObserver, times(1)).onError(throwableCaptor.capture());

        Throwable throwable = throwableCaptor.getValue();
        assertTrue(throwable instanceof StatusRuntimeException);
        StatusRuntimeException statusRuntimeException = (StatusRuntimeException) throwable;
        assertEquals(Status.NOT_FOUND.getCode(), statusRuntimeException.getStatus().getCode());
    }

    @Test
    void getPaintingByArtistShouldReturnPaintingsList() {
        PaintingByArtistRequest request = PaintingByArtistRequest.newBuilder()
                .setArtist(ArtistRequest.newBuilder()
                        .setId(artistId.toString())
                        .build())
                .setPage(0)
                .setSize(10)
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<PaintingEntity> paintingPage = new PageImpl<>(List.of(paintingEntity));

        when(paintingRepository.findAllByArtistId(artistId, pageRequest)).thenReturn(paintingPage);

        Museum museum = Museum.newBuilder()
                .setId(museumId.toString())
                .setTitle("Test Museum")
                .build();
        when(grpcMuseumClient.getMuseumById(museumId)).thenReturn(museum);

        Artist artist = Artist.newBuilder()
                .setId(artistId.toString())
                .setName("Test Artist")
                .build();
        when(grpcArtistClient.getArtistById(artistId)).thenReturn(artist);

        StreamObserver<AllPaintingsResponse> responseObserver = mock(StreamObserver.class);

        grpcPaintingService.getPaintingByArtist(request, responseObserver);

        ArgumentCaptor<AllPaintingsResponse> responseCaptor = ArgumentCaptor.forClass(AllPaintingsResponse.class);
        verify(responseObserver, times(1)).onNext(responseCaptor.capture());
        verify(responseObserver, times(1)).onCompleted();

        AllPaintingsResponse response = responseCaptor.getValue();
        assertEquals(1, response.getPaintingsCount());
        assertEquals("Test Painting", response.getPaintings(0).getTitle());
        assertEquals("Test Description", response.getPaintings(0).getDescription());
        assertEquals("Test Museum", response.getPaintings(0).getMuseum().getTitle());
        assertEquals("Test Artist", response.getPaintings(0).getArtist().getName());
        assertEquals(1, response.getTotalCount());
    }

    @Test
    void createPaintingShouldSaveAndReturnPainting() {
        Museum museum = Museum.newBuilder()
                .setId(museumId.toString())
                .setTitle("Test Museum")
                .build();


        Artist artist = Artist.newBuilder()
                .setId(artistId.toString())
                .setName("Test Artist")
                .build();

        Painting request = Painting.newBuilder()
                .setTitle("New Painting")
                .setDescription("New Description")
                .setContent("newContent")
                .setMuseum(museum)
                .setArtist(artist)
                .build();

        when(paintingRepository.save(any(PaintingEntity.class))).thenAnswer(invocation -> {
            PaintingEntity entity = invocation.getArgument(0);
            entity.setId(paintingId);
            return entity;
        });
        when(grpcMuseumClient.getMuseumById(museumId)).thenReturn(museum);
        when(grpcArtistClient.getArtistById(artistId)).thenReturn(artist);

        StreamObserver<Painting> responseObserver = mock(StreamObserver.class);

        grpcPaintingService.createPainting(request, responseObserver);

        ArgumentCaptor<PaintingEntity> paintingEntityCaptor = ArgumentCaptor.forClass(PaintingEntity.class);
        verify(paintingRepository, times(1)).save(paintingEntityCaptor.capture());

        PaintingEntity savedEntity = paintingEntityCaptor.getValue();
        assertEquals("New Painting", savedEntity.getTitle());
        assertEquals("New Description", savedEntity.getDescription());
        assertEquals(museumId, savedEntity.getMuseumId());
        assertEquals(artistId, savedEntity.getArtistId());

        ArgumentCaptor<Painting> paintingCaptor = ArgumentCaptor.forClass(Painting.class);
        verify(responseObserver, times(1)).onNext(paintingCaptor.capture());
        verify(responseObserver, times(1)).onCompleted();

        Painting responsePainting = paintingCaptor.getValue();
        assertEquals(paintingId.toString(), responsePainting.getId());
        assertEquals("New Painting", responsePainting.getTitle());
        assertEquals("New Description", responsePainting.getDescription());
    }

    @Test
    void updatePaintingShouldUpdateAndReturnPaintingWhenFound() {
        Museum museum = Museum.newBuilder()
                .setId(museumId.toString())
                .setTitle("Test Museum")
                .build();


        Artist artist = Artist.newBuilder()
                .setId(artistId.toString())
                .setName("Test Artist")
                .build();

        Painting request = Painting.newBuilder()
                .setId(paintingId.toString())
                .setTitle("Updated Title")
                .setDescription("Updated Description")
                .setContent("updatedContent")
                .setMuseum(museum)
                .setArtist(artist)
                .build();

        when(paintingRepository.findById(paintingId)).thenReturn(Optional.of(paintingEntity));
        when(paintingRepository.save(any(PaintingEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(grpcMuseumClient.getMuseumById(museumId)).thenReturn(museum);
        when(grpcArtistClient.getArtistById(artistId)).thenReturn(artist);

        StreamObserver<Painting> responseObserver = mock(StreamObserver.class);

        grpcPaintingService.updatePainting(request, responseObserver);

        ArgumentCaptor<PaintingEntity> paintingEntityCaptor = ArgumentCaptor.forClass(PaintingEntity.class);
        verify(paintingRepository, times(1)).save(paintingEntityCaptor.capture());

        PaintingEntity updatedEntity = paintingEntityCaptor.getValue();
        assertEquals("Updated Title", updatedEntity.getTitle());
        assertEquals("Updated Description", updatedEntity.getDescription());
        assertEquals(museumId, updatedEntity.getMuseumId());
        assertEquals(artistId, updatedEntity.getArtistId());

        ArgumentCaptor<Painting> paintingCaptor = ArgumentCaptor.forClass(Painting.class);
        verify(responseObserver, times(1)).onNext(paintingCaptor.capture());
        verify(responseObserver, times(1)).onCompleted();

        Painting responsePainting = paintingCaptor.getValue();
        assertEquals(paintingId.toString(), responsePainting.getId());
        assertEquals("Updated Title", responsePainting.getTitle());
        assertEquals("Updated Description", responsePainting.getDescription());
    }

    @Test
    void updatePaintingShouldReturnNotFoundWhenPaintingDoesNotExist() {
        Painting request = Painting.newBuilder()
                .setId(UUID.randomUUID().toString())
                .build();
        when(paintingRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        StreamObserver<Painting> responseObserver = mock(StreamObserver.class);

        grpcPaintingService.updatePainting(request, responseObserver);

        ArgumentCaptor<Throwable> throwableCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(responseObserver, times(1)).onError(throwableCaptor.capture());

        Throwable throwable = throwableCaptor.getValue();
        assertTrue(throwable instanceof StatusRuntimeException);
        StatusRuntimeException statusRuntimeException = (StatusRuntimeException) throwable;
        assertEquals(Status.NOT_FOUND.getCode(), statusRuntimeException.getStatus().getCode());
    }
}