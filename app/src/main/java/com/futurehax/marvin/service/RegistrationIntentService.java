package com.futurehax.marvin.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.futurehax.marvin.PreferencesProvider;
import com.futurehax.marvin.R;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by FutureHax on 10/21/15.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String REGISTRATION_FAILED = "registrationFailed";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        PreferencesProvider preferencesProvider = new PreferencesProvider(this);
        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);

//            // Subscribe to topic channels
//            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            preferencesProvider.sentTokenToServer(true);
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            preferencesProvider.sentTokenToServer(false);
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(REGISTRATION_COMPLETE));
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        new PreferencesProvider(this).storeToken(token);
    }

//    /**
//     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
//     *
//     * @param token GCM token
//     * @throws IOException if unable to reach the GCM PubSub service
//     */
//    // [START subscribe_topics]
//    private void subscribeTopics(String token) throws IOException {
//        GcmPubSub pubSub = GcmPubSub.getInstance(this);
//        for (String topic : TOPICS) {
//            pubSub.subscribe(token, "/topics/" + topic, null);
//        }
//    }
//    // [END subscribe_topics]
}
