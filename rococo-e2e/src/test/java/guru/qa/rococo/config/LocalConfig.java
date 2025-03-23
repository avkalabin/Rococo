package guru.qa.rococo.config;

import javax.annotation.Nonnull;

public enum LocalConfig implements Config {
    INSTANCE;

    @Nonnull
    @Override
    public String databaseAddress() {
        return "localhost:3306";
    }

    @Nonnull
    @Override
    public String frontUrl() {
        return "http://127.0.0.1:3000/";
    }

    @Nonnull
    @Override
    public String authUrl() {
        return "http://127.0.0.1:9000/";
    }

    @Nonnull
    @Override
    public String artistJdbcUrl() {
        return "jdbc:mysql://127.0.0.1:3306/rococo-artist";
    }

    @Nonnull
    @Override
    public String authJdbcUrl() {
        return "jdbc:mysql://127.0.0.1:3306/rococo-auth";
    }

    @Nonnull
    @Override
    public String geoJdbcUrl() {
        return "jdbc:mysql://127.0.0.1:3306/rococo-geo";
    }

    @Nonnull
    @Override
    public String museumJdbcUrl() {
        return "jdbc:mysql://127.0.0.1:3306/rococo-museum";
    }

    @Nonnull
    @Override
    public String paintingJdbcUrl() {
        return "jdbc:mysql://127.0.0.1:3306/rococo-painting";
    }

    @Nonnull
    @Override
    public String userdataJdbcUrl() {
        return "jdbc:mysql://127.0.0.1:3306/rococo-userdata";
    }

    @Nonnull
    @Override
    public String userdataGrpcAddress() {
        return "localhost";
    }

    @Nonnull
    @Override
    public String artistGrpcAddress() {
        return "localhost";
    }

    @Nonnull
    @Override
    public String museumGrpcAddress() {
        return "localhost";
    }

    @Nonnull
    @Override
    public String geoGrpcAddress() {
        return "localhost";
    }

    @Nonnull
    @Override
    public String paintingGrpcAddress() {
        return "localhost";
    }

    @Nonnull
    @Override
    public String allureDockerServiceUrl() {
        return "http://127.0.0.1";
    }
}
