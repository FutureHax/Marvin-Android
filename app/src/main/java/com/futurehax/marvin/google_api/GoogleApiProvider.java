package com.futurehax.marvin.google_api;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.futurehax.marvin.PreferencesProvider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

/**
 * Created by FutureHax on 10/25/15.
 */
public class GoogleApiProvider implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int PROFILE_PIC_SIZE = 350;

    private static GoogleApiProvider sProvider = null;
    private final Context mContext;
    GoogleApiClient mGoogleApiClient;
    Activity mCurrentConnectedActivity;

    private boolean mIntentInProgress;
    private volatile boolean mAttemptingLogin;
    private ConnectionResult mConnectionResult;
    private static final int RC_SIGN_IN = 0;
    ILoginAttempt mLoginAttemptListener;

    public GoogleApiProvider(Context context) {
        mContext = context;
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

    }

    public synchronized void disconnect() {
        mGoogleApiClient.disconnect();
        mCurrentConnectedActivity = null;
    }

    public synchronized static GoogleApiProvider getInstance(Context context) {
        if (sProvider == null) {
            sProvider = new GoogleApiProvider(context);
        }

        return sProvider;
    }


    public synchronized boolean updateStoredProfileInfo() {
        PreferencesProvider p = new PreferencesProvider(mContext);
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;
                p.setProfile(personName,
                        personGooglePlusProfile.split("/")[personGooglePlusProfile.split("/").length - 1],
                        email,
                        personPhotoUrl);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mAttemptingLogin) {
            mAttemptingLogin = false;
            updateStoredProfileInfo();
            mLoginAttemptListener.onResult(true);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), mCurrentConnectedActivity,
                    0).show();
        } else {
            if (!mIntentInProgress) {
                // Store the ConnectionResult for later usage
                mConnectionResult = connectionResult;

                if (mAttemptingLogin) {
                    // The user has already clicked 'sign-in' so we attempt to
                    // resolve all
                    // errors until the user is signed in, or they cancel.
                    resolveSignInError();
                }
            }
        }
    }

    /**
     * Method to resolve any signin errors
     */
    private void resolveSignInError() {
        if (mConnectionResult != null && mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(mCurrentConnectedActivity, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    public synchronized boolean isConnected() {
        return mGoogleApiClient.isConnected();
    }

    public boolean isConnecting() {
        return mGoogleApiClient.isConnecting();
    }

    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != Activity.RESULT_OK) {
                mAttemptingLogin = false;
                mLoginAttemptListener.onResult(false);
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    public void signIn(Activity connectedActivity, ILoginAttempt loginAttemptListener) {
        mCurrentConnectedActivity = connectedActivity;
        if (mAttemptingLogin) {
            return;
        }
        mAttemptingLogin = true;
        mLoginAttemptListener = loginAttemptListener;
        mGoogleApiClient.connect();
        mHandler.postDelayed(mErrorCheckRunnable, 3000);
    }

    Handler mHandler = new Handler();
    Runnable mErrorCheckRunnable = new Runnable() {
        @Override
        public void run() {
            if (mAttemptingLogin) {
                mAttemptingLogin = false;
                mGoogleApiClient.disconnect();
                mLoginAttemptListener.onResult(false);
            }
        }
    };
}
