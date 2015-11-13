package com.hstrobel.lsfplan.classes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.hstrobel.lsfplan.MainActivity;
import com.hstrobel.lsfplan.R;

import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.component.VEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Henry on 10.11.2015.
 */
public class CalenderUtils {
    public static List GetEventsNextWeek(Calendar myCal) {
        java.util.Calendar today = java.util.Calendar.getInstance();
        return GetEvents(myCal, today, new Dur(7, 0, 0, 0));
    }

    public static List GetEventsForDay(Calendar myCal, java.util.Calendar date) {
        date.clear(java.util.Calendar.HOUR);
        date.clear(java.util.Calendar.HOUR_OF_DAY);
        date.clear(java.util.Calendar.MINUTE);
        date.clear(java.util.Calendar.SECOND);
        System.out.print(date.toString());
        return GetEvents(myCal, date, new Dur(1, 0, 0, 0));
    }

    private static List GetEvents(Calendar myCal, java.util.Calendar date, Dur duration) {
        Period period = new Period(new DateTime(date.getTime()), duration);
        Filter filter = new Filter(new PeriodRule(period));

        Collection eventsTodayC = filter.filter(myCal.getComponents(Component.VEVENT));
        List eventsToday = new ArrayList(eventsTodayC);
        return eventsToday;
    }

    private static Comparator<VEvent> comparator = new Comparator<VEvent>() {
        public int compare(VEvent c1, VEvent c2) {
            return (TimeWithoutDate(c1).compareTo(TimeWithoutDate(c2)));
        }
    };

    private static java.util.Calendar TimeWithoutDate(VEvent c1) {
        java.util.Calendar d = java.util.Calendar.getInstance();
        d.setTimeInMillis(c1.getStartDate().getDate().getTime());
        d.set(0, 0, 0);
        return d;
    }

    private static DateTime DateWithOutTime(VEvent c1) {
        DateTime d = new DateTime(c1.getStartDate().getDate().getTime());
        d.setHours(0);
        d.setMinutes(0);
        return d;
    }

    public static List<VEvent> SortEvents(List<VEvent> events) {
        Collections.sort(events, comparator);
        return events;// use the comparator as much as u want
    }


    public static List<VEvent> GetNextEvent(Calendar myCal) {
        DateTime starttR = null;
        List<VEvent> toReturn = new ArrayList<VEvent>();

        List events = GetEventsNextWeek(myCal);
        for (Object comp : events) {
            if (!(comp instanceof VEvent)) continue;

            VEvent event = (VEvent) comp;
            DateTime startE = GetNextRecuringStartDate(event);
            if (startE.before(new java.util.Date())) continue;  //past check

            if (toReturn.isEmpty()) {
                //first element
                starttR = startE;
                toReturn.add(event);
            } else {
                //compare to get the start datw wich is a) in future b) the closest

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
        //start from now, function is for notifications only
        return GetNextRecuringStartDate(event, new DateTime());
    }

    public static DateTime GetNextRecuringStartDate(VEvent event, DateTime start) {
        Period period = new Period(start, new Dur(7, 0, 0, 0)); //1w
        PeriodList r = event.calculateRecurrenceSet(period);

        Period closest = null;
        for (Period p : (Iterable<Period>) r) {
            //System.out.println(" - " + p.toString());
            if (closest == null) {
                closest = p;
            } else {
                if (p.compareTo(closest) < 0) {
                    closest = p;
                }
            }
        }

        if (closest == null) throw new NoSuchElementException(event.getSummary().getValue());
        return closest.getStart();
    }

    public static String getTopic(VEvent event) {
        String[] tops = event.getSummary().getValue().split("-");
        if (tops.length > 1) {
            return tops[1].trim();
        }
        return event.getSummary().getValue().trim();
    }

    public static String formatEventLong(VEvent event, Context c) {
        String topic = getTopic(event);
        String room_time = formatEventShort(event, c);
        return String.format(c.getString(R.string.notification_long), topic) + room_time;
    }

    public static String formatEventShort(VEvent event, Context c) {
        DateTime time = GetNextRecuringStartDate(event, DateWithOutTime(event));
        String room = event.getLocation().getValue();
        return String.format(c.getString(R.string.notification_short), time, room);
    }

    public static int getId(VEvent event) {
        //SUMMARY:14220920 - Rechnerarchitekturen
        //edge case: not persist over shutdown, add saving?
        int num = java.util.Calendar.getInstance().get(java.util.Calendar.MILLISECOND) * 10;
        if (event.getDescription().getValue().equals("")){
            event.getDescription().setValue(Integer.toString(num));
            return num;
        }
        return Integer.parseInt(event.getDescription().getValue());
    }

    public static void showNotfication(VEvent event, Context context) {
        String mode = Globals.mSettings.getString("soundMode", "");
        int soundMode = NotificationCompat.DEFAULT_LIGHTS;

        if (mode.equals("Silent")) {
            soundMode = NotificationCompat.DEFAULT_LIGHTS;
        } else if (mode.equals("Vibrate")) {
            soundMode = NotificationCompat.DEFAULT_VIBRATE;
        } else if (mode.equals("Sound")) {
            soundMode = NotificationCompat.DEFAULT_SOUND;
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp) // notification icon
                .setContentTitle(context.getString(R.string.notification_title)) // title for notification
                .setContentText(formatEventShort(event, context)) // message for notification
                .setAutoCancel(true) // clear notification after click
                .setStyle(new NotificationCompat.BigTextStyle().bigText(formatEventLong(event, context)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(soundMode);


        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(getId(event), mBuilder.build());
    }
}
