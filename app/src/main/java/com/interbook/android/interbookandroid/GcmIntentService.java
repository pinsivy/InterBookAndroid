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

    /* * Constantes permettant la r�cup�ration des informations * du message dans les extras de la notification envoy�e * par le serveur de notification. */
    // R�cup�ration de l'identification du message
    static final String MESSAGE_ID = "id";
    // R�cup�ration du nom du message
    static final String MESSAGE_TITRE = "titre";
    // R�cup�ration de la date et heure du message
    static final String MESSAGE_DATE_CREATION = "dateCreation";
    // R�cup�ration du texte du message
    static final String MESSAGE_TEXTE = "texte";

    public GcmIntentService() {
        super("GcmIntentService");
    }
    public static final String TAG = "GCM Demo";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // Le param�tre intent de la m�thode getMessageType() est la notification push
        // re�ue par le BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            /* * On filtre le message (ou notification) sur son type. * On met de c�t� les messages d'erreur pour nous concentrer sur * le message de notre notification. */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
                // Si c'est un message "classique".
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Cette boucle repr�sente le travail qui devra �tre effectu�.
                for (int i = 0; i < 5; i++) {
                    Log.i(TAG, "Working... " + (i + 1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Traite les informations se trouvant dans l'extra de l'intent
                // pour g�n�rer une notifiation android que l'on enverra.
                sendMessageNotification(extras);
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // On indique � notre broadcastReceiver que nous avons fini le traitement.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    /** * Cette m�thode permet de cr�er une notification � partir * d'un message pass� en param�tre. */
    private void sendNotification(String msg) {
        Log.d(TAG, "Preparing to send notification...: " + msg);
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
        Log.d(TAG, "Notification ID = " + NOTIFICATION_ID);
        Log.d(TAG, "Notification sent successfully.");
    }

    /** * Cette m�thode permet � partir des informations envoy�es par le serveur
     * * de notification de cr�er le message et la notification � afficher sur
     * * le terminal de l'utilisateur. *
     * * @param extras les extras envoy�s par le serveur de notification */
    private void sendMessageNotification(Bundle extras) {
        Log.d(TAG, "Preparing to send notification with message...: " + extras.toString());
        // On cr�e un objet Message � partir des informations r�cup�r�es dans
        // le flux JSON du message envoy� par l'application server
        Message msg = extractMessageFromExtra(extras);
        // On associe notre notification � une Activity. Ici c'est l'activity
        // qui affiche le message � l'utilisateur
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
            new Intent(this, MainActivity.class).putExtra("argument", msg), 0);

        // On r�cup�re le gestionnaire de notification android
        mNotificationManager = (NotificationManager)
        this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.common_signin_btn_icon_dark)
                        .setContentTitle("Le nom de l'application par exemple")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(MESSAGE_TEXTE))
                        .setContentText(extras.getString(MESSAGE_TEXTE))
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_SOUND);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Log.d(TAG, "Notification sent successfully.");
    }

    /** * Cette m�thode permet d'extraire les informations du message de la notification * afin de cr�er un message. * @param extras l'objet contenant les informations du message. * @return le message */
    private Message extractMessageFromExtra(Bundle extras) {
        Message msg = null;
        if (extras != null) {

            final String id = extras.getString(MESSAGE_ID);
            try {
                msg = new Message.Builder()
                        .timeToLive(5)
                        .addData("id", id)
                        .addData("titre", MESSAGE_TITRE)
                        .addData("dateCreation", MESSAGE_DATE_CREATION)
                        .addData("texte", MESSAGE_TEXTE)
                        .build();
            } catch (NumberFormatException nfe) {
                Log.e("error", "le message n'a pas un identifiant valide. "+nfe.getMessage());
                nfe.printStackTrace();
            }
            /*final String dateTime = extras.getString(MESSAGE_DATE_CREATION);
            try {
                msg.setDateMessage(new Date(Long.parseLong(dateTime)));
            } catch (NumberFormatException nfe) {
                Log.e("error : le message n'a pas une date valide. ",nfe.getMessage());
                nfe.printStackTrace();
            }
            msg.setTitre(extras.getString(MESSAGE_TITRE));
            msg.setTexte(extras.getString(MESSAGE_TEXTE));*/
        }
        Log.d(TAG, "extractMessageFromExtra - fin");
        return msg;
    }

}
