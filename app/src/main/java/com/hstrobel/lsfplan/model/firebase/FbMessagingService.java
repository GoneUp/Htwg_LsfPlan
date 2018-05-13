package com.hstrobel.lsfplan.model.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hstrobel.lsfplan.BuildConfig;
import com.hstrobel.lsfplan.Constants;
import com.hstrobel.lsfplan.R;
import com.hstrobel.lsfplan.gui.MainActivity;

public class FbMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @RequiresApi(26)
    private static void createNotificationChannel(Context context, boolean vibrate) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //User visible
        CharSequence name = context.getString(R.string.channel_remote_name);
        String description = context.getString(R.string.channel_remote_description);

        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel mChannel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_REMOTE_ID, name, importance);

        mChannel.setDescription(description);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.GREEN);

        mChannel.enableVibration(vibrate);
        if (mNotificationManager != null)
            mNotificationManager.createNotificationChannel(mChannel);
    }
    // [END receive_message]

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i(TAG, "From: " + remoteMessage.getFrom());

        //failsafe for testing
        if (remoteMessage.getData().containsKey("debug") && !BuildConfig.DEBUG) {
            return;
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.i(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.i(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody, String title) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = Constants.NOTIFICATION_CHANNEL_REMOTE_ID;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(this, true);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}