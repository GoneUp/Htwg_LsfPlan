package com.hstrobel.lsfplan.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.hstrobel.lsfplan.Globals;
import com.hstrobel.lsfplan.model.calender.CalenderUtils;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "LSF";

    private static final int NOTIFICATION_ID = 5955648;
    private static final String INTENT_ACTION = "LSF_ALARM";
    private static final String INTENT_EVENT = "event";
    private static final String INTENT_OLD_ID = "oldNotifyIds";

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


        Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeInMillis(CalenderUtils.getNextRecuringStartDate(events[0]).getTime()); //all should have the same start time
        int minutesBefore = Integer.parseInt(Globals.settings.getString("notfiyTime", "15"));
        cal.add(Calendar.MINUTE, -minutesBefore);

        //DEBUG DEBUG REMOVE IT
        //start.setTime(new DateTime().getTime() + 30 * 1000);

        Intent intentAlarm = new Intent(c, AlarmReceiver.class);
        intentAlarm.setAction(INTENT_ACTION);
        intentAlarm.putExtra(INTENT_EVENT, events);
        intentAlarm.putExtra(INTENT_OLD_ID, oldNotifyIds);


        PendingIntent alert = PendingIntent.getBroadcast(c, NOTIFICATION_ID, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), alert);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), alert);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), alert);
        }


        alarms.add(alert);

        for (VEvent e : events) {
            Log.i(TAG, "Alarm Scheduled for event " + e.getSummary().getValue());
        }
        Log.i(TAG, "time sched  " + SimpleDateFormat.getDateTimeInstance().format(cal.getTime()));

    }

    public static void CancelNextEventNot(Context c) {
        Log.i(TAG, "cancel sched");
        enforceInit(c);

        if (alarms == null) {
            return;
        }
        while (!alarms.isEmpty()) {
            alarmManager.cancel(alarms.remove());
        }

    }

    private static void enforceInit(Context c) {
        if (alarmManager == null) {
            alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        }
        if (alarms == null) {
            alarms = new LinkedList<>();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");
        try {
            if (!intent.getAction().equals(INTENT_ACTION)) {
                return;
            }

            //Process could be killed in the meantime, setup our calender, but NOT init the notifications to prevent duplicates loops etc
            if (!Globals.initialized)
                Globals.InitCalender(context, false);

            enforceInit(context);

            //Hide old notifications
            Integer[] oldNotifyIds = (Integer[]) intent.getSerializableExtra(INTENT_OLD_ID);
            if (oldNotifyIds != null) {
                for (int id : oldNotifyIds) {
                    NotificationUtils.killNotification(id, context);
                }
            }

            //Display the new notifications
            VEvent[] events = (VEvent[]) intent.getSerializableExtra(INTENT_EVENT);
            if (events != null) {
                Integer[] notifyIDs = new Integer[events.length];
                for (int i = 0; i < events.length; i++) {
                    notifyIDs[i] = NotificationUtils.showNotification(events[i], context);
                }

                ScheduleNextEventNot(context, notifyIDs);
            }
        } catch (Exception ex) {
            Log.e(TAG, "onReceive: ", ex);
        }
    }
}