package com.edis.eschool;


import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.edis.eschool.notification.NotificationDao;
import com.edis.eschool.pojo.Notifications;
import com.edis.eschool.sql.DatabaseHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by sadjang on 18/06/2018.
 */


//  FORMAT DE LA NOTIFICATION
// TYPE: contenu : dateServeur       /// separateur les deux points
// TYPE:  absence
// dateServer :12 javier 2019 / ou 12/12/2013 / bref une chaine de caracter
// contenu: contenu du  de la notification


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    DatabaseHelper myDb;
    private static String TAG = "MyFirebaseMessagingService";
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    Context context;

    @Override
    public void onNewToken(String s) {
        this.context = this;
        Log.e("NEW_TOKEN", s);
        String refreshedToken = s;
        SharedPreferences pref = getApplicationContext().getSharedPreferences(
                getString(R.string.shared_preference_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(getString(R.string.firebase_token), refreshedToken);
        editor.apply();
        Log.i(TAG, "End of new token commit");
        if (pref.contains(getString(R.string.phone_number))){
            sendRegistrationToServer(refreshedToken);
        }
    }

    private void sendRegistrationToServer(String token){
        // TODO send new token to server
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        this.context = this;
        myDb = DatabaseHelper.getInstance(this);
        Log.e("NEW_TOKEN", "reception de la notification");
        if (remoteMessage != null) {
            final String title = remoteMessage.getNotification().getTitle();
            final String body = remoteMessage.getNotification().getBody();
            String click_action = remoteMessage.getNotification().getClickAction();
            Notifications notifcation = insertInsqlite(title, body);
            if (notifcation != null) {
                createNotification(title, body, click_action, notifcation);
                sendBroacast(title, body);
            }

        }
        assert remoteMessage != null;
        if (remoteMessage.getData().size() > 0) {
            Log.e("NEW_TOKEN", "ici la la la mgh");
            final String title = remoteMessage.getData().get("title");
            final String body = remoteMessage.getData().get("message");
            String click_action = remoteMessage.getNotification().getClickAction();
            // sendBroacast(title,body);
            Notifications notifcation = insertInsqlite(title, body);
            if (notifcation != null) {
                createNotification(title, body, click_action, notifcation);
                sendBroacast(title, body);
            }

        }

    }

    public void sendBroacast(String title, String body) {
        Intent intent = new Intent("com.edis.eschool_TARGET_NOTIFICATION");
        intent.putExtra("title", title);
        intent.putExtra("body", body);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.sendBroadcast(intent);
    }

    /**
     * display the notification
     * @param
     */
    /**
     * fonction qui permet l'insertion des notification dans la sqlite
     *
     * @param title
     * @param message
     */

//  format du message TYPE: contenu:dateServeur
    public Notifications insertInsqlite(String title, String message) {
        String[] tablemessage = message.split(":");
        if (tablemessage.length >= 3) {
            int imagenotification = 0;
            if (tablemessage[0].equals("absence")) {
                imagenotification = 0;
            }
            NotificationDao dao = new NotificationDao(context);
            Notifications notification = dao.insert(title, tablemessage[tablemessage.length - 2], imagenotification, tablemessage[0], tablemessage[tablemessage.length - 1], 0);
            return notification;


        } else {
            @SuppressLint("SimpleDateFormat")
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            NotificationDao dao = new NotificationDao(context);
            Notifications notification = dao.insert(title, message, 0, "generale", date, 0);
            return notification;

        }
    }


    public void createNotification(String title, String message, String click_action, Notifications notification) {
        /**Creates an explicit intent for an Activity in your app**/
        // Intent resultIntent = new Intent(this , Notification.class);

        Intent resultIntent = null;
        if (click_action != null && click_action.equals("com.edis.eschool_TARGET_NOTIFICATION")) {
            resultIntent = new Intent(this, DetailNotification.class);
            /////// resultIntent=new Intent(click_action);
            resultIntent.putExtra("notifications", notification);

        } else {
            resultIntent = new Intent(this, DetailNotification.class);
            ///resultIntent=new Intent("com.edis.eschool_TARGET_NOTIFICATION");
            resultIntent.putExtra("notifications", notification);
            /////     resultIntent=new Intent(this,DetailNotification.class);
        }

        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("body", message);
        resultIntent.putExtras(bundle);

        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(false)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setSmallIcon(R.drawable.common_full_open_on_phone)
                .setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
    }


}
