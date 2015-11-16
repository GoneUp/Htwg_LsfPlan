package com.hstrobel.lsfplan.classes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.lang.exception.ExceptionUtils;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("LSF", "onReceive");
        try {
            //Process could be killed in the meantime, setup our calender, but NOT init the notifications to prevent duplicates loops etc
            if (!Globals.initalized) Globals.InitCalender(context, false);
            VEvent[] events = (VEvent[]) intent.getSerializableExtra("event");
            for (VEvent e : events) {
                CalenderUtils.showNotfication(e, context);
            }
            ScheduleNextEventNot(context);
        } catch (Exception ex) {
            Log.e("LSF", "FAIL onReceive:\n " + ExceptionUtils.getCause(ex));
            Log.e("LSF", "FAIL onReceive ST:\n " + ExceptionUtils.getFullStackTrace(ex));
        }
    }

    public static void ScheduleNextEventNot(Context c) {
        //cancel for all cases
        CancelNextEventNot(c);

        if (Globals.myCal == null) {
            Log.i("LSF", "mycal null");
            return;
        }

        VEvent[] events = CalenderUtils.getNextEvent(Globals.myCal).toArray(new VEvent[0]);

        if (events.length == 0) {
            Log.i("LSF", "events size 0");
            return;
        } else if (events.length > 1) {
            Log.i("LSF", "events size bigger 1, size is " + events.length);
        }

        Date start = CalenderUtils.getNextRecuringStartDate(events[0]); //all should have the same start time
        int minutesBefore = Integer.parseInt(Globals.mSettings.getString("notfiyTime", "15"));
        start.setTime(start.getTime() - minutesBefore * 60 * 1000);

        //DEBUG DEBUG REMOVE IT
        //start.setTime(new DateTime().getTime() + 30 * 1000);

        Intent intentAlarm = new Intent(c, AlarmReceiver.class);
        intentAlarm.putExtra("event", events);

        AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, start.getTime(), PendingIntent.getBroadcast(c, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

        for (VEvent e : events) {
            Log.i("LSF", "Alarm Scheduled for event " + e.getSummary().getValue());
        }
        Log.i("LSF", "time sched  " + start.toString());

    }

    public static void CancelNextEventNot(Context c) {
        AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        Intent intentAlarm = new Intent(c, AlarmReceiver.class);
        alarmManager.cancel(PendingIntent.getBroadcast(c, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

    }
}