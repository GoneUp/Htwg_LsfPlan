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

import java.util.LinkedList;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "LSF";

    private static final int NOTIFICATION_ID = 5955648;
    private static AlarmManager alarmManager = null;
    private static LinkedList<PendingIntent> alarms = null;

    public static void ScheduleNextEventNot(Context c, Integer[] oldNotifyIds) {
        enforceInit(c);

        //Cancel old alarms
        CancelNextEventNot(c);

        if (Globals.myCal == null) {
            Log.i(TAG, "mycal null");
            return;
        }

        List<VEvent> tmpList = CalenderUtils.getNextEvent(Globals.myCal);
        VEvent[] events = tmpList.toArray(new VEvent[tmpList.size()]);

        Log.i(TAG, "ScheduleNextEventNot: ");
        Log.i(TAG, "ScheduleNextEventNot: events size " + events.length);
        if (events.length == 0)
            return;

        Date start = CalenderUtils.getNextRecuringStartDate(events[0]); //all should have the same start time
        int minutesBefore = Integer.parseInt(Globals.mSettings.getString("notfiyTime", "15"));
        start.setTime(start.getTime() - (minutesBefore) * 60 * 1000);

        //DEBUG DEBUG REMOVE IT
        //start.setTime(new DateTime().getTime() + 30 * 1000);

        Intent intentAlarm = new Intent(c, AlarmReceiver.class);
        intentAlarm.putExtra("event", events);
        intentAlarm.putExtra("oldNotifyIds", oldNotifyIds);


        PendingIntent alert = PendingIntent.getBroadcast(c, NOTIFICATION_ID, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, start.getTime(), alert);
        alarms.add(alert);

        for (VEvent e : events) {
            Log.i(TAG, "Alarm Scheduled for event " + e.getSummary().getValue());
        }
        Log.i(TAG, "time sched  " + start.toString());

    }

    public static void CancelNextEventNot(Context c) {
        Log.i(TAG, "cancel sched");
        enforceInit(c);

        if (alarms == null)
            return;
        while (!alarms.isEmpty())
            alarmManager.cancel(alarms.remove());

    }

    private static void enforceInit(Context c) {
        if (alarmManager == null)
            alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        if (alarms == null)
            alarms = new LinkedList<>();

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");
        try {
            //Process could be killed in the meantime, setup our calender, but NOT init the notifications to prevent duplicates loops etc
            if (!Globals.initialized)
                Globals.InitCalender(context, false);

            enforceInit(context);

            //Hide old notifications
            Integer[] oldNotifyIds = (Integer[]) intent.getSerializableExtra("oldNotifyIds");
            for (int id : oldNotifyIds) {
                NotificationUtils.killNotification(id, context);
            }

            //Display the new notifications
            VEvent[] events = (VEvent[]) intent.getSerializableExtra("event");
            Integer[] notifyIDs = new Integer[events.length];
            for (int i = 0; i < events.length; i++) {
                notifyIDs[i] = NotificationUtils.showNotification(events[i], context);
            }

            ScheduleNextEventNot(context, notifyIDs);
        } catch (Exception ex) {
            Log.e(TAG, "FAIL onReceive:\n " + ExceptionUtils.getCause(ex));
            Log.e(TAG, "FAIL onReceive ST:\n " + ExceptionUtils.getFullStackTrace(ex));
        }
    }
}