package com.edis.eschool;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.edis.eschool.utils.Constante;

import java.io.IOException;


import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{
    private static String TAG = "LoginActivity";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    Context context;
    // UI references.
    private EditText mPhoneNumberView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mPhoneNumberView =  findViewById(R.id.phone_number);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        context = getApplicationContext();

        TextView mEmailSignInButton =  findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }
    public void showProgress(final boolean show){

    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        // Reset errors.
        mPhoneNumberView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String phone_number = mPhoneNumberView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid phone number address.
        if (TextUtils.isEmpty(phone_number)) {
            mPhoneNumberView.setError(getString(R.string.error_field_required));
            focusView = mPhoneNumberView;
            cancel = true;
        } else if (!isPhoneNumberValid(phone_number)) {
            mPhoneNumberView.setError(getString(R.string.error_invalid_email));
            focusView = mPhoneNumberView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(phone_number, password);
            mAuthTask.execute((Void) null);
        }
    }


    private boolean isPhoneNumberValid(String phone_number) {
        //TODO: Replace this with your own logic
        return phone_number.length() > Constante.PHONE_NUMBER_LENGTH;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 4;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mToken;
        private SharedPreferences pref;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
            pref = getApplicationContext().getSharedPreferences(
                    getString(R.string.shared_preference_file), Context.MODE_PRIVATE);
            mToken = pref.getString(getString(R.string.firebase_token), "");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String url = Constante.SERVER_PATH + "login.php";
            OkHttpClient client =new OkHttpClient();
            Log.e(TAG, url);
            Log.i(TAG, mEmail + " " + mPassword);
            RequestBody body =new FormBody.Builder()
                    .add("phonenumber", mEmail)
                    .add("password", mPassword)
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
                /**
                 * register the new account here and do something with the jsonData
                 */
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(getString(R.string.phone_number), mEmail);
                editor.apply();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
                if(e.getMessage().equals("timeout")){
                    Log.e(TAG, "Erreur de connextion Time Out");
                    // showDialogue("Erreur de connection\n reasayer Svp") ;
                }else{
                    Log.e(TAG, "Erreur de Connexion inconnu");
                    //  showDialogue("Erreur "+e.getMessage()) ;
                }
            }
            // TODO:
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

