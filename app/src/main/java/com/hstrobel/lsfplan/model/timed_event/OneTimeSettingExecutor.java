package com.hstrobel.lsfplan.model.timed_event;

import android.content.SharedPreferences;
import android.util.Log;

public class OneTimeSettingExecutor {
    public static void checkAndOneTimeExecute(SharedPreferences prefs, String preferenceName, OnOnTimeExecuteListener listener) {
        boolean alreadyExecuted = prefs.getBoolean(preferenceName, false);

        if (alreadyExecuted) {
            Log.i("OneTimeSettingExecutor", String.format("Already executed pref %s, exiting", preferenceName));
            return;
        }

        Log.i("OneTimeSettingExecutor", String.format("calling onetime event for %s", preferenceName));
        listener.onOneTimeEvent();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(preferenceName, true);
        editor.apply();

    }

    public static interface OnOnTimeExecuteListener {
        public void onOneTimeEvent();
    }

}
