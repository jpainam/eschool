package com.edis.eschool;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.edis.eschool.dummy.DummyContent;
import com.edis.eschool.sql.Database;
import com.edis.eschool.student.StudentFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements StudentFragment.OnListFragmentInteractionListener {

    //private TextView mTextMessage;

    private LinearLayout mainContentView;

    Database myDb;
    public static final int MULTIPLE_PERMISSIONS = 10;
    String[] permissions= new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    //mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    //.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //LocalBroadcastManager.getInstance(this).registerReceiver(mHandler,new IntentFilter("com.example.sadjang.myshool_FCM-MESSAGE"));
        if (checkPermissions()) {
            //  permissions  granted.
            myDb = new Database(this);
        }
        myDb = new Database(this);

        //if (myDb.getAllVisite().getCount() > 0) {

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        //} else {
            /*mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
            mDotLayout = (LinearLayout) findViewById(R.id.dotsLayout);
            mNextBtn = (Button) findViewById(R.id.nextBtn);
            mBackBtn = (Button) findViewById(R.id.prevBtn);


            slideAdapter = new PresentationAdapter(this);
            mSlideViewPager.setAdapter(slideAdapter);
            addDotsIndicator(0);
            mSlideViewPager.addOnPageChangeListener(viewListener);
            mNextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mNextBtn.getText().equals("Finish")) {
                        //  inscription dans la bd
                        myDb.isertVisite(1, getApplicationContext());
                        partir();
                    }
                    mSlideViewPager.setCurrentItem(mCurrentPage + 1);
                }
            });

            mBackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSlideViewPager.setCurrentItem(mCurrentPage - 1);
                }
            });*/
            setContentView(R.layout.activity_main);

            //mTextMessage = (TextView) findViewById(R.id.message);
            mainContentView = (LinearLayout) findViewById(R.id.main_container);
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().add(R.id.main_container,
                        new StudentFragment()).commit();
            }
            BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //}
    }

        private  boolean checkPermissions() {
            int result;
            List<String> listPermissionsNeeded = new ArrayList<>();
            for (String p:permissions) {
                result = ContextCompat.checkSelfPermission(this,p);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(p);
                }
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS );
                return false;
            }
            return true;
        }
    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
