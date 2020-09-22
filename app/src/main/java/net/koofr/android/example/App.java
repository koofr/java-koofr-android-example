package net.koofr.android.example;

import android.app.Application;
import android.content.Context;

public class App extends Application {
    private KoofrClient koofrClient;

    public void onCreate() {
        super.onCreate();

        synchronized(this) {
            koofrClient = new KoofrClient(this);
        }
    }

    public KoofrClient getKoofrClient() {
        return koofrClient;
    }
}
