package guru.qa.rococo.service;

import guru.qa.grpc.rococo.RococoUserdataServiceGrpc;
import guru.qa.grpc.rococo.User;
import guru.qa.grpc.rococo.UserRequest;
import guru.qa.rococo.data.UserEntity;
import guru.qa.rococo.data.repository.UserRepository;
import guru.qa.rococo.model.UserJson;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.Arrays;

@GrpcService
public class GrpcUserdataService extends RococoUserdataServiceGrpc.RococoUserdataServiceImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcUserdataService.class);

    private final UserRepository userRepository;

    @Autowired
    public GrpcUserdataService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "users", groupId = "userdata")
    public void listener(@Payload UserJson user, ConsumerRecord<String, UserJson> cr) {
        LOG.info("### Kafka topic [users] received message: " + user.username());
        LOG.info("### Kafka consumer record: " + cr.toString());
        UserEntity userDataEntity = new UserEntity();
        userDataEntity.setUsername(user.username());
        UserEntity userEntity = userRepository.save(userDataEntity);
        LOG.info(String.format(
                "### User '%s' successfully saved to database with id: %s",
                user.username(),
                userEntity.getId()
        ));
    }

    @Override
    public void getUser(UserRequest request, StreamObserver<User> responseObserver) {
        UserEntity userEntity = userRepository.findByUsername(request.getUsername());
        if (userEntity != null) {
            responseObserver.onNext(toGrpc(userEntity));
            responseObserver.onCompleted();
        } else {
            throw new StatusRuntimeException(
                    Status.NOT_FOUND.withDescription("User not found by username: " + request.getUsername()));
        }
    }

    @Override
    public void updateUser(User request, StreamObserver<User> responseObserver) {
        UserEntity userEntity = userRepository.findByUsername(request.getUsername());

        if (userEntity != null) {
            userEntity.setFirstname(request.getFirstname());
            userEntity.setLastname(request.getLastname());
            userEntity.setAvatar(request.getAvatar().getBytes());
            userRepository.save(userEntity);
            responseObserver.onNext(toGrpc(userEntity));
            responseObserver.onCompleted();
        } else {
            throw new StatusRuntimeException(
                    Status.NOT_FOUND.withDescription("User not found by username: " + request.getUsername()));
        }
    }

    private User toGrpc(UserEntity userEntity) {
        return User.newBuilder()
                .setId(userEntity.getId().toString())
                .setUsername(userEntity.getUsername())
                .setFirstname(userEntity.getFirstname())
                .setLastname(userEntity.getLastname())
                .setAvatar(Arrays.toString(userEntity.getAvatar()))
                .build();
    }
}
