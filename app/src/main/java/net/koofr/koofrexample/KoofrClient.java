package net.koofr.koofrexample;

import net.koofr.api.auth.Authenticator;
import net.koofr.api.auth.oauth2.OAuth2Authenticator;
import net.koofr.api.http.Client;
import net.koofr.api.http.impl.basic.BasicClient;
import net.koofr.api.rest.v2.Api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class KoofrClient {
    private static Api api;

    public static String getClientId() {
        return App.getAppContext().getString(R.string.koofr_client_id);
    }

    public static String getClientSecret() {
        return App.getAppContext().getString(R.string.koofr_client_secret);
    }

    public static String getRedirectUrl() {
        return "urn:ietf:wg:oauth:2.0:oob";
    }

    public static String getAuthUrl() {
        String encodedRedirectUri = "";

        try {
            encodedRedirectUri = URLEncoder.encode(getRedirectUrl(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "https://app.koofr.net/oauth2/auth?redirect_uri=" + encodedRedirectUri + "&response_type=code&client_id=" + getClientId() + "&scope=public&platform=android";
    }

    public static String getTokenUrl() {
        return "https://app.koofr.net/oauth2/token";
    }

    public static void initialize(String code) throws IOException {
        Client client = new BasicClient();
        OAuth2Authenticator auth = new OAuth2Authenticator(client, getTokenUrl(), getClientId(), getClientSecret(), getRedirectUrl());
        auth.setGrantCode(code);

        // TODO save refresh token
        // auth.getRefreshToken()

        api = new Api("https://app.koofr.net/api/v2", auth, client);
    }

    public static Api getApi() {
        return api;
    }

    public static void reset() {
        api = null;
    }
}
