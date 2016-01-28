package com.futurehax.marvin.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ViewFlipper;

import com.futurehax.marvin.manager.PreferencesProvider;
import com.futurehax.marvin.R;
import com.futurehax.marvin.api.AuthenticateTask;
import com.futurehax.marvin.google_api.GoogleApiProvider;
import com.futurehax.marvin.google_api.ILoginAttempt;
import com.futurehax.marvin.service.RegistrationIntentService;
import com.google.android.gms.common.SignInButton;
import com.greysonparrelli.permiso.Permiso;
import com.greysonparrelli.permiso.PermisoActivity;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends PermisoActivity implements ILoginAttempt {

    private static final int REQUEST_CODE_ACCOUNT = 0;
    private static final int REQUEST_CODE_STORAGE = 1;
    // UI references.
    ViewFlipper flippy;
    private SignInButton btnSignIn;
    GoogleApiProvider mGoogleApiProvider;
    int failCount = 0;


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

        flippy = (ViewFlipper) findViewById(R.id.flippy);

        flippy.setDisplayedChild(1);
        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        btnSignIn.setOnClickListener(signinListener);

        mGoogleApiProvider = GoogleApiProvider.getInstance(this);

        Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
                                                     @Override
                                                     public void onPermissionResult(Permiso.ResultSet resultSet) {
                                                         if (!resultSet.areAllPermissionsGranted()) {
                                                             showUnable();
                                                         } else {
                                                             startService(new Intent(LoginActivity.this, RegistrationIntentService.class));
                                                         }
                                                     }

                                                     @Override
                                                     public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                                                         Permiso.getInstance().showRationaleInDialog("Requested Permissions",
                                                                 getString(R.string.permission_rationale), null, callback);
                                                     }
                                                 }, Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION);

    }

    protected void onStart() {
        super.onStart();
        if (new PreferencesProvider(this).getTokenIsSent()) {
            mGoogleApiProvider.signIn(LoginActivity.this, LoginActivity.this);
        } else {
            flippy.setDisplayedChild(0);
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

    BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RegistrationIntentService.REGISTRATION_COMPLETE)) {
//                mGoogleApiProvider.signIn(LoginActivity.this, LoginActivity.this);
            } else {
                showUnable();
            }
        }
    };

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


    private void showUnable() {
        failCount = failCount + 1;
        Snackbar.make(btnSignIn, "Unable to login", Snackbar.LENGTH_LONG)
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

