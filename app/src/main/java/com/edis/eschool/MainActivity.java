package com.edis.eschool;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.edis.eschool.notification.ListeNotifications;
import com.edis.eschool.pojo.Student;
import com.edis.eschool.sql.DatabaseHelper;
import com.edis.eschool.utils.Constante;
import com.edis.eschool.student.StudentFragment;
import com.facebook.drawee.backends.pipeline.Fresco;


public class MainActivity extends AppCompatActivity implements StudentFragment.OnListFragmentInteractionListener {
    private static final String TAG = "MainActivity";
    // Instance fields
    Account mAccount;
    DatabaseHelper databaseHelper;
    //final Fragment fragment1 = new HomeFragment();
    final Fragment fragment1 = new StudentFragment();
    final Fragment fragment2 = new DashboardFragment();
    //final Fragment fragment3 = new NotificationsFragment();
    final Fragment fragment3 = new ListeNotifications();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;
    public int active_fragment = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * If first run, manually sync the local database with the remote database
         */
        SharedPreferences pref = getApplicationContext().getSharedPreferences(
                getString(R.string.shared_preference_file), Context.MODE_PRIVATE);

        //if (!pref.contains(getString(R.string.visite))) {
            // TODO : Open the tuto page
        //}else
        if (!pref.contains(getString(R.string.phone_number))) {
            // User not authenticated
            Log.i(TAG, "Start Login Activity - First time run");
            startLoginActivity();
        }else {
            Fresco.initialize(this);
            databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
            Log.i(TAG, "Getting database Instance");
            /**
             * First run, force manual sync
             */
            if (!pref.contains(getString(R.string.last_time_sync))) {
                mAccount = CreateSyncAccount(this);
                manualSynch();
            }
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

            fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();
            fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit();
            fm.beginTransaction().add(R.id.main_container, fragment1, "1").commit();
        }

    }

    /**
     * Create a new edis account for the sync adapter
     *
     * @param context The application context
     */
    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                Constante.ACCOUNT, Constante.ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        ContentResolver.setSyncAutomatically(newAccount, Constante.AUTHORITY, true);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            if(ContentResolver.getIsSyncable(newAccount, Constante.AUTHORITY) == 0) {
                ContentResolver.setIsSyncable(newAccount, Constante.AUTHORITY, 1);
            }
            ContentResolver.setSyncAutomatically(newAccount,
                    Constante.AUTHORITY, false);
            ContentResolver.addPeriodicSync(newAccount, Constante.AUTHORITY,
                    new Bundle(), 86400 ); // 24hours = 86 400 seconds
            Log.i(TAG,
                    "Account " + Constante.ACCOUNT + " " + Constante.ACCOUNT_TYPE + " created");
            return newAccount;
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
            Log.i(TAG, "Account already exists");
            return newAccount;
        }
    }

    private void manualSynch() {
        /** Pass the settings flags by inserting them in a bundle */
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        ContentResolver.requestSync(mAccount, Constante.AUTHORITY, settingsBundle);
        Log.i(TAG, "Manual Sync Finish");
    }

    private void startLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    active_fragment = 1;
                    return true;

                case R.id.navigation_dashboard:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    active_fragment = 2;
                    return true;

                case R.id.navigation_notifications:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    active_fragment = 3;
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("active_fragment", active_fragment);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        active_fragment = savedInstanceState.getInt("active_fragment");
        if(active_fragment == 2){
            active = fragment2;
        }else if(active_fragment == 3){
            active = fragment3;
        }else{
            active = fragment1;
        }
        fm.beginTransaction().replace(R.id.main_container, active).commit();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        active.onActivityResult(requestCode, resultCode, data);
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    @Override
    public void onListFragmentInteraction(Student item) {
        Log.i(TAG, item.getFirstName() + " " + item.getLastName());
        Intent intent = new Intent(getApplicationContext(), InnerMenuActivity.class);
        intent.putExtra("student", item);
        startActivity(intent);
        //finish();
    }
}
