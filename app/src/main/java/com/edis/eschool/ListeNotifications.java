package com.edis.eschool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.edis.eschool.MyAdapter.NotificationsAdapteur;
import com.edis.eschool.notification.NotificationDao;
import com.edis.eschool.pojo.Notifications;
import com.edis.eschool.sql.Database;
import com.edis.eschool.sql.DatabaseHelper;

public class ListeNotifications extends Fragment {
    RecyclerView myrv;
    NotificationsAdapteur myAdapter;
    Context context;
    int lastId = 0;
    private final String ACTION_RECEIVE_NOTIFICATION = "com.edis.eschool_TARGET_NOTIFICATION";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_liste_notifications, container, false);
        //((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        //((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LocalBroadcastManager.getInstance(container.getContext()).registerReceiver(mHandler,
                new IntentFilter(ACTION_RECEIVE_NOTIFICATION));
        context = container.getContext();
        myrv = (RecyclerView) view.findViewById(R.id.recycleviewnotification);
        myrv.setLayoutManager(new LinearLayoutManager(context));
        myAdapter = new NotificationsAdapteur(container.getContext());
        myrv.setAdapter(myAdapter);
        initNotification();
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder target, int i) {
                int position = target.getAdapterPosition();
                myAdapter.removeNotification(position);
            }
        });
        helper.attachToRecyclerView(myrv);

        final EditText seachnotification = (EditText) view.findViewById(R.id.seachnotification);
        seachnotification.setFocusable(false);
        seachnotification.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                seachnotification.setFocusableInTouchMode(true);

                return false;
            }
        });
        seachnotification.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filterNotification(s.toString());
            }
        });
        return view;
    }

    private void filterNotification(String texte) {
        myAdapter.serchNotifi(texte);
    }

    private void initNotification() {
        NotificationDao myDb = new NotificationDao(context);

        Cursor res = myDb.getAllNotification();
        while (res.moveToNext()) {
            addNotification(res, 100);
        }


    }

    public void addNotification(Cursor res, int n) {

        Notifications notifications = new Notifications();
        int idnotification = res.getInt(0);
        notifications.setIdnotification(idnotification);
        if (lastId < idnotification) {
            lastId = idnotification;
        }
        notifications.setTitre(res.getString(1));
        String s = res.getString(2);
        String upToNCharacters = s.substring(0, Math.min(s.length(), n));
        notifications.setMessage(upToNCharacters + " ...");
        notifications.setImage(res.getInt(3));
        notifications.setType(res.getString(4));
        notifications.setDate(res.getString(5));
        notifications.setLu(res.getInt(6));

        myAdapter.add(notifications);
    }


    private BroadcastReceiver mHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_RECEIVE_NOTIFICATION)) {
                NotificationDao dao = new NotificationDao(context);
                Cursor res = dao.getAllNewNotification(lastId);
                while (res.moveToNext()) {
                    addNotification(res, 100);
                }
            }

        }
    };


   /*public void back(){
       Intent detailNotifi= new Intent(context, MainActivity.class);
       startActivity(detailNotifi);
       overridePendingTransition(android.R.anim.slide_out_right ,android.R.anim.slide_in_left);//transition simple
       finish();
   }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                back();
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/


}
