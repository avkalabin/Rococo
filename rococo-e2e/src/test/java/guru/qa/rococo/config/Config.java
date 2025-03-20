package guru.qa.rococo.config;

import javax.annotation.Nonnull;

public interface Config {

    static @Nonnull Config getInstance() {
        return "docker".equals(System.getProperty("test.env"))
                ? DockerConfig.INSTANCE
                : LocalConfig.INSTANCE;
    }

    @Nonnull
    String databaseAddress();

    @Nonnull
    default String databaseUser() {
        return "root";
    }
    @Nonnull
    default String databasePassword() {
        return "secret";
    }

    @Nonnull
    String frontUrl();

    @Nonnull
    String authUrl();

    @Nonnull
    String artistJdbcUrl();

    @Nonnull
    String authJdbcUrl();

    @Nonnull
    String geoJdbcUrl();

    @Nonnull
    String museumJdbcUrl();

    @Nonnull
    String paintingJdbcUrl();

    @Nonnull
    String userdataJdbcUrl();

    @Nonnull
    String userdataGrpcAddress();

    default int userdataGrpcPort() {
        return 8091;
    }

    @Nonnull
    String artistGrpcAddress();

    default int artistGrpcPort() {
        return 8092;
    }

    @Nonnull
    String museumGrpcAddress();

    default int museumGrpcPort() {
        return 8093;
    }

    @Nonnull
    String geoGrpcAddress();

    default int geoGrpcPort() {
        return 8094;
    }

    @Nonnull
    String paintingGrpcAddress();

    default int paintingGrpcPort() {
        return 8095;
    }

    String allureDockerServiceUrl();

    @Nonnull
    default String projectId() {
        return "rococo-kalabin";
    }
}
