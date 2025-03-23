package guru.qa.rococo.service;

import guru.qa.grpc.rococo.RococoUserdataServiceGrpc;
import guru.qa.grpc.rococo.User;
import guru.qa.grpc.rococo.UserRequest;
import guru.qa.rococo.data.UserEntity;
import guru.qa.rococo.data.repository.UserRepository;
import guru.qa.rococo.model.EventType;
import guru.qa.rococo.model.LogJson;
import guru.qa.rococo.model.UserJson;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static java.nio.charset.StandardCharsets.UTF_8;

@GrpcService
public class GrpcUserdataService extends RococoUserdataServiceGrpc.RococoUserdataServiceImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcUserdataService.class);

    private final UserRepository userRepository;
    private final KafkaTemplate<String, LogJson> kafkaTemplate;

    @Autowired
    public GrpcUserdataService(UserRepository userRepository,
                               KafkaTemplate<String, LogJson> kafkaTemplate) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    @KafkaListener(topics = "users", groupId = "userdata")
    public void listener(@Nonnull @Payload UserJson user) {
        LOG.info("### Kafka topic [users] received message: " + user.username());
        UserEntity userDataEntity = new UserEntity();
        userDataEntity.setUsername(user.username());
        UserEntity userEntity = userRepository.save(userDataEntity);
        LOG.info(String.format(
                "### User '%s' successfully saved to database with id: %s",
                user.username(),
                userEntity.getId()
        ));
        LogJson logJson = new LogJson(
                EventType.USER_CREATED,
                userEntity.getId(),
                "User " + userEntity.getUsername() + " successfully created",
                Instant.now());
        kafkaTemplate.send("userdata", logJson);
        LOG.info("### Kafka topic [userdata] sent message: {}", logJson);
    }

    @Override
    public void getUser(@Nonnull UserRequest request,
                        StreamObserver<User> responseObserver) {
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
    public void updateUser(@Nonnull User request,
                           StreamObserver<User> responseObserver) {
        UserEntity userEntity = userRepository.findByUsername(request.getUsername());

        if (userEntity != null) {
            userEntity.setFirstname(request.getFirstname());
            userEntity.setLastname(request.getLastname());
            userEntity.setAvatar(request.getAvatar().getBytes());
            userRepository.save(userEntity);
            responseObserver.onNext(toGrpc(userEntity));
            responseObserver.onCompleted();
            LogJson logJson = new LogJson(
                    EventType.USER_UPDATED,
                    userEntity.getId(),
                    "User " + userEntity.getUsername() + " successfully updated",
                    Instant.now());
            kafkaTemplate.send("userdata", logJson);
            LOG.info("### Kafka topic [userdata] sent message: {}", logJson);
        } else {
            throw new StatusRuntimeException(
                    Status.NOT_FOUND.withDescription("User not found by username: " + request.getUsername()));
        }
    }

    @Nonnull
    private User toGrpc(@Nonnull UserEntity userEntity) {
        return User.newBuilder()
                .setId(userEntity.getId().toString())
                .setUsername(userEntity.getUsername())
                .setFirstname(userEntity.getFirstname() != null ? userEntity.getFirstname() : "")
                .setLastname(userEntity.getLastname() != null ? userEntity.getLastname() : "")
                .setAvatar(userEntity.getAvatar() != null ? new String(userEntity.getAvatar(), UTF_8) : "")
                .build();
    }
}
