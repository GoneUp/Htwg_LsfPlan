package com.hstrobel.lsfplan.model;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.hstrobel.lsfplan.Constants;
import com.hstrobel.lsfplan.GlobalState;
import com.hstrobel.lsfplan.gui.download.network.IDownloadCallback;
import com.hstrobel.lsfplan.gui.download.network.IcsFileDownloader;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Henry on 06.04.2016.
 */
public class SyncService extends IntentService implements IDownloadCallback {
    private static String TAG = "LSF";

    public SyncService() {
        super("Background Calender Sync Service");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        try {
            Log.i(TAG, "onHandleIntent: SyncS started");

            //if the user is already downloading something new we don't interfere
            GlobalState state = GlobalState.getInstance();
            if (state.icsLoader != null)
                return;

            if (!state.settings.getBoolean("gotICS", false))
                return;

            if (!state.settings.getBoolean("enableRefresh", false))
                return;

            long time_load = state.settings.getLong("ICS_DATE", Integer.MAX_VALUE);
            GregorianCalendar now = new GregorianCalendar();
            //DEBUG REMOVE
            //now.add(Calendar.YEAR, 5);

            GregorianCalendar syncExpire = new GregorianCalendar();
            syncExpire.setTimeInMillis(time_load);
            syncExpire.add(Calendar.WEEK_OF_YEAR, 1); //weekly syncs

            if (now.getTimeInMillis() < syncExpire.getTimeInMillis()) {
                Log.i(TAG, "onHandleIntent: SyncS is already updated");

            } else {
                String url = state.settings.getString("ICS_URL", "");

                if (!url.isEmpty()) {
                    Log.i(TAG, "onHandleIntent: starting download");
                    state.icsLoader = new IcsFileDownloader(this, url);
                    new Thread(state.icsLoader).start();
                }
            /*The new Thread is not really needed since we are already on background task,
            but I wanted a single way to download the file from all classes.
             */
            }
        } catch (Exception ex) {
            Log.e(TAG, "SyncS Intent: ", ex);
        }
    }

    @Override
    public void FileLoaded() {
        try {
            if (GlobalState.getInstance().isDownloadInvalid()) {
                //not a ics file
                Log.i(TAG, "FileLoaded: Download not valid");
                return;
            }

            GlobalState.getInstance().SetNewCalendar(getApplicationContext());

            //Update our ui (if present)
            Intent in = new Intent();
            in.setAction(Constants.INTENT_UPDATE_LIST);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(in);
        } catch (Exception ex) {
            Log.e(TAG, "SyncS Fileloaded: ", ex);
        }
    }
}