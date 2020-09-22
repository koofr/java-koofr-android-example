package net.koofr.android.example;

import android.content.Context;
import android.util.Log;

import net.koofr.api.auth.oauth2.OAuth2Authenticator;
import net.koofr.api.http.Client;
import net.koofr.api.http.impl.basic.BasicClient;
import net.koofr.api.rest.v2.Api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class KoofrClient {
    private static final String TAG = KoofrClient.class.getName();

    Context ctx;
    Api api;

    public KoofrClient(Context ctx) {
        this.ctx = ctx;
    }
    public String getClientId() {
        return ctx.getString(R.string.koofr_client_id);
    }

    public String getClientSecret() {
        return ctx.getString(R.string.koofr_client_secret);
    }

    public String getRedirectUrl() {
        return "urn:ietf:wg:oauth:2.0:oob";
    }

    public String getAuthUrl() {
        String encodedRedirectUri = "";

        try {
            encodedRedirectUri = URLEncoder.encode(getRedirectUrl(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.w(TAG, "URL encoding failed.", e);
        }

        return "https://app.koofr.net/oauth2/auth?redirect_uri=" + encodedRedirectUri + "&response_type=code&client_id=" + getClientId() + "&scope=public&platform=android";
    }

    public String getTokenUrl() {
        return "https://app.koofr.net/oauth2/token";
    }

    public void initialize(String code) throws IOException {
        Client client = new BasicClient();
        OAuth2Authenticator auth = new OAuth2Authenticator(client, getTokenUrl(), getClientId(), getClientSecret(), getRedirectUrl());
        auth.setGrantCode(code);

        // NOTE save refresh token to preferences or use android account framework to store it,
        // in order to avoid future logins

        api = new Api("https://app.koofr.net/api/v2", auth, client);
    }

    public Api getApi() {
        return api;
    }

    public void reset() {
        api = null;
    }
}
