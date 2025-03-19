package guru.qa.rococo.config;

import org.jetbrains.annotations.NotNull;

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
        return "http://frontend.rococo.dc/";
    }

    @NotNull
    @Override
    public String authUrl() {
        return "http://auth.rococo.dc:9000/";
    }

    @NotNull
    @Override
    public String artistJdbcUrl() {
        return "jdbc:mysql://rococo-all-db:3306/rococo-artist";
    }

    @NotNull
    @Override
    public String authJdbcUrl() {
        return "jdbc:mysql://rococo-all-db:3306/rococo-auth";
    }

    @NotNull
    @Override
    public String geoJdbcUrl() {
        return "jdbc:mysql://rococo-all-db:3306/rococo-geo";
    }

    @NotNull
    @Override
    public String museumJdbcUrl() {
        return "jdbc:mysql://rococo-all-db:3306/rococo-museum";
    }

    @NotNull
    @Override
    public String paintingJdbcUrl() {
        return "jdbc:mysql://rococo-all-db:3306/rococo-painting";
    }

    @NotNull
    @Override
    public String userdataJdbcUrl() {
        return "jdbc:mysql://rococo-all-db:3306/rococo-userdata";
    }

    @NotNull
    @Override
    public String userdataGrpcAddress() {
        return "userdata.rococo.dc";
    }

    @NotNull
    @Override
    public String artistGrpcAddress() {
        return "artist.rococo.dc";
    }

    @NotNull
    @Override
    public String museumGrpcAddress() {
        return "museum.rococo.dc";
    }

    @NotNull
    @Override
    public String geoGrpcAddress() {
        return "geo.rococo.dc";
    }

    @NotNull
    @Override
    public String paintingGrpcAddress() {
        return "painting.rococo.dc";
    }
}
