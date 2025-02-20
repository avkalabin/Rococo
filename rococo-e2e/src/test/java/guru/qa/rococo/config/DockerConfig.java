package guru.qa.rococo.config;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public enum DockerConfig implements Config {
    INSTANCE;

    @NotNull
    @Override
    public String databaseAddress() {
        return "rococo-all-db:3306";
    }

    @NotNull
    @Override
    public String frontUrl() {
        return "";
    }

    @NotNull
    @Override
    public String authUrl() {
        return "";
    }

    @NotNull
    @Override
    public String authJdbcUrl() {
        return "";
    }

    @NotNull
    @Override
    public String userdataJdbcUrl() {
        return "";
    }

    @NotNull
    @Override
    public String userdataGrpcAddress() {
        return "";
    }

    @NotNull
    @Override
    public String artistGrpcAddress() {
        return "";
    }

    @NotNull
    @Override
    public String museumGrpcAddress() {
        return "";
    }

    @NotNull
    @Override
    public String geoGrpcAddress() {
        return "";
    }

    @NotNull
    @Override
    public String paintingGrpcAddress() {
        return "";
    }
}
