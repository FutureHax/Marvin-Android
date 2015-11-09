package com.futurehax.marvin.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ViewFlipper;

import com.futurehax.marvin.PreferencesProvider;
import com.futurehax.marvin.R;
import com.futurehax.marvin.UberEstimoteApplication;
import com.futurehax.marvin.api.AuthenticateTask;
import com.futurehax.marvin.google_api.GoogleApiProvider;
import com.futurehax.marvin.google_api.ILoginAttempt;
import com.futurehax.marvin.service.RegistrationIntentService;
import com.google.android.gms.common.SignInButton;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements ILoginAttempt {

    private static final int REQUEST_CODE_ACCOUNT = 0;
    private static final int REQUEST_CODE_STORAGE = 1;
    // UI references.
    ViewFlipper flippy;
    private SignInButton btnSignIn;
    GoogleApiProvider mGoogleApiProvider;
    int failCount = 0;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_CODE_ACCOUNT) {
            handleGivenPermissions();
        } else if (requestCode == REQUEST_CODE_STORAGE) {
            requestAccessToSD();
        }
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiProvider.isConnected()) {
            mGoogleApiProvider.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        mGoogleApiProvider.onActivityResult(requestCode, responseCode, intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        flippy = (ViewFlipper) findViewById(R.id.flippy);

        flippy.setDisplayedChild(1);
        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        btnSignIn.setOnClickListener(signinListener);

        mGoogleApiProvider = GoogleApiProvider.getInstance(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{android.Manifest.permission.GET_ACCOUNTS},
                    REQUEST_CODE_ACCOUNT);
        } else {
            // permission has been granted, continue as usual
            handleGivenPermissions();
        }
    }

    private void handleGivenPermissions() {
        startService(new Intent(this, RegistrationIntentService.class));
    }

    protected void onStart() {
        super.onStart();
        if (new PreferencesProvider(this).getTokenIsSent()) {
            mGoogleApiProvider.signIn(LoginActivity.this, LoginActivity.this);
        }
    }

    OnClickListener signinListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!mGoogleApiProvider.isConnecting()) {
                mGoogleApiProvider.signIn(LoginActivity.this, LoginActivity.this);
            }
        }
    };

    BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RegistrationIntentService.REGISTRATION_COMPLETE)) {
                requestAccessToSD();
            } else {
                showUnable();
            }
        }
    };

    private void requestAccessToSD() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_STORAGE);
        } else {
            ((UberEstimoteApplication) getApplication()).startupListener();
            flippy.setDisplayedChild(0);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(RegistrationIntentService.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(RegistrationIntentService.REGISTRATION_FAILED));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onResult(boolean success) {
        if (success) {
            if (new PreferencesProvider(this).getHost() == null) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                new AuthenticateTask(this, flippy).execute();
            }
        } else {
           showUnable();
        }
    }

    private void showUnable() {
        failCount = failCount + 1;
        Snackbar.make(findViewById(android.R.id.content), "Unable to login", Snackbar.LENGTH_LONG)
                .setAction(failCount < 3 ? "Retry" : "Skip", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (failCount < 3) {
                            mGoogleApiProvider.signIn(LoginActivity.this, LoginActivity.this);
                        } else {
                            startActivity(new Intent(v.getContext(), MainActivity.class));
                            finish();
                        }
                    }
                }).show();
    }
}

