package com.hstrobel.lsfplan;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.JobIntentService;
import android.text.TextUtils;

import com.hstrobel.lsfplan.gui.download.CourseGroup;
import com.hstrobel.lsfplan.gui.download.network.IcsFileDownloader;
import com.hstrobel.lsfplan.model.AlarmReceiver;
import com.hstrobel.lsfplan.model.BootReceiver;
import com.hstrobel.lsfplan.model.SyncService;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

/**
 * Created by Henry on 09.11.2015.
 */
public class GlobalState {
    public static final String TAG = "LSF";
    private static GlobalState instance;

    public IcsFileDownloader icsLoader = null;
    public String icsFile = null; //ICS Calender as text
    public SharedPreferences settings;
    public boolean initialized = false;
    public boolean updated = false;
    public Calendar myCal = null;
    public List<CourseGroup> cachedPlans = null;

    public static GlobalState getInstance() {
        if (instance == null) {
            instance = new GlobalState();
        }
        return instance;
    }

    public void InitCalender(Context c, boolean initNotification) throws IOException, ParserException {
        //App Startup, load everthing from our settings file
        if (!initialized) {
            //Try to load from file
            settings = PreferenceManager.getDefaultSharedPreferences(c);
            fixInvalidSettingEntries();

            cachedPlans = null;
            myCal = null;
            if (settings.getBoolean("gotICS", false)) {
                //found something!
                icsFile = settings.getString("ICS_FILE", "");
                updated = true;
            }
            initialized = true;
        }

        //Update the Calender object with the n(new) fileStream
        if (updated) {
            if (icsFile != null) {
                CalendarBuilder builder = new CalendarBuilder();
                myCal = builder.build(new StringReader(icsFile));
            }
            updated = false;
            if (initNotification) {
                InitNotifications(c);
            }
        }

        //Start the background service that downloads a new caleander from time to time
        //TODO: move to jobscheduler, no timecritical background work
        SyncStart(c, false);
    }

    public void InitNotifications(final Context c) {
        ComponentName receiver = new ComponentName(c, BootReceiver.class);
        PackageManager pm = c.getPackageManager();

        if (settings.getBoolean("enableNotifications", false)) {
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
            new Thread(() -> AlarmReceiver.ScheduleNextEventNot(c, new Integer[0])).start();


        } else {
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
            AlarmReceiver.CancelNextEventNot(c);
        }
    }


    public void SetNewCalendar(Context act) throws IOException, ParserException {
        icsFile = icsLoader.getFile();
        updated = true;
        InitCalender(act, false);

        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("gotICS", true);
        editor.putString("ICS_FILE", icsFile);
        editor.putLong("ICS_DATE", java.util.Calendar.getInstance().getTimeInMillis());
        editor.putString("ICS_URL", icsLoader.getUrl());
        editor.apply();

        //Set to null to show that noting is download --> used by SyncService
        icsLoader = null;
    }

    public void Save() throws IOException, ValidationException {
        Writer w = new StringWriter();
        CalendarOutputter output = new CalendarOutputter();
        output.output(myCal, w);
        String newFile = w.toString();

        if (!icsFile.equals(newFile)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("ICS_FILE", icsFile);
            editor.apply();
        }
    }

    public boolean isDownloadInvalid() {
        return icsLoader.getFile() == null || !icsLoader.getFile().startsWith("BEGIN:VCALENDAR");
    }

    public void SyncStart(Context c, boolean forceRefresh) {
        Intent intent = new Intent();
        intent.putExtra(Constants.INTENT_EXTRA_REFRESH, forceRefresh);

        //Rejecting re-init on previously-failed class java.lang.Class<android.support.v4.app.JobIntentService$JobServiceEngineImpl>: java.lang.NoClassDefFoundError: Failed resolution of: Landroid/app/job/JobServiceEngine;
        //normal on pre oreo
        JobIntentService.enqueueWork(c, SyncService.class, Constants.SYNC_SERVICE_ID, intent);
    }

    public int getCollege() {
        int mode = settings.getInt(Constants.PREF_COLLEGE, -1);
        if (mode == -1) {
            setCollege(Constants.PREF_COLLEGE_DEFAULT);
            mode = Constants.PREF_COLLEGE_DEFAULT;
        }
        return mode;
    }

    public void setCollege(int mode) {
        SharedPreferences.Editor edit = settings.edit();
        edit.putInt(Constants.PREF_COLLEGE, mode);
        edit.apply();
    }

    private void fixInvalidSettingEntries() {
        if (TextUtils.isEmpty(settings.getString("notfiyTime", "15"))) {
            //prevent empty field
            settings.edit().putString("notfiyTime", "0").apply();
        }
    }


}
