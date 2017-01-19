package com.hstrobel.lsfplan.model;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.hstrobel.lsfplan.Globals;
import com.hstrobel.lsfplan.R;
import com.hstrobel.lsfplan.gui.MainActivity;
import com.hstrobel.lsfplan.model.calender.CalenderUtils;

import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.component.VEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

/**
 * Created by Henry on 01.06.2016.
 */
public class NotificationUtils {
    public static String getTopic(VEvent event) {
        //1453052420 - IT-Security
        String[] tops = event.getSummary().getValue().split(" - ");
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
        Date time_start = CalenderUtils.getNextRecuringStartDate(event, CalenderUtils.dateWithOutTime(event));
        Date time_end = d.getTime(time_start);


        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.GERMANY);
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        String room = event.getLocation().getValue();

        return String.format(c.getString(R.string.notification_short), sdf.format(time_start), sdf.format(time_end), room);
    }

    public static String formatDate(VEvent event) {
        Date time_start = CalenderUtils.getNextRecuringStartDate(event, CalenderUtils.dateWithOutTime(event));

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.GERMANY);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(time_start);
    }


    public static int showNotification(VEvent event, Context context) {
        String mode = Globals.settings.getString("soundMode", "");
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

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.notification_title)) // title for notification
                .setContentText(formatEventShort(event, context)) // message for notification
                .setAutoCancel(true) // clear notification after click
                .setStyle(new NotificationCompat.BigTextStyle().bigText(formatEventLong(event, context)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(soundMode);

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.ic_notify_white);
        } else {
            builder.setSmallIcon(R.drawable.ic_notifications_black_24dp);
        }

        Random rnd = new Random();
        int notificationId = rnd.nextInt(100000);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pi);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationId, builder.build());

        return notificationId;
    }

    public static void killNotification(int id, Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(id);
    }
}
