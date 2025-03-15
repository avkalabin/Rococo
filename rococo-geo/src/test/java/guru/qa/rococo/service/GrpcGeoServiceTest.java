package guru.qa.rococo.service;

import guru.qa.grpc.rococo.*;
import guru.qa.rococo.data.CountryEntity;
import guru.qa.rococo.data.repository.CountryRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrpcGeoServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private GrpcGeoService grpcGeoService;

    private final UUID countryId = UUID.randomUUID();
    private CountryEntity countryEntity;

    @BeforeEach
    void setUp() {
        countryEntity = new CountryEntity();
        countryEntity.setId(countryId);
        countryEntity.setName("Test Country");
    }

    @Test
    void getAllCountriesShouldReturnCountriesList() {
        AllCountriesRequest request = AllCountriesRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<CountryEntity> countryPage = new PageImpl<>(List.of(countryEntity));

        when(countryRepository.findAll(pageRequest)).thenReturn(countryPage);

        StreamObserver<AllCountriesResponse> responseObserver = mock(StreamObserver.class);

        grpcGeoService.getAllCountries(request, responseObserver);

        ArgumentCaptor<AllCountriesResponse> responseCaptor = ArgumentCaptor.forClass(AllCountriesResponse.class);
        verify(responseObserver, times(1)).onNext(responseCaptor.capture());
        verify(responseObserver, times(1)).onCompleted();

        AllCountriesResponse response = responseCaptor.getValue();
        assertEquals(1, response.getCountriesCount());
        assertEquals("Test Country", response.getCountries(0).getName());
        assertEquals(countryId.toString(), response.getCountries(0).getId());
        assertEquals(1, response.getTotalCount());
    }

    @Test
    void getCountryByIdShouldReturnCountryWhenFound() {
        CountryRequest request = CountryRequest.newBuilder()
                .setId(countryId.toString())
                .build();
        when(countryRepository.findById(countryId)).thenReturn(Optional.of(countryEntity));

        StreamObserver<Country> responseObserver = mock(StreamObserver.class);

        grpcGeoService.getCountryById(request, responseObserver);

        ArgumentCaptor<Country> countryCaptor = ArgumentCaptor.forClass(Country.class);
        verify(responseObserver, times(1)).onNext(countryCaptor.capture());
        verify(responseObserver, times(1)).onCompleted();

        Country responseCountry = countryCaptor.getValue();
        assertEquals(countryId.toString(), responseCountry.getId());
        assertEquals("Test Country", responseCountry.getName());
    }

    @Test
    void getCountryByIdShouldReturnNotFoundWhenCountryDoesNotExist() {
        CountryRequest request = CountryRequest.newBuilder()
                .setId(UUID.randomUUID().toString())
                .build();
        when(countryRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        StreamObserver<Country> responseObserver = mock(StreamObserver.class);

        grpcGeoService.getCountryById(request, responseObserver);

        ArgumentCaptor<Throwable> throwableCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(responseObserver, times(1)).onError(throwableCaptor.capture());

        Throwable throwable = throwableCaptor.getValue();
        assertTrue(throwable instanceof StatusRuntimeException);
        StatusRuntimeException statusRuntimeException = (StatusRuntimeException) throwable;
        assertEquals(Status.NOT_FOUND.getCode(), statusRuntimeException.getStatus().getCode());
    }
}