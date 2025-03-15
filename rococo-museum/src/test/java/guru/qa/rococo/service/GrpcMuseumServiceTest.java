package guru.qa.rococo.service;

import guru.qa.grpc.rococo.*;
import guru.qa.rococo.data.MuseumEntity;
import guru.qa.rococo.data.repository.MuseumRepository;
import guru.qa.rococo.service.api.GrpcGeoClient;
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
class GrpcMuseumServiceTest {

    @Mock
    private MuseumRepository museumRepository;

    @Mock
    private GrpcGeoClient grpcGeoClient;

    @InjectMocks
    private GrpcMuseumService grpcMuseumService;

    private final UUID museumId = UUID.randomUUID();
    private final UUID countryId = UUID.randomUUID();
    private MuseumEntity museumEntity;

    @BeforeEach
    void setUp() {
        museumEntity = new MuseumEntity();
        museumEntity.setId(museumId);
        museumEntity.setTitle("Test Museum");
        museumEntity.setDescription("Test Description");
        museumEntity.setPhoto("photoData".getBytes(StandardCharsets.UTF_8));
        museumEntity.setCity("Test City");
        museumEntity.setGeoId(countryId);
    }

    @Test
    void getAllMuseumsShouldReturnMuseumsList() {
        AllMuseumsRequest request = AllMuseumsRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .setTitle("Test")
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<MuseumEntity> museumPage = new PageImpl<>(List.of(museumEntity));

        when(museumRepository.findAllByTitleContainsIgnoreCase("Test", pageRequest)).thenReturn(museumPage);

        Country country = Country.newBuilder()
                .setId(countryId.toString())
                .setName("Test Country")
                .build();
        when(grpcGeoClient.getCountryById(countryId)).thenReturn(country);

        StreamObserver<AllMuseumsResponse> responseObserver = mock(StreamObserver.class);

        grpcMuseumService.getAllMuseums(request, responseObserver);

        ArgumentCaptor<AllMuseumsResponse> responseCaptor = ArgumentCaptor.forClass(AllMuseumsResponse.class);
        verify(responseObserver, times(1)).onNext(responseCaptor.capture());
        verify(responseObserver, times(1)).onCompleted();

        AllMuseumsResponse response = responseCaptor.getValue();
        assertEquals(1, response.getMuseumsCount());
        assertEquals("Test Museum", response.getMuseums(0).getTitle());
        assertEquals("Test Description", response.getMuseums(0).getDescription());
        assertEquals("Test City", response.getMuseums(0).getGeo().getCity());
        assertEquals("Test Country", response.getMuseums(0).getGeo().getCountry().getName());
        assertEquals(1, response.getTotalCount());
    }

    @Test
    void getMuseumByIdShouldReturnMuseumWhenFound() {
        MuseumRequest request = MuseumRequest.newBuilder()
                .setId(museumId.toString())
                .build();
        when(museumRepository.findById(museumId)).thenReturn(Optional.of(museumEntity));

        Country country = Country.newBuilder()
                .setId(countryId.toString())
                .setName("Test Country")
                .build();
        when(grpcGeoClient.getCountryById(countryId)).thenReturn(country);

        StreamObserver<Museum> responseObserver = mock(StreamObserver.class);

        grpcMuseumService.getMuseumById(request, responseObserver);

        ArgumentCaptor<Museum> museumCaptor = ArgumentCaptor.forClass(Museum.class);
        verify(responseObserver, times(1)).onNext(museumCaptor.capture());
        verify(responseObserver, times(1)).onCompleted();

        Museum responseMuseum = museumCaptor.getValue();
        assertEquals(museumId.toString(), responseMuseum.getId());
        assertEquals("Test Museum", responseMuseum.getTitle());
        assertEquals("Test Description", responseMuseum.getDescription());
        assertEquals("Test City", responseMuseum.getGeo().getCity());
        assertEquals("Test Country", responseMuseum.getGeo().getCountry().getName());
    }

    @Test
    void getMuseumByIdShouldReturnNotFoundWhenMuseumDoesNotExist() {
        MuseumRequest request = MuseumRequest.newBuilder()
                .setId(UUID.randomUUID().toString())
                .build();
        when(museumRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        StreamObserver<Museum> responseObserver = mock(StreamObserver.class);

        grpcMuseumService.getMuseumById(request, responseObserver);

        ArgumentCaptor<Throwable> throwableCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(responseObserver, times(1)).onError(throwableCaptor.capture());

        Throwable throwable = throwableCaptor.getValue();
        assertTrue(throwable instanceof StatusRuntimeException);
        StatusRuntimeException statusRuntimeException = (StatusRuntimeException) throwable;
        assertEquals(Status.NOT_FOUND.getCode(), statusRuntimeException.getStatus().getCode());
    }

    @Test
    void createMuseumShouldSaveAndReturnMuseum() {
        Country country = Country.newBuilder()
                .setId(countryId.toString())
                .setName("Test Country")
                .build();

        Museum request = Museum.newBuilder()
                .setTitle("New Museum")
                .setDescription("New Description")
                .setPhoto("newPhoto")
                .setGeo(Geo.newBuilder()
                        .setCity("New City")
                        .setCountry(country)
                        .build())
                .build();

        when(museumRepository.save(any(MuseumEntity.class))).thenAnswer(invocation -> {
            MuseumEntity entity = invocation.getArgument(0);
            entity.setId(museumId);
            return entity;
        });
        when(grpcGeoClient.getCountryById(countryId)).thenReturn(country);

        StreamObserver<Museum> responseObserver = mock(StreamObserver.class);

        grpcMuseumService.createMuseum(request, responseObserver);

        ArgumentCaptor<MuseumEntity> museumEntityCaptor = ArgumentCaptor.forClass(MuseumEntity.class);
        verify(museumRepository, times(1)).save(museumEntityCaptor.capture());

        MuseumEntity savedEntity = museumEntityCaptor.getValue();
        assertEquals("New Museum", savedEntity.getTitle());
        assertEquals("New Description", savedEntity.getDescription());
        assertEquals("New City", savedEntity.getCity());
        assertEquals(countryId, savedEntity.getGeoId());

        ArgumentCaptor<Museum> museumCaptor = ArgumentCaptor.forClass(Museum.class);
        verify(responseObserver, times(1)).onNext(museumCaptor.capture());
        verify(responseObserver, times(1)).onCompleted();

        Museum responseMuseum = museumCaptor.getValue();
        assertEquals(museumId.toString(), responseMuseum.getId());
        assertEquals("New Museum", responseMuseum.getTitle());
        assertEquals("New Description", responseMuseum.getDescription());
        assertEquals("New City", responseMuseum.getGeo().getCity());
        assertEquals("Test Country", responseMuseum.getGeo().getCountry().getName());
    }

    @Test
    void updateMuseumShouldUpdateAndReturnMuseumWhenFound() {
        Country country = Country.newBuilder()
                .setId(countryId.toString())
                .setName("Updated Country")
                .build();

        Museum request = Museum.newBuilder()
                .setId(museumId.toString())
                .setTitle("Updated Title")
                .setDescription("Updated Description")
                .setPhoto("updatedPhoto")
                .setGeo(Geo.newBuilder()
                        .setCity("Updated City")
                        .setCountry(country)
                        .build())
                .build();

        when(museumRepository.findById(museumId)).thenReturn(Optional.of(museumEntity));
        when(museumRepository.save(any(MuseumEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(grpcGeoClient.getCountryById(countryId)).thenReturn(country);

        StreamObserver<Museum> responseObserver = mock(StreamObserver.class);

        grpcMuseumService.updateMuseum(request, responseObserver);

        ArgumentCaptor<MuseumEntity> museumEntityCaptor = ArgumentCaptor.forClass(MuseumEntity.class);
        verify(museumRepository, times(1)).save(museumEntityCaptor.capture());

        MuseumEntity updatedEntity = museumEntityCaptor.getValue();
        assertEquals("Updated Title", updatedEntity.getTitle());
        assertEquals("Updated Description", updatedEntity.getDescription());
        assertEquals("Updated City", updatedEntity.getCity());
        assertEquals(countryId, updatedEntity.getGeoId());

        ArgumentCaptor<Museum> museumCaptor = ArgumentCaptor.forClass(Museum.class);
        verify(responseObserver, times(1)).onNext(museumCaptor.capture());
        verify(responseObserver, times(1)).onCompleted();

        Museum responseMuseum = museumCaptor.getValue();
        assertEquals(museumId.toString(), responseMuseum.getId());
        assertEquals("Updated Title", responseMuseum.getTitle());
        assertEquals("Updated Description", responseMuseum.getDescription());
        assertEquals("Updated City", responseMuseum.getGeo().getCity());
        assertEquals("Updated Country", responseMuseum.getGeo().getCountry().getName());
    }

    @Test
    void updateMuseumShouldReturnNotFoundWhenMuseumDoesNotExist() {
        Museum request = Museum.newBuilder()
                .setId(UUID.randomUUID().toString())
                .build();
        when(museumRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        StreamObserver<Museum> responseObserver = mock(StreamObserver.class);

        grpcMuseumService.updateMuseum(request, responseObserver);

        ArgumentCaptor<Throwable> throwableCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(responseObserver, times(1)).onError(throwableCaptor.capture());

        Throwable throwable = throwableCaptor.getValue();
        assertTrue(throwable instanceof StatusRuntimeException);
        StatusRuntimeException statusRuntimeException = (StatusRuntimeException) throwable;
        assertEquals(Status.NOT_FOUND.getCode(), statusRuntimeException.getStatus().getCode());
    }
}