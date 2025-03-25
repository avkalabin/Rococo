package guru.qa.rococo.config;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public enum DockerConfig implements Config {
    INSTANCE;

    @Nonnull
    @Override
    public String databaseAddress() {
        return "rococo-all-db:3306";
    }

    @Nonnull
    @Override
    public String frontUrl() {
        return "http://frontend.rococo.dc/";
    }

    @NotNull
    @Override
    public String gatewayUrl() {
        return "http://gateway.rococo.dc:8080/";
    }

    @Nonnull
    @Override
    public String authUrl() {
        return "http://auth.rococo.dc:9000/";
    }

    @Nonnull
    @Override
    public String artistJdbcUrl() {
        return "jdbc:mysql://rococo-all-db:3306/rococo-artist";
    }

    @Nonnull
    @Override
    public String authJdbcUrl() {
        return "jdbc:mysql://rococo-all-db:3306/rococo-auth";
    }

    @Nonnull
    @Override
    public String geoJdbcUrl() {
        return "jdbc:mysql://rococo-all-db:3306/rococo-geo";
    }

    @Nonnull
    @Override
    public String museumJdbcUrl() {
        return "jdbc:mysql://rococo-all-db:3306/rococo-museum";
    }

    @Nonnull
    @Override
    public String paintingJdbcUrl() {
        return "jdbc:mysql://rococo-all-db:3306/rococo-painting";
    }

    @Nonnull
    @Override
    public String userdataJdbcUrl() {
        return "jdbc:mysql://rococo-all-db:3306/rococo-userdata";
    }

    @Nonnull
    @Override
    public String userdataGrpcAddress() {
        return "userdata.rococo.dc";
    }

    @Nonnull
    @Override
    public String artistGrpcAddress() {
        return "artist.rococo.dc";
    }

    @Nonnull
    @Override
    public String museumGrpcAddress() {
        return "museum.rococo.dc";
    }

    @Nonnull
    @Override
    public String geoGrpcAddress() {
        return "geo.rococo.dc";
    }

    @Nonnull
    @Override
    public String paintingGrpcAddress() {
        return "painting.rococo.dc";
    }

    @Nonnull
    @Override
    public String allureDockerServiceUrl() {
        final String url = System.getenv("ALLURE_DOCKER_API");
        return url == null
                ? "http://allure:5050/"
                : url;
    }
}
