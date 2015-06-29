package com.interbook.android.interbookandroid;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.server.Message;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by dell on 15/06/2015.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    static final String MESSAGE_ID = "id";
    static final String MESSAGE_TITRE = "title";
    static final String MESSAGE_DATE_CREATION = "time";
    static final String MESSAGE_TEXTE = "message";

    public GcmIntentService() {
        super("GcmIntentService");
    }
    public static final String TAG = "GCM Demo";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                for (int i = 0; i < 5; i++) {
                    Log.i(TAG, "Working... " + (i + 1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }

                sendMessageNotification(extras);
            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, DashboardActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        //.setSmallIcon(R.drawable.ic_stat_gcm)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg)
                        .setDefaults(Notification.DEFAULT_ALL);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void sendMessageNotification(Bundle extras) {
        Message msg = extractMessageFromExtra(extras);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ReservationActivity.class).putExtra("argument", msg), PendingIntent.FLAG_UPDATE_CURRENT);

        mNotificationManager = (NotificationManager)
        this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.logo_launcher)
                        .setContentTitle("InterBook")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(MESSAGE_TEXTE))
                        .setContentText(extras.getString(MESSAGE_TEXTE))
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_SOUND);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private Message extractMessageFromExtra(Bundle extras) {
        Message msg = null;
        if (extras != null) {

            final String id = extras.getString(MESSAGE_ID);
            try {
                msg = new Message.Builder()
                        .timeToLive(5)
                        .addData("id", id)
                        .addData("titre", extras.getString(MESSAGE_TITRE))
                        .addData("dateCreation", extras.getString(MESSAGE_DATE_CREATION))
                        .addData("texte", extras.getString(MESSAGE_TEXTE))
                        .addData("nomEntreprise", extras.getString("nomEntreprise"))
                        .addData("idr", extras.getString("idr"))
                        .addData("debut", extras.getString("debut"))
                        .addData("fin", extras.getString("fin"))
                        .build();
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }
        return msg;
    }

}
