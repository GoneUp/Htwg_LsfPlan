package com.hstrobel.lsfplan;

import android.app.Application;
import android.util.Log;

import com.evernote.android.job.JobManager;
import com.hstrobel.lsfplan.model.job.BriefingJobCreator;


public class App extends Application {
    private final static String TAG = "LSF";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "App created");

        JobManager.create(this).addJobCreator(new BriefingJobCreator());
    }
}
