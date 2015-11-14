package com.hstrobel.lsfplan.classes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("LSF", "onReceive");
        try {
            //Process could be killed in the meantime, setup our calender, but NOT init the notifications to prevent duplicates loops etc
            if (!Globals.initalized) Globals.InitCalender(context, false);
            VEvent event = (VEvent) intent.getSerializableExtra("event");
            CalenderUtils.showNotfication(event, context);
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

        List<VEvent> events = CalenderUtils.GetNextEvent(Globals.myCal);

        if (events.size() > 1) {
            Log.i("LSF", "events size bigger 1, size is " + events.size());
            return;
        }

        Date start = CalenderUtils.GetNextRecuringStartDate(events.get(0));
        int minutesBefore = Integer.parseInt(Globals.mSettings.getString("notfiyTime", "15"));
        start.setTime(start.getTime() - minutesBefore * 60 * 1000);

        //DEBUG DEBUG REMOVE IT
        //start.setTime(new DateTime().getTime() + 30 * 1000);

        Intent intentAlarm = new Intent(c, AlarmReceiver.class);
        intentAlarm.putExtra("event", events.get(0));

        AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, start.getTime(), PendingIntent.getBroadcast(c, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));


        Log.i("LSF", "Alarm Scheduled for event " + events.get(0).getDescription().getValue());
        Log.i("LSF", "time sched  " + start.toString());

    }

    public static void CancelNextEventNot(Context c) {
        AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        Intent intentAlarm = new Intent(c, AlarmReceiver.class);
        alarmManager.cancel(PendingIntent.getBroadcast(c, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

    }
}