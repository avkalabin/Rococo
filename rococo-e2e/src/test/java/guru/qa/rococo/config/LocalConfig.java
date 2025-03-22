package guru.qa.rococo.config;

import org.jetbrains.annotations.NotNull;

public enum LocalConfig implements Config {
    INSTANCE;

    @NotNull
    @Override
    public String databaseAddress() {
        return "localhost:3306";
    }

    @NotNull
    @Override
    public String frontUrl() {
        return "http://127.0.0.1:3000/";
    }

    @NotNull
    @Override
    public String authUrl() {
        return "http://127.0.0.1:9000/";
    }

    @NotNull
    @Override
    public String artistJdbcUrl() {
        return "jdbc:mysql://127.0.0.1:3306/rococo-artist";
    }

    @NotNull
    @Override
    public String authJdbcUrl() {
        return "jdbc:mysql://127.0.0.1:3306/rococo-auth";
    }

    @NotNull
    @Override
    public String geoJdbcUrl() {
        return "jdbc:mysql://127.0.0.1:3306/rococo-geo";
    }

    @NotNull
    @Override
    public String museumJdbcUrl() {
        return "jdbc:mysql://127.0.0.1:3306/rococo-museum";
    }

    @NotNull
    @Override
    public String paintingJdbcUrl() {
        return "jdbc:mysql://127.0.0.1:3306/rococo-painting";
    }

    @NotNull
    @Override
    public String userdataJdbcUrl() {
        return "jdbc:mysql://127.0.0.1:3306/rococo-userdata";
    }

    @NotNull
    @Override
    public String userdataGrpcAddress() {
        return "localhost";
    }

    @NotNull
    @Override
    public String artistGrpcAddress() {
        return "localhost";
    }

    @NotNull
    @Override
    public String museumGrpcAddress() {
        return "localhost";
    }

    @NotNull
    @Override
    public String geoGrpcAddress() {
        return "localhost";
    }

    @NotNull
    @Override
    public String paintingGrpcAddress() {
        return "localhost";
    }

    @Override
    public String allureDockerServiceUrl() {
        return "http://127.0.0.1";
    }
}
