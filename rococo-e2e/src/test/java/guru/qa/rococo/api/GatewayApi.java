package guru.qa.rococo.api;

import guru.qa.rococo.model.*;
import guru.qa.rococo.model.rest.pageable.RestResponsePage;
import retrofit2.Call;
import retrofit2.http.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public interface GatewayApi {

    @GET("/api/artist")
    Call<RestResponsePage<ArtistJson>> getAllArtists(@Query("name") @Nullable String name,
                                                     @Query("page") int page,
                                                     @Query("size") int size);

    @GET("/api/artist/{id}")
    Call<ArtistJson> getArtistById(@Path("id") @Nonnull UUID id);

    @POST("/api/artist")
    Call<ArtistJson> createArtist(@Header("Authorization") String bearerToken,
                                  @Body ArtistJson artist);

    @PATCH("/api/artist")
    Call<ArtistJson> updateArtist(@Header("Authorization") String bearerToken,
                                  @Body ArtistJson artist);

    @GET("/api/country")
    Call<RestResponsePage<CountryJson>> getAllCountries(@Query("page") int page,
                                                        @Query("size") int size);

    @GET("/api/museum")
    Call<RestResponsePage<MuseumJson>> getAllMuseums(@Query("title") @Nullable String title,
                                                     @Query("page") int page,
                                                     @Query("size") int size);

    @GET("/api/museum/{id}")
    Call<MuseumJson> getMuseumById(@Path("id") @Nonnull UUID id);

    @POST("/api/museum")
    Call<MuseumJson> createMuseum(@Header("Authorization") String bearerToken,
                                  @Body MuseumJson museum);

    @PATCH("/api/museum")
    Call<MuseumJson> updateMuseum(@Header("Authorization") String bearerToken,
                                  @Body MuseumJson museum);

    @GET("/api/painting")
    Call<RestResponsePage<PaintingJson>> getAllPaintings(@Query("name") @Nullable String title,
                                                         @Query("page") int page,
                                                         @Query("size") int size);

    @GET("/api/painting/author/{id}")
    Call<RestResponsePage<PaintingJson>> getPaintingByArtist(@Path("id") UUID id,
                                                             @Query("page") int page,
                                                             @Query("size") int size
    );

    @GET("/api/painting/{id}")
    Call<PaintingJson> getPaintingById(@Path("id") @Nonnull UUID id);

    @POST("/api/painting")
    Call<PaintingJson> createPainting(@Header("Authorization") String bearerToken,
                                      @Body PaintingJson museum);

    @PATCH("/api/painting")
    Call<PaintingJson> updatePainting(@Header("Authorization") String bearerToken,
                                      @Body PaintingJson museum);

    @GET("api/session")
    Call<SessionJson> getSessionUser(@Header("Authorization") String bearerToken);

    @GET("/api/user")
    Call<UserJson> getUser(@Header("Authorization") String bearerToken);

    @PATCH("/api/user")
    Call<UserJson> updateUser(@Header("Authorization") String bearerToken,
                              @Body UserJson user);
}
