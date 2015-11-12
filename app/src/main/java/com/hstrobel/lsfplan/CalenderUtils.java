package com.hstrobel.lsfplan;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.component.VEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Henry on 10.11.2015.
 */
public class CalenderUtils {
    public static Collection GetTodaysEvents(Calendar myCal) {
        java.util.Calendar today = java.util.Calendar.getInstance();
        //today.set(java.util.Calendar.HOUR_OF_DAY, 0);
        today.clear(java.util.Calendar.MINUTE);
        today.clear(java.util.Calendar.SECOND);

        // create a period starting now with a duration of one (1) day..
        Period period = new Period(new DateTime(today.getTime()), new Dur(1, 0, 0, 0));
        Filter filter = new Filter(new PeriodRule(period));

        Collection eventsToday = filter.filter(myCal.getComponents(Component.VEVENT));
        return eventsToday;
    }

    public static Collection<VEvent> GetNextEvent(Calendar myCal) {
        java.util.Calendar calEnd = java.util.Calendar.getInstance();
        calEnd.set(java.util.Calendar.YEAR, 3000);
        Date starttR = null;
        Collection<VEvent> toReturn = new ArrayList<VEvent>();

        Collection events = GetTodaysEvents(myCal);
        for (Object comp : events) {
            if (!(comp instanceof VEvent)) continue;

            VEvent event = (VEvent) comp;
            if (toReturn.isEmpty()) {
                //first element
                toReturn.add(event);
                starttR = GetNextRecuringStartDate(event);
            } else {
                //compare to get the start datw wich is a) in future b) the closest
                Date startE = GetNextRecuringStartDate(event);

                //past check
                if (startE.before(new java.util.Date())) continue;

                if (startE.compareTo(starttR) == 0) {
                    //equal
                    toReturn.add(event);

                } else if (startE.compareTo(starttR) < 0) {
                    //closer
                    toReturn.clear();
                    toReturn.add(event);
                    starttR = startE;
                }
            }

        }

        return toReturn;
    }

    public static DateTime GetNextRecuringStartDate(VEvent event) {
        DateTime start = new DateTime();

        Period period = new Period(new DateTime(start.getTime()), new Dur(14, 0, 0, 0));
        PeriodList r = event.calculateRecurrenceSet(period);

        Period closest = null;
        for (Period p : (Iterable<Period>) r) {
            System.out.println(" - " + p.toString());
            if (closest == null) {
                closest = p;
            } else {
                if (p.compareTo(closest) < 0) {
                    closest = p;
                }
            }
        }

        return closest.getStart();
    }

    public static String formatEventLong(VEvent event, Context c) {
        String topic = event.getSummary().getValue().split("-")[1].trim();
        java.util.Date time = GetNextRecuringStartDate(event);
        String room = event.getLocation().getValue();
        return String.format(c.getString(R.string.notification_long), topic, time, room);
    }

    public static String formatEventShort(VEvent event, Context c) {
        java.util.Date time = GetNextRecuringStartDate(event);
        String room = event.getLocation().getValue();
        return String.format(c.getString(R.string.notification_short), time, room);
    }

    public static int getId(VEvent event){
        //SUMMARY:14220920 - Rechnerarchitekturen
        String num = event.getSummary().getValue().split("-")[0].trim();
        return Integer.parseInt(num);
    }

    public static void showNotfication(VEvent event, Context context) {
        String mode = Globals.mSettings.getString("soundMode", "");
        int soundMode = NotificationCompat.DEFAULT_LIGHTS;

        if (mode.equals("Silent")){
            soundMode = NotificationCompat.DEFAULT_LIGHTS;
        } else if (mode.equals("Vibrate")) {
            soundMode = NotificationCompat.DEFAULT_VIBRATE;
        } else if (mode.equals("Sound")) {
            soundMode = NotificationCompat.DEFAULT_SOUND;
        }

        NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp) // notification icon
                .setContentTitle(context.getString(R.string.notification_title)) // title for notification
                .setContentText(formatEventShort(event, context)) // message for notification
                .setAutoCancel(true) // clear notification after click
                .setStyle(new NotificationCompat.BigTextStyle().bigText(formatEventLong(event, context)))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(soundMode);


        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(getId(event), mBuilder.build());
    }
}
