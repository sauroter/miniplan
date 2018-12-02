package de.sauroter.miniplan.task;

import android.app.Application;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import de.sauroter.miniplan.miniplan.R;

public class CheckUserCredentialsTask extends AsyncTask<Void, Integer, Boolean> {

    @Nullable
    private final String password;
    @Nullable
    private final String username;
    @NonNull
    private final Application application;

    public CheckUserCredentialsTask(@Nullable final String username,
                                    @Nullable final String password,
                                    @NonNull final Application application) {
        this.username = username;
        this.password = password;
        this.application = application;
    }


    @Override
    protected Boolean doInBackground(Void... voids) {

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return false;
        }


        final Uri uri = new Uri.Builder().scheme("https")
                .authority("majo-minis.de")
                .path("App/Handy.php")
                .appendQueryParameter("Name", username)
                .appendQueryParameter("Passwort", password)
                .build();


        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(uri.toString()).openConnection();
            final int responseCode = urlConnection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                return false;
            }

            final String success = application.getResources().getString(R.string.csv_success);
            final String response = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())).readLine();

            if (response.contains(success)) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            urlConnection.disconnect();
        }
        return false;
    }
}
