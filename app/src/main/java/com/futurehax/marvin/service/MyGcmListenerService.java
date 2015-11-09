package com.futurehax.marvin.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.futurehax.marvin.activities.MainActivity;
import com.futurehax.marvin.PreferencesProvider;
import com.futurehax.marvin.R;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by FutureHax on 10/21/15.
 */
public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    private static final String ACTION_REQUEST_HEARTBEAT = "request_beat";
    private static final String ACTION_MESSAGE = "message";
    private static final String ACTION_UPDATE = "update";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String action = data.getString("action");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Action: " + action);

        if (action == null) {
            return;
        }
        switch (action) {
            case ACTION_REQUEST_HEARTBEAT:
                HeartBeatService.sendHeartBeat(this);
                break;
            case ACTION_MESSAGE:
                sendNotification(data.getString("message_content"));
                break;
            case ACTION_UPDATE:
                handleUpdate(data.getString("url"));
                break;
        }
    }

    private void handleUpdate(String url) {


        Intent intent = new Intent(this, DownloadUpdateService.class);
        intent.putExtra(DownloadUpdateService.EXTRA_URL, url);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Marvin Update!")
                .setContentText(url)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        if (new PreferencesProvider(this).getAlertsEnabled()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("Marvin Alert")
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }
}