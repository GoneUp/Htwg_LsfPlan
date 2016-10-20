package com.hstrobel.lsfplan.model;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hstrobel.lsfplan.Globals;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "LSF";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.i(TAG, "Setting Bootup");
            try {
                Globals.InitCalender(context, true);
            } catch (Exception ex) {
                Log.e(TAG, "Bootup: ", ex);
            }

        }
    }
}