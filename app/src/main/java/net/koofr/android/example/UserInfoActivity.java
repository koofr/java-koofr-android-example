package net.koofr.android.example;

import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import net.koofr.api.json.JsonException;
import net.koofr.api.rest.v2.data.Self;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class UserInfoActivity extends AppCompatActivity {
    private static final String TAG = UserInfoActivity.class.getName();

    TextView nameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        nameTextView = findViewById(R.id.user_name);

        nameTextView.setText("Loading...");

        new UserInfoTask(this).execute();
    }

    public void logout(View view) {
        ((App)getApplication()).getKoofrClient().reset();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


    private static class UserInfoTask extends AsyncTask<Void, Void, Self> {
        WeakReference<UserInfoActivity> activityRef;

        public UserInfoTask(UserInfoActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        protected Self doInBackground(Void... voids) {
            try {
                return ((App)activityRef.get().getApplication()).getKoofrClient().getApi().self().get();
            } catch (JsonException e) {
                Log.w(TAG, "Failed to fetch user info.", e);
            } catch (IOException e) {
                Log.w(TAG, "Failed to fetch user info.", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Self self) {
            activityRef.get().nameTextView.setText(self.firstName + " " + self.lastName);
        }
    }
}
