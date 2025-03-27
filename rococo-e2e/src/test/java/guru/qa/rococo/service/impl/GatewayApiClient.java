package guru.qa.rococo.service.impl;

import guru.qa.rococo.api.GatewayApi;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.model.*;
import guru.qa.rococo.model.rest.pageable.RestResponsePage;
import io.qameta.allure.Step;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.UUID;

public class GatewayApiClient extends RestClient {

    private final GatewayApi gatewayApi;

    public GatewayApiClient() {
        super(CFG.gatewayUrl());
        this.gatewayApi = create(GatewayApi.class);
    }

    @Step("Send GET /api/artist request to rococo-gateway")
    public Response<RestResponsePage<ArtistJson>> getAllArtists(@Nullable String name, int page, int size) {
        return executeRequest(gatewayApi.getAllArtists(name,page, size));
    }

    @Step("Send GET /api/artist/{id} request to rococo-gateway")
    public Response<ArtistJson> getArtistById(@Nonnull UUID id) {
        return executeRequest(gatewayApi.getArtistById(id));
    }

    @Step("Send POST /api/artist request to rococo-gateway")
    public Response<ArtistJson> createArtist(@Nonnull String bearerToken, @Nonnull ArtistJson artist) {
        return executeRequest(gatewayApi.createArtist(bearerToken, artist));
    }

    @Step("Send PATCH /api/artist request to rococo-gateway")
    public Response<ArtistJson> updateArtist(@Nonnull String bearerToken, @Nonnull ArtistJson artist) {
        return executeRequest(gatewayApi.updateArtist(bearerToken, artist));
    }

    @Step("Send GET /api/country request to rococo-gateway")
    public Response<RestResponsePage<CountryJson>> getAllCountries(int page, int size) {
        return executeRequest(gatewayApi.getAllCountries(page, size));
    }

    @Step("Send GET /api/museum request to rococo-gateway")
    public Response<RestResponsePage<MuseumJson>> getAllMuseums(@Nullable String title, int page, int size) {
        return executeRequest(gatewayApi.getAllMuseums(title, page, size));
    }

    @Step("Send GET /api/museum/{id} request to rococo-gateway")
    public Response<MuseumJson> getMuseumById(@Nonnull UUID id) {
        return executeRequest(gatewayApi.getMuseumById(id));
    }

    @Step("Send POST /api/museum request to rococo-gateway")
    public Response<MuseumJson> createMuseum(@Nonnull String bearerToken, @Nonnull MuseumJson museum) {
        return executeRequest(gatewayApi.createMuseum(bearerToken, museum));
    }

    @Step("Send PATCH /api/museum request to rococo-gateway")
    public Response<MuseumJson> updateMuseum(@Nonnull String bearerToken, @Nonnull MuseumJson museum) {
        return executeRequest(gatewayApi.updateMuseum(bearerToken, museum));
    }

    @Step("Send GET /api/painting request to rococo-gateway")
    public Response<RestResponsePage<PaintingJson>> getAllPaintings(@Nullable String title, int page, int size) {
        return executeRequest(gatewayApi.getAllPaintings(title, page, size));
    }

    @Step("Send GET /api/painting/author/{id} request to rococo-gateway")
    public Response<RestResponsePage<PaintingJson>> getPaintingByArtist(@Nonnull UUID id, int page, int size) {
        return executeRequest(gatewayApi.getPaintingByArtist(id, page, size));
    }

    @Step("Send GET /api/painting/{id} request to rococo-gateway")
    public Response<PaintingJson> getPaintingById(@Nonnull UUID id) {
        return executeRequest(gatewayApi.getPaintingById(id));
    }

    @Step("Send POST /api/painting request to rococo-gateway")
    public Response<PaintingJson> createPainting(@Nonnull String bearerToken, @Nonnull PaintingJson painting) {
        return executeRequest(gatewayApi.createPainting(bearerToken, painting));
    }

    @Step("Send PATCH /api/painting request to rococo-gateway")
    public Response<PaintingJson> updatePainting(@Nonnull String bearerToken, @Nonnull PaintingJson painting) {
        return executeRequest(gatewayApi.updatePainting(bearerToken, painting));
    }

    @Step("Send GET /api/session/ request to rococo-gateway")
    public Response<SessionJson> getSessionUser(@Nonnull String bearerToken) {
        return executeRequest(gatewayApi.getSessionUser(bearerToken));
    }

    @Step("Send GET /api/user request to rococo-gateway ")
    public Response<UserJson> getUser(@Nonnull String bearerToken) {
        return executeRequest(gatewayApi.getUser(bearerToken));
    }

    @Step("Send PATCH /api/user request to rococo-gateway")
    public Response<UserJson> updateUser(@Nonnull String bearerToken, @Nonnull UserJson user) {
        return executeRequest(gatewayApi.updateUser(bearerToken, user));
    }

    @Nonnull
    private <T> Response<T> executeRequest(@Nonnull Call<T> call) {
        try {
            return call.execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
