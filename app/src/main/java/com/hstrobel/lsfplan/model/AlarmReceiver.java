package com.hstrobel.lsfplan.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.hstrobel.lsfplan.Constants;
import com.hstrobel.lsfplan.GlobalState;
import com.hstrobel.lsfplan.model.calender.CalenderUtils;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

        GlobalState state = GlobalState.getInstance();
        if (state.myCal == null) {
            Log.i(TAG, "mycal null");
            return;
        }

        int minutesBefore = Integer.parseInt(state.settings.getString("notfiyTime", "15"));
        List<VEvent> events = CalenderUtils.getNextEvents(state.myCal, minutesBefore);
        String[] eventUids = new String[events.size()];
        for (int i = 0; i < events.size(); i++) {
            VEvent event = events.get(i);
            eventUids[i] = event.getUid().getValue();
        }

        Log.i(TAG, "ScheduleNextEventNot: ");
        Log.i(TAG, "ScheduleNextEventNot: events size " + eventUids.length);
        if (eventUids.length == 0)
            return;


        Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeInMillis(CalenderUtils.getNextRecuringStartDate(events.get(0)).getTime()); //all should have the same start time
        cal.add(Calendar.MINUTE, -minutesBefore);

        //notifyDebug is dev options
        //sets the newxt alarm to trigger in 30 secs
        if (state.settings.getBoolean(Constants.PREF_DEV_NOTIFY, false)) {
            cal.setTimeInMillis(new DateTime().getTime() + 30 * 1000);
            Log.w(TAG, "ScheduleNextEventNot: notifyDebug is on!!");
        }


        Intent intentAlarm = new Intent(c, AlarmReceiver.class);
        intentAlarm.setAction(INTENT_ACTION);
        intentAlarm.putExtra(INTENT_EVENT, eventUids);
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
            GlobalState state = GlobalState.getInstance();
            if (!state.initialized)
                state.InitCalender(context, false);

            enforceInit(context);

            //Hide old notifications
            Integer[] oldNotifyIds = (Integer[]) intent.getSerializableExtra(INTENT_OLD_ID);
            if (oldNotifyIds != null) {
                for (int id : oldNotifyIds) {
                    NotificationUtils.killNotification(id, context);
                }
            }

            //Display the new notifications
            String[] eventUids = (String[]) intent.getSerializableExtra(INTENT_EVENT);
            List<Integer> notifyIDs = new ArrayList<>();
            if (eventUids != null) {
                for (String eventUid : eventUids) {
                    VEvent event = getEventForUid(eventUid);

                    if (event == null) {
                        Log.w(TAG, "onReceive: could not find event " + eventUid);
                        continue;
                    }

                    int id = NotificationUtils.showNotification(event, context);
                    notifyIDs.add(id);
                }
            } else {
                Log.w(TAG, "onReceive: events are null");
            }

            ScheduleNextEventNot(context, notifyIDs.toArray(new Integer[]{}));
        } catch (Exception ex) {
            Log.e(TAG, "onReceive: ", ex);
        }

    }

    private VEvent getEventForUid(String uid) {
        for (Object c : GlobalState.getInstance().myCal.getComponents(Component.VEVENT)) {
            if (c instanceof VEvent) {
                VEvent tmpEvent = (VEvent) c;
                if (tmpEvent.getUid().getValue().equals(uid)) {
                    return tmpEvent;
                }
            }
        }
        return null;
    }
}