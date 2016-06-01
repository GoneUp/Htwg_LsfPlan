package com.hstrobel.lsfplan.classes;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Henry on 06.04.2016.
 */
public class SyncService extends IntentService implements DownloadCallback {
    private static String TAG = "LSF";

    public SyncService() {
        super("Background Calender Sync Service");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();
        Log.i(TAG, "onHandleIntent: SyncS started");

        //if the user is already downloading something new we don't interfere
        if (Globals.icsLoader != null)
            return;

        if (!Globals.mSettings.getBoolean("gotICS", false))
            return;

        updateEventCache();

        if (!Globals.mSettings.getBoolean("enableRefresh", false))
            return;

        long time_load = Globals.mSettings.getLong("ICS_DATE", Integer.MAX_VALUE);
        GregorianCalendar now = new GregorianCalendar();
        //DEBUG REMOVE
        now.add(Calendar.YEAR, 5);

        GregorianCalendar syncExpire = new GregorianCalendar();
        syncExpire.setTimeInMillis(time_load);
        syncExpire.add(Calendar.WEEK_OF_YEAR, 1); //weekly syncs

        if (now.getTimeInMillis() < syncExpire.getTimeInMillis()) {
            Log.i(TAG, "onHandleIntent: SyncS is already updated");

        } else {
            String url = Globals.mSettings.getString("ICS_URL", "");

            if (!url.isEmpty()) {
                Log.i(TAG, "onHandleIntent: starting download");
                Globals.icsLoader = new ICSLoader(this, new Handler(), url);
                new Thread(Globals.icsLoader).start();
            }
            /*The new Thread is not really needed since we are already on background task,
            but I wanted a single way to download the file from all classes.
             */
        }
    }

    @Override
    public void FileLoaded() {
        try {
            if (Globals.isDownloadValid()) {
                //not a ics file
                Log.i(TAG, "FileLoaded: Download not valid");
                return;
            }

            Globals.SetNewCalendar(getApplicationContext());

            if (Globals.mainActivity != null && Globals.mainActivity.mListFragment != null) {
                //Update main view
                Globals.mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Globals.mainActivity.mListFragment.onResume();
                    }
                });
            }

            updateEventCache();
        } catch (Exception ex) {
            Log.e("LSF", "FAIL DL:\n " + ExceptionUtils.getCause(ex));
            Log.e("LSF", "FAIL DL ST:\n " + ExceptionUtils.getFullStackTrace(ex));
        }
    }

    private void updateEventCache() {
        if (Globals.mainActivity != null && Globals.mainActivity.mListFragment != null) {
            Globals.mainActivity.mListFragment.onCheckCache();
        }
    }
}