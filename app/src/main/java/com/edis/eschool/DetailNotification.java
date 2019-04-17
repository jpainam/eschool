package com.edis.eschool;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.edis.eschool.pojo.Notifications;

public class DetailNotification extends AppCompatActivity {
    TextView message, title,date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_notification);
        Intent intent=this.getIntent();
        /*getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        */

        Notifications notifications = (Notifications)intent.getSerializableExtra("notifications");
        message=(TextView)findViewById(R.id.message);
        title=(TextView)findViewById(R.id.title);
        date=(TextView)findViewById(R.id.date);

        message.setText(notifications.getMessage());
        title.setText(notifications.getTitre());
        date.setText(notifications.getDate());


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_out_right ,android.R.anim.slide_in_left);//transition simple
        finish();
      }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                overridePendingTransition(android.R.anim.slide_out_right ,android.R.anim.slide_in_left);//transition simple
                finish();
                break;
         }
        return super.onOptionsItemSelected(item);
       }
}
