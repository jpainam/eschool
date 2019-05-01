package com.edis.eschool.sync;

import android.accounts.Account;
import android.app.AlertDialog;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.edis.eschool.R;
import com.edis.eschool.pojo.Student;
import com.edis.eschool.student.StudentDao;
import com.edis.eschool.utils.Constante;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;

import dmax.dialog.SpotsDialog;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = "SyncAdapter";
    AlertDialog dialog = null;
    ContentResolver contentResolver;


    public SyncAdapter(Context context, boolean autoInitialize) {

        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        contentResolver = context.getContentResolver();
        dialog = new SpotsDialog.Builder()
                .setContext(getContext()).setCancelable(true)
                .setMessage(getContext().getString(R.string.loading_message))
                .build();
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(Context context, boolean autoInitialize,
                       boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        contentResolver = context.getContentResolver();
        dialog = new SpotsDialog.Builder()
                .setContext(getContext()).setCancelable(true)
                .setMessage(getContext().getString(R.string.loading_message))
                .build();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Intent intent = new Intent();
        intent.setAction(getContext().getString(R.string.SYNC_STATUS_ACTION));
        intent.putExtra(getContext().getString(R.string.sync_status), getContext().getString(R.string.sync_running));
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);


        Log.i(TAG, "Beginning network synchronization");
        /**
         * Get the last sync time used in the database to return all data
         * create after last sync time. Fetch from the local database
         */

        String jsonData = donwloadData();
        updateLocalDatabase(jsonData);
        Log.i(TAG, "Streaming completed");
        /**
         * Send a broadcast to update list view
         */
        intent = new Intent();
        intent.setAction(getContext().getString(R.string.refresh_list_broadcast));
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
        intent = new Intent();
        intent.setAction(getContext().getString(R.string.SYNC_STATUS_ACTION));
        intent.putExtra(getContext().getString(R.string.sync_status), getContext().getString(R.string.sync_finished));
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);


    }



    public String donwloadData() {
        SharedPreferences pref = getContext().getSharedPreferences(
                getContext().getString(R.string.shared_preference_file),
                Context.MODE_PRIVATE);
        String phone_number = pref.getString(
                getContext().getString(R.string.phone_number), null);
        String lastSyncTime = pref.getString(
                getContext().getString(R.string.last_time_sync), "");
        String mToken = pref.getString(
                getContext().getString(R.string.firebase_token), null);

        String url = getContext().getString(R.string.sync_url);

        Log.w(TAG, mToken);
        Log.i(TAG, "Last time Sync " + lastSyncTime + " from " + url + " " + phone_number);
        OkHttpClient client = new OkHttpClient();
        RequestBody body =new FormBody.Builder()
                .add("phone_number", phone_number)
                .add("last_time_sync", lastSyncTime)
                .add("token", mToken)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response;
        String jsonData = null;
        try {
            response = client.newCall(request).execute();
            jsonData = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

        return jsonData;
    }

    public void updateLocalDatabase(String jsonData) {
        if (jsonData != null) {
            if (jsonData != "404") {
                try {
                    JSONObject data = new JSONObject(jsonData);
                    boolean success = data.getBoolean("success");
                    if (success) {
                        Log.i(TAG, "Updating Local Database");
                        JSONArray studentData = data.getJSONArray("students");
                        syncStudent(studentData);
                    }
                    SharedPreferences pref = getContext().getSharedPreferences(
                            getContext().getString(R.string.shared_preference_file),
                            Context.MODE_PRIVATE);
                    /**
                     * Update last time sync
                     */
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentTime = sdf.format(new Date());
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(getContext().getString(R.string.last_time_sync), currentTime);
                    editor.apply();
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    Log.e(TAG, ex.getMessage());
                }
            }
        }
    }

    private void syncStudent(JSONArray data) throws JSONException {
        StudentDao dao = new StudentDao(getContext());
        if (data.length() > 0) {
            dao.emptyTable();
        }
        for (int i = 0; i < data.length(); i++) {
            Log.i(TAG, "Syncing Student");
            JSONObject item = data.getJSONObject(i);
            int id = Integer.parseInt(item.getString("id"));
            String firstname = item.getString("firstname");
            String lastname = item.getString("lastname");
            String sexe = item.getString("sex");
            String etablissement = item.getString("etablissement");
            String classe = item.getString("classe");
            String photo = item.getString("photo");
            if (photo == "" || photo == null) {
                if (sexe.equals("M")) {
                    photo = Constante.MALE_AVATAR;
                } else {
                    photo = Constante.FEMALE_AVATAR;
                }
            }
            final Student st = new Student(id, firstname, lastname, sexe,
                    classe, etablissement);
            final String path = Constante.SERVER + "eschool" + "/" + Constante.IMG_DIR
                    + "/" + photo;
            Log.i(TAG, path);
            st.setPhoto(path);
            dao.insert(st);
            /**
             * Download the student image
             */

            //String filename = path.substring(path.lastIndexOf("/")+  1);
        }
    }
}
