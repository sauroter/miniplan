package de.sauroter.miniplan.model;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import java.util.List;

import de.sauroter.miniplan.data.AltarService;
import de.sauroter.miniplan.miniplan.R;
import de.sauroter.miniplan.task.FetchMiniplanDataAsyncTask;
import timber.log.Timber;

public class AltarServiceUpdateJobService extends JobService {
    public static int ID = 1;

    @Nullable
    private FetchMiniplanDataAsyncTask fetchMiniplanDataAsyncTask;

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(final JobParameters params) {

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        final String userName = prefs.getString(getResources().getString(R.string.tag_miniplan_username), "");
        final String password = prefs.getString(getResources().getString(R.string.tag_miniplan_password), "");

        if (userName.isEmpty() || password.isEmpty()) {
            Timber.e("Could not run AltarServiceUpdateJobService because of missing credentials");
            return true;
        }

        fetchMiniplanDataAsyncTask = new FetchMiniplanDataAsyncTask(password, userName, getApplication()) {
            @Override
            protected void onPostExecute(@Nullable final List<AltarService> altarServices) {
                super.onPostExecute(altarServices);
                Timber.d("AltarServiceUpdateJobService ran");

                if (altarServices == null || altarServices.isEmpty()) {
                    jobFinished(params, true);
                }
                jobFinished(params, false);
            }
        };
        fetchMiniplanDataAsyncTask.execute();

        return true;
    }

    @Override
    public boolean onStopJob(final JobParameters params) {

        if (fetchMiniplanDataAsyncTask != null) {
            fetchMiniplanDataAsyncTask.cancel(true);
        }

        return false;
    }
}
