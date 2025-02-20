package guru.qa.rococo.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.rococo.api.AuthApi;
import guru.qa.rococo.api.core.CodeInterceptor;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.api.core.ThreadSafeCookieStore;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.utils.OAuthUtils;
import lombok.SneakyThrows;
import retrofit2.Response;


public class AuthApiClient extends RestClient {

    private static final Config CFG = Config.getInstance();
    private static final String CLIENT_ID = "client";
    private static final String RESPONSE_TYPE = "code";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String SCOPE = "openid";
    private static final String CODE_CHALLENGE_METHOD = "S256";
    private static final String REDIRECT_URI = CFG.frontUrl() + "authorized";

    private final AuthApi authApi;

    public AuthApiClient() {
        super(CFG.authUrl(), true, new CodeInterceptor());
        this.authApi = create(AuthApi.class);
    }

    @SneakyThrows
    public String login(String username, String password) {
        final String codeVerifier = OAuthUtils.generateCodeVerifier();
        final String codeChallenge = OAuthUtils.generateCodeChallange(codeVerifier);

        authApi.authorize(
                RESPONSE_TYPE,
                CLIENT_ID,
                SCOPE,
                REDIRECT_URI,
                codeChallenge,
                CODE_CHALLENGE_METHOD
        ).execute();

        authApi.login(
                username,
                password,
                ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
        ).execute();

        Response<JsonNode> tokenResponse = authApi.token(
                ApiLoginExtension.getCode(),
                REDIRECT_URI,
                CLIENT_ID,
                codeVerifier,
                GRANT_TYPE
        ).execute();

        return tokenResponse.body().get("id_token").asText();
    }
}
