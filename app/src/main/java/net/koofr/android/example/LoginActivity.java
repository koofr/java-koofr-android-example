package net.koofr.android.example;

import android.content.Intent;
import android.net.UrlQuerySanitizer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getName();

    private WebView webView;

    @SuppressWarnings("SetJavaScriptEnabled")
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

        final String redirectUrl = ((App)getApplication()).getKoofrClient().getRedirectUrl();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(redirectUrl)) {
                    UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(url);

                    String error = sanitizer.getValue("error");

                    if (error != null && !error.isEmpty()) {
                        String errorReason = sanitizer.getValue("error_reason");
                        Log.w(TAG, "Failed to log in: " + errorReason);
                    } else {
                        String code = sanitizer.getValue("code");

                        new InitializeApiTask(LoginActivity.this).execute(code);

                        webView.loadDataWithBaseURL("", "Loading...", "text/html", "UTF-8", "");
                    }

                    return false;
                } else {
                    return false;
                }
            }
        });

        webView.loadUrl(((App)getApplication()).getKoofrClient().getAuthUrl());
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

    private static class InitializeApiTask extends AsyncTask<String, Void, Void> {
        WeakReference<LoginActivity> activityRef;

        public InitializeApiTask(LoginActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(String... args) {
            String code = args[0];
            try {
                ((App)activityRef.get().getApplication()).getKoofrClient().initialize(code);
            } catch (IOException e) {
                Log.w(TAG, "Failed to initialize Koofr API.", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void self) {
            Intent intent = new Intent(activityRef.get(), UserInfoActivity.class);
            activityRef.get().startActivity(intent);
        }
    }
}
