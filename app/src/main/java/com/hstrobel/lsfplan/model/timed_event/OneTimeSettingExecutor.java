package com.hstrobel.lsfplan.model.timed_event;

import android.content.SharedPreferences;
import android.util.Log;

public class OneTimeSettingExecutor {
    public static final String TAG = "OneTimeSettingExecutor";

    public static void checkAndOneTimeExecute(SharedPreferences prefs, String preferenceName, OnOnTimeExecuteListener listener) {
        try {
            boolean alreadyExecuted = prefs.getBoolean(preferenceName, false);
            if (alreadyExecuted) {
                Log.i(TAG, String.format("Already executed pref %s, exiting", preferenceName));
                return;
            }

            Log.i(TAG, String.format("calling onetime event for %s", preferenceName));
            listener.onOneTimeEvent();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(preferenceName, true);
            editor.apply();

        } catch (Exception ex) {
            Log.e(TAG, "excpection: ", ex);
        }
    }

    public static interface OnOnTimeExecuteListener {
        public void onOneTimeEvent();
    }

}
