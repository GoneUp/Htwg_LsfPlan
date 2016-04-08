package com.hstrobel.lsfplan.classes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.TimeZone;

/**
 * Created by Henry on 10.11.2015.
 */
public class CalenderUtils {
    private static Comparator<VEvent> comparator = new Comparator<VEvent>() {
        public int compare(VEvent c1, VEvent c2) {
            return (timeWithoutDate(c1).compareTo(timeWithoutDate(c2)));
        }
    };

    public static List<VEvent> getEventsNextWeek(Calendar myCal) {
        java.util.Calendar today = java.util.Calendar.getInstance();
        return getEvents(myCal, today, new Dur(7, 0, 0, 0));
    }

    public static List<VEvent> getEventsForDay(Calendar myCal, java.util.Calendar date) {
        date.clear(java.util.Calendar.HOUR);
        date.clear(java.util.Calendar.HOUR_OF_DAY);
        date.clear(java.util.Calendar.MINUTE);
        date.clear(java.util.Calendar.SECOND);
        System.out.print(date.toString());
        return getEvents(myCal, date, new Dur(1, 0, 0, 0));
    }

    private static List<VEvent> getEvents(Calendar myCal, java.util.Calendar date, Dur duration) {
        Period period = new Period(new DateTime(date.getTime()), duration);
        Filter filter = new Filter(new PeriodRule(period));

        Collection<VEvent> eventsTodayC = filter.filter(myCal.getComponents(Component.VEVENT));
        return new ArrayList<>(eventsTodayC);
    }

    private static java.util.Calendar timeWithoutDate(VEvent c1) {
        java.util.Calendar d = java.util.Calendar.getInstance();
        d.setTimeInMillis(c1.getStartDate().getDate().getTime());
        d.set(0, 0, 0);
        return d;
    }

    private static DateTime dateWithOutTime(VEvent c1) {
        java.util.Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(c1.getStartDate().getDate().getTime());
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.HOUR, 0);
        return new DateTime(cal.getTimeInMillis());
    }

    public static List<VEvent> sortEvents(List<VEvent> events) {
        Collections.sort(events, comparator);
        return events;// use the comparator as much as u want
    }


    public static List<VEvent> getNextEvent(Calendar myCal) {
        DateTime starttR = null;
        List<VEvent> toReturn = new ArrayList<VEvent>();
        int minutesBefore = Integer.parseInt(Globals.mSettings.getString("notfiyTime", "15"));
        java.util.Date fewestTime = new java.util.Date(System.currentTimeMillis() + ((minutesBefore + 5) * 60 * 1000)); //07.45 + 15 min notify time + 5 min puffer, past cherck

        List events = getEventsNextWeek(myCal);
        for (Object comp : events) {
            if (!(comp instanceof VEvent)) continue;

            VEvent event = (VEvent) comp;
            DateTime startE = getNextRecuringStartDate(event);
            if (startE.before(fewestTime)) continue;

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

    public static DateTime getNextRecuringStartDate(VEvent event) {
        //start from now, function is for notifications only
        return getNextRecuringStartDate(event, new DateTime());
    }

    public static DateTime getNextRecuringStartDate(VEvent event, DateTime start) {
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
        Dur d = new Dur(event.getStartDate().getDate(), event.getEndDate().getDate());
        Date time_start = getNextRecuringStartDate(event, dateWithOutTime(event));
        Date time_end = d.getTime(time_start);

        String room = event.getLocation().getValue();
        return String.format(c.getString(R.string.notification_short), time_start, time_end, room);
    }

    public static String formatDate(VEvent event) {
        Dur d = new Dur(event.getStartDate().getDate(), event.getEndDate().getDate());
        Date time_start = getNextRecuringStartDate(event, dateWithOutTime(event));

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.GERMANY);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(time_start);
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

        switch (mode) {
            case "Silent":
                soundMode = NotificationCompat.DEFAULT_LIGHTS;
                break;
            case "Vibrate":
                soundMode = NotificationCompat.DEFAULT_VIBRATE;
                break;
            case "Sound":
                soundMode = NotificationCompat.DEFAULT_SOUND;
                break;
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.notification_title)) // title for notification
                .setContentText(formatEventShort(event, context)) // message for notification
                .setAutoCancel(true) // clear notification after click
                .setStyle(new NotificationCompat.BigTextStyle().bigText(formatEventLong(event, context)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(soundMode);

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            mBuilder.setSmallIcon(R.drawable.ic_notify_white);
        } else {
            mBuilder.setSmallIcon(R.drawable.ic_notifications_black_24dp);
        }



        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(getId(event), mBuilder.build());
    }
}
