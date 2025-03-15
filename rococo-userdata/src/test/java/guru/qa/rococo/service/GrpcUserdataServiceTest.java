package guru.qa.rococo.service;

import guru.qa.grpc.rococo.User;
import guru.qa.grpc.rococo.UserRequest;
import guru.qa.rococo.data.UserEntity;
import guru.qa.rococo.data.repository.UserRepository;
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

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrpcUserdataServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GrpcUserdataService grpcUserdataService;

    private final UUID userId = UUID.randomUUID();
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername("testUser");
        userEntity.setFirstname("Test");
        userEntity.setLastname("User");
        userEntity.setAvatar("avatarData".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void getUserShouldReturnUserWhenFound() {
        UserRequest request = UserRequest.newBuilder()
                .setUsername("testUser")
                .build();
        when(userRepository.findByUsername("testUser")).thenReturn(userEntity);

        StreamObserver<User> responseObserver = mock(StreamObserver.class);

        grpcUserdataService.getUser(request, responseObserver);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(responseObserver, times(1)).onNext(userCaptor.capture());
        verify(responseObserver, times(1)).onCompleted();

        User responseUser = userCaptor.getValue();
        assertEquals(userId.toString(), responseUser.getId());
        assertEquals("testUser", responseUser.getUsername());
        assertEquals("Test", responseUser.getFirstname());
        assertEquals("User", responseUser.getLastname());
        assertEquals("avatarData", responseUser.getAvatar());
    }

    @Test
    void getUserShouldReturnNotFoundWhenUserDoesNotExist() {
        UserRequest request = UserRequest.newBuilder()
                .setUsername("unknownUser")
                .build();
        when(userRepository.findByUsername("unknownUser")).thenReturn(null);

        StreamObserver<User> responseObserver = mock(StreamObserver.class);

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            grpcUserdataService.getUser(request, responseObserver);
        });

        assertEquals(Status.NOT_FOUND.getCode(), exception.getStatus().getCode());
        assertEquals("User not found by username: unknownUser", exception.getStatus().getDescription());
    }

    @Test
    void updateUserShouldUpdateAndReturnUserWhenFound() {
        User request = User.newBuilder()
                .setUsername("testUser")
                .setFirstname("UpdatedFirstname")
                .setLastname("UpdatedLastname")
                .setAvatar("updatedAvatar")
                .build();
        when(userRepository.findByUsername("testUser")).thenReturn(userEntity);
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StreamObserver<User> responseObserver = mock(StreamObserver.class);

        grpcUserdataService.updateUser(request, responseObserver);

        ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository, times(1)).save(userEntityCaptor.capture());

        UserEntity updatedEntity = userEntityCaptor.getValue();
        assertEquals("UpdatedFirstname", updatedEntity.getFirstname());
        assertEquals("UpdatedLastname", updatedEntity.getLastname());
        assertEquals("updatedAvatar", new String(updatedEntity.getAvatar(), StandardCharsets.UTF_8));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(responseObserver, times(1)).onNext(userCaptor.capture());
        verify(responseObserver, times(1)).onCompleted();

        User responseUser = userCaptor.getValue();
        assertEquals(userId.toString(), responseUser.getId());
        assertEquals("testUser", responseUser.getUsername());
        assertEquals("UpdatedFirstname", responseUser.getFirstname());
        assertEquals("UpdatedLastname", responseUser.getLastname());
        assertEquals("updatedAvatar", responseUser.getAvatar());
    }

    @Test
    void updateUserShouldReturnNotFoundWhenUserDoesNotExist() {
        User request = User.newBuilder()
                .setUsername("unknownUser")
                .build();
        when(userRepository.findByUsername("unknownUser")).thenReturn(null);

        StreamObserver<User> responseObserver = mock(StreamObserver.class);

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            grpcUserdataService.updateUser(request, responseObserver);
        });

        assertEquals(Status.NOT_FOUND.getCode(), exception.getStatus().getCode());
        assertEquals("User not found by username: unknownUser", exception.getStatus().getDescription());
    }
}