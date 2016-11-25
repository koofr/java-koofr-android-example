package net.koofr.koofrexample;

import android.content.Intent;
import android.net.UrlQuerySanitizer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        webView = (WebView) findViewById(R.id.login_web);

        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.clearCache(true);
        webView.clearHistory();
        clearCookies();

        final String redirectUrl = KoofrClient.getRedirectUrl();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(redirectUrl)) {
                    UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(url);

                    String error = sanitizer.getValue("error");

                    if (error != null && !error.isEmpty()) {
                        String errorReason = sanitizer.getValue("error_reason");
                        // TODO handle error
                    } else {
                        String code = sanitizer.getValue("code");

                        new InitializeApiTask().execute(code);

                        webView.loadDataWithBaseURL("", "Loading...", "text/html", "UTF-8", "");
                    }

                    return false;
                } else {
                    return false;
                }
            }
        });

        webView.loadUrl(KoofrClient.getAuthUrl());
    }

    @SuppressWarnings("deprecation")
    private void clearCookies() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(this);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    private class InitializeApiTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... args) {
            String code = args[0];

            try {
                KoofrClient.initialize(code);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void self) {
            Intent intent = new Intent(LoginActivity.this, UserInfoActivity.class);
            startActivity(intent);
        }
    }
}
