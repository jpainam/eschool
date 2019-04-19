package com.edis.eschool.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.edis.eschool.pojo.Student;
import com.edis.eschool.student.StudentDao;
import com.edis.eschool.utils.Constante;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = "SyncAdapter";

    ContentResolver contentResolver;


    public SyncAdapter(Context context, boolean autoInitialize) {

        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        contentResolver = context.getContentResolver();
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
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.i(TAG, "Beginning network synchronization");
        /**
         * Get the last sync time used in the database to return all data
         * create after last sync time. Fetch from the local database
         */
        String lastSyncTime = "2014-12-12";
        String url = Constante.SERVER_PATH + "sync.php?lastSyncTime=" + lastSyncTime;
        Log.i(TAG, "Last time Sync " + lastSyncTime + " from " + url);
        String jsonData = donwloadData(url);
        updateLocalDatabase(jsonData);
        Log.i(TAG, "Streaming completed");
    }

    public String donwloadData(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
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
                    if(success) {
                        Log.i(TAG, "Updating Local Database");
                        JSONArray studentData = data.getJSONArray("students");
                        syncStudent(studentData);
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    Log.e(TAG, ex.getMessage());
                }
            }
        }
    }

    private void syncStudent(JSONArray data) throws JSONException {

        for (int i = 0; i < data.length(); i++) {
            Log.i(TAG, "Syncing Student");
            JSONObject item = data.getJSONObject(i);
            StudentDao dao = new StudentDao(getContext());
            int id = Integer.parseInt(item.getString("id"));
            String firstname = item.getString("firstname");
            String lastname = item.getString("lastname");
            String sexe = item.getString("sex");
            String etablissement = item.getString("etablissement");
            String classe = item.getString("classe");
            String photo = item.getString("photo");
            if(photo == "" || photo == null){
                if(sexe.equals("M")){
                    photo = Constante.MALE_AVATAR;
                }else{
                    photo = Constante.FEMALE_AVATAR;
                }
            }
            final Student st = new Student(id, firstname, lastname, sexe,
                    classe, etablissement);
            final String path = Constante.SERVER +  "eschool" + "/" + Constante.IMG_DIR
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
