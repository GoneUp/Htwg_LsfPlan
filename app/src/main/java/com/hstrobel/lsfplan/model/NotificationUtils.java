package com.hstrobel.lsfplan.model;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.hstrobel.lsfplan.Constants;
import com.hstrobel.lsfplan.GlobalState;
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
    private static final String TAG = "LSF";

    @NonNull
    public static String getTopic(VEvent event) {
        //1453052420 - IT-Security
        String[] tops = event.getSummary().getValue().split(" - ");
        if (tops.length > 1) {
            return tops[1].trim();
        }
        return event.getSummary().getValue().trim();
    }


    @Nullable
    private static int[] getUidsInternal(VEvent event) {
        //UID:158533 285435
        //course id (veranstid) - event series id( 285435)
        String uid = event.getUid().getValue();
        if (uid.length() != 12) {
            Log.e(TAG, "getCourseId: FU");
            return null;
        }
        String courseId = uid.substring(0, 5);
        String eventId = uid.substring(6, 11);

        try {
            return new int[]{Integer.parseInt(courseId), Integer.parseInt(eventId)};
        } catch (Exception ex) {
            Log.e(TAG, "getCourseId: not a number " + ex);
        }

        return null;
    }

    public static int getCourseId(VEvent event) {
        int[] uids = getUidsInternal(event);


        if (uids != null) {
            return uids[0];
        }
        return 0;
    }

    public static int getEventId(VEvent event) {
        int[] uids = getUidsInternal(event);
        if (uids != null) {
            return uids[1];
        }
        return 0;
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
        String mode = GlobalState.getInstance().settings.getString("soundMode", "");
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

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context, mode.equals("Vibrate") || mode.equals("Sound"));
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ALARMS_ID)
                .setContentTitle(context.getString(R.string.notification_title)) // title for notification
                .setContentText(formatEventShort(event, context)) // message for notification
                .setAutoCancel(true) // clear notification after click
                .setStyle(new NotificationCompat.BigTextStyle().bigText(formatEventLong(event, context)))
                .setDefaults(soundMode)
                .setCategory(NotificationCompat.CATEGORY_EVENT);

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

    @RequiresApi(26)
    private static void createNotificationChannel(Context context, boolean vibrate) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //User visible
        CharSequence name = context.getString(R.string.channel_name);
        String description = context.getString(R.string.channel_description);

        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel mChannel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ALARMS_ID, name, importance);

        mChannel.setDescription(description);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.GREEN);

        mChannel.enableVibration(vibrate);
        mNotificationManager.createNotificationChannel(mChannel);

    }

    static void killNotification(int id, Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(id);
    }
}
