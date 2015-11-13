package com.hstrobel.lsfplan.classes;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.hstrobel.lsfplan.WebSelector;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.ConnectException;

/**
 * Created by Henry on 09.11.2015.
 */
public class Globals {
    public static WebSelector.ICSLoader loader = null;
    public static SharedPreferences mSettings;
    public static boolean initalized = false;
    public static boolean updated = false;
    public static boolean changed = false;
    public static String icsFile = null;
    public static InputStream icsFileStream = null;
    public static Calendar myCal = null;

    public static void InitCalender(Context c, boolean initNotification) throws IOException, ParserException {
        if (!initalized) {
            //Try to load from file
            mSettings = PreferenceManager.getDefaultSharedPreferences(c);
            myCal = null;
            if (mSettings.getBoolean("gotICS", false)) {
                //found something!
                icsFile = mSettings.getString("ICS_FILE", "");
                updated = true;
            }
            initalized = true;
        }

        if (updated) {
            icsFileStream = IOUtils.toInputStream(icsFile, "UTF-8");
            if (icsFileStream != null) {
                CalendarBuilder builder = new CalendarBuilder();
                myCal = builder.build(icsFileStream);
            }
            updated = false;
            if (initNotification){
                InitNotifications(c);
            }
        }
    }

    public static void InitNotifications(final Context c) {
        ComponentName receiver = new ComponentName(c, BootReceiver.class);
        PackageManager pm = c.getPackageManager();

        if (Globals.mSettings.getBoolean("enableNotifications", false)) {
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
            new Thread(new Runnable() {
                public void run() {
                    AlarmReceiver.ScheduleNextEventNot(c);
                }
            }).start();

        } else {
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
            AlarmReceiver.CancelNextEventNot(c);
        }
    }


    public static void Update(Activity act) throws IOException, ParserException {
        updated = true;
        InitCalender(act, false);
    }

    public static void Save() throws IOException, ValidationException {
        if (changed) {
            Writer w = new StringWriter();
            CalendarOutputter outputter = new CalendarOutputter();
            outputter.output(myCal, w);
            icsFile = w.toString();

            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString("ICS_FILE", Globals.icsFile);
            editor.commit();
        }

    }

}
