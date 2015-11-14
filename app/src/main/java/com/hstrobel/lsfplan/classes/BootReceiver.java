package com.hstrobel.lsfplan.classes;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.apache.commons.lang.exception.ExceptionUtils;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.i("LSF", "Setting Bootup");
            try {
                Globals.InitCalender(context, true);
            } catch (Exception ex) {
                Log.e("LSF", "FAIL Bootup:\n " + ExceptionUtils.getCause(ex));
                Log.e("LSF", "FAIL Bootup ST:\n " + ExceptionUtils.getFullStackTrace(ex));
            }

        }
    }
}