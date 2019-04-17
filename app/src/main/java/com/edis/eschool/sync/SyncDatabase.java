package com.edis.eschool.sync;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Normallement utiliser pour liste les method de synchronisation et les appeler dans SyncAdapter
 * Desormais deplacer dans SyncAdapter
 */
public class SyncDatabase {

    public void donwloadData() {
        String url = "";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response;
        String jsonData;
        jsonData = null;
        try {
            response = client.newCall(request).execute();
            jsonData = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
