package com.futurehax.marvin.google_api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.futurehax.marvin.manager.PreferencesProvider;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by FutureHax on 10/25/15.
 */
public class GoogleApiProvider {
    private static final String TAG = GoogleApiProvider.class.getSimpleName();

    private static GoogleApiProvider sProvider = null;
    private final Context mContext;
    GoogleApiClient mGoogleApiClient;
    Activity mCurrentConnectedActivity;

    private static final int RC_SIGN_IN = 0;
    ILoginAttempt mLoginAttemptListener;

    public GoogleApiProvider(Context context) {
        mContext = context;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("507867051475-tq3ea5pbndst088ckbfunvjq0hjgff8o.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

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


    public synchronized void updateStoredProfileInfo(GoogleSignInResult result) {
        new BackgroundTokenFetcher(result).execute();
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
                mLoginAttemptListener.onResult(false);
            }

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            updateStoredProfileInfo(result);
        }
    }

    public void signIn(Activity connectedActivity, ILoginAttempt loginAttemptListener) {
        mCurrentConnectedActivity = connectedActivity;
        mLoginAttemptListener = loginAttemptListener;
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        mCurrentConnectedActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    class BackgroundTokenFetcher extends AsyncTask<Void, Void, Boolean> {
        GoogleSignInResult result;

        public BackgroundTokenFetcher(GoogleSignInResult result) {
            this.result = result;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mGoogleApiClient.disconnect();
            mLoginAttemptListener.onResult(success);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (result.isSuccess()) {
                PreferencesProvider p = new PreferencesProvider(mCurrentConnectedActivity);
                GoogleSignInAccount currentPerson = result.getSignInAccount();

                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getPhotoUrl().toString();
                String email = currentPerson.getEmail();
                p.setProfile(personName,
                        currentPerson.getId(),
                        email,
                        personPhotoUrl);
                p.setGoogleAuthToken(currentPerson.getIdToken());
            }
            return result.isSuccess();
        }
    }
}
