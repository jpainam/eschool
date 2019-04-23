package com.edis.eschool;


import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.edis.eschool.notification.NotificationDao;
import com.edis.eschool.pojo.Notifications;
import com.edis.eschool.sql.DatabaseHelper;
import com.edis.eschool.utils.Constante;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


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
            sendRegistrationToServer(refreshedToken, pref.getString(
                    getString(R.string.phone_number), null));
        }
    }

    private void sendRegistrationToServer(String token, String phone_number){
        if(token == null){
            Log.w(TAG, "Ask for a new Token");
            FirebaseApp.initializeApp(this);
            InstanceIdResult iit = FirebaseInstanceId.getInstance().getInstanceId().
                    addOnSuccessListener( new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    final String newToken = instanceIdResult.getToken();
                    // update pref
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(
                            getString(R.string.shared_preference_file), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(getString(R.string.firebase_token), newToken);
                    editor.apply();
                }
            }).getResult();
            token = iit.getToken();
        }
        Log.i(TAG, token);
        AsyncTask<String, Void, Boolean> asyncTask = new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                String mToken = "";
                if(params.length > 0) {
                    mToken = params[0];
                }
                String phone = "";
                if(params.length > 1 && params[1] != null) {
                    phone = params[1];
                }
                String url = Constante.SERVER_PATH + "update_token.php";
                OkHttpClient client =new OkHttpClient();
                Log.e(TAG, url);
                RequestBody body =new FormBody.Builder()
                        .add("phone_number", phone)
                        .add("token", mToken)
                        .build();
                Request newReq=new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                try {
                    Response response = client.newCall(newReq).execute();
                    String jsonData = response.body().string();
                    Log.e(TAG, jsonData);
                }catch (Exception ex){
                    ex.printStackTrace();
                    Log.e(TAG, ex.getMessage());
                }
                return  true;
            }

        };
        asyncTask.execute(token, phone_number);
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        this.context = this;
        myDb = DatabaseHelper.getInstance(this);
        Log.e("NEW_TOKEN", "reception de la notification");
        if (remoteMessage.getData()!=null){
            if (remoteMessage.getData().size() > 0) {
                Log.e("NEW_TOKEN", "reception de la notification not null");
                Map<String, String> data = remoteMessage.getData();
                final String title = data.get("title");
                final String body = data.get("body");
                String typeNotif = data.get("type");
                if(typeNotif == null || typeNotif.isEmpty()){
                    typeNotif = "Unknown";
                }
                //// final String notification = remoteMessage.getData().get("notification");
                String click_action = remoteMessage.getNotification().getClickAction();
                Log.e("NEW_TOKEN title", title);
                Log.e("NEW_TOKEN body", body);
                Notifications notifcation = insertInsqlite(title, body, typeNotif) ;
                if (notifcation != null) {
                    createNotification(title, body, click_action, notifcation);
                    sendBroacast(title, body);
                }
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
    public Notifications insertInsqlite(String title, String message,String type) {
        NotificationDao dao = new NotificationDao(context);
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        Notifications notification = dao.insert(title, message, type, date, 0);
        return notification;
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
        mBuilder.setSmallIcon(R.drawable.common_full_open_on_phone);
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
