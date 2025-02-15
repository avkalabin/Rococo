package guru.qa.rococo.service.api;

import guru.qa.grpc.rococo.*;
import guru.qa.rococo.model.MuseumJson;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Component
public class GrpcMuseumClient {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcMuseumClient.class);

    @GrpcClient("grpcMuseumClient")
    private RococoMuseumServiceGrpc.RococoMuseumServiceBlockingStub rococoMuseumServiceStub;

    public Page<MuseumJson> getAllMuseum(String title, Pageable pageable) {
        AllMuseumsRequest request = AllMuseumsRequest.newBuilder()
                .setPage(pageable.getPageNumber())
                .setSize(pageable.getPageSize())
                .setTitle(title == null ? "" : title)
                .build();

        try {
            AllMuseumsResponse response = rococoMuseumServiceStub.getAllMuseums(request);
            List<MuseumJson> museumJsonsList = response.getMuseumsList()
                    .stream()
                    .map(MuseumJson::fromGrpc)
                    .toList();
            LOG.info("Successfully fetched {} museums", museumJsonsList.size());
            return new PageImpl<>(museumJsonsList, pageable, response.getTotalCount());
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    public MuseumJson getMuseumById(UUID id) {
        MuseumRequest request = MuseumRequest.newBuilder()
                .setId(id.toString())
                .build();
        try {
            Museum response = rococoMuseumServiceStub.getMuseumById(request);
            return MuseumJson.fromGrpc(response);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Museum with id " + id + " not found", e);
            } else {
                LOG.error("### Error while calling gRPC server ", e);
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
            }
        }
    }

    public MuseumJson createMuseum(MuseumJson museum) {
        Museum request = toGrpc(museum);
        try {
            Museum response = rococoMuseumServiceStub.createMuseum(request);
            return MuseumJson.fromGrpc(response);
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    public MuseumJson updateMuseum(MuseumJson museum) {
        Museum request = toGrpc(museum);
        try {
            Museum response = rococoMuseumServiceStub.updateMuseum(request);
            return MuseumJson.fromGrpc(response);
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    private Museum toGrpc(MuseumJson museum) {
        Country country = Country.newBuilder()
                .setId(museum.geo().country().id().toString())
                .setName(museum.geo().country().name())
                .build();
        Geo geo = Geo.newBuilder()
                .setCity(museum.geo().city())
                .setCountry(country)
                .build();

        return Museum.newBuilder()
                .setId(museum.id() != null ? museum.id().toString() : "")
                .setTitle(museum.title())
                .setDescription(museum.description())
                .setPhoto(museum.photo())
                .setGeo(geo)
                .build();
    }
}
