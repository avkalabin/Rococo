package guru.qa.rococo.service.api;

import guru.qa.grpc.rococo.Painting;
import guru.qa.grpc.rococo.RococoUserdataServiceGrpc;
import guru.qa.grpc.rococo.User;
import guru.qa.grpc.rococo.UserRequest;
import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.model.UserJson;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class GrpcUserdataClient {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcUserdataClient.class);

    @GrpcClient("grpcUserdataClient")
    private RococoUserdataServiceGrpc.RococoUserdataServiceBlockingStub grpcUserdataServiceStub;

    public UserJson getUser(String username) {
        UserRequest request = UserRequest.newBuilder()
                .setUsername(username)
                .build();
        try {
            User user = grpcUserdataServiceStub.getUser(request);
            return UserJson.fromGrpc(user);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with username " + username + " not found", e);
            } else {
                LOG.error("### Error while calling gRPC server ", e);
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
            }
        }
    }

    public UserJson updateUser(UserJson user) {
        User request = toGrpc(user);
        try {
            User response = grpcUserdataServiceStub.updateUser(request);
            return UserJson.fromGrpc(response);
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    private User toGrpc(UserJson user) {
        return User.newBuilder()
                .setId(user.id().toString())
                .setUsername(user.username())
                .setFirstname(user.firstname())
                .setLastname(user.lastname())
                .setAvatar(user.avatar())
                .build();
    }
}