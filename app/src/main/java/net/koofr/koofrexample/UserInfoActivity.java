package net.koofr.koofrexample;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.koofr.api.json.JsonException;
import net.koofr.api.rest.v2.data.Self;

import java.io.IOException;

public class UserInfoActivity extends AppCompatActivity {
    TextView nameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        nameTextView = (TextView) findViewById(R.id.user_name);

        nameTextView.setText("Loading...");

        new UserInfoTask().execute();
    }

    public void logout(View view) {
        KoofrClient.reset();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


    private class UserInfoTask extends AsyncTask<Void, Void, Self> {

        @Override
        protected Self doInBackground(Void... voids) {
            try {
                return KoofrClient.getApi().self().get();
            } catch (JsonException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Self self) {
            nameTextView.setText(self.firstName + " " + self.lastName);
        }
    }
}
