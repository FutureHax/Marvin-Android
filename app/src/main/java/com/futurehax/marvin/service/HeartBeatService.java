package com.futurehax.marvin.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.futurehax.marvin.manager.PreferencesProvider;
import com.futurehax.marvin.manager.UberBeaconManager;
import com.futurehax.marvin.api.UrlGenerator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class HeartBeatService extends IntentService {

    public HeartBeatService() {
        super("HeartBeatService");
    }

    JSONObject data;

    public static void sendHeartBeat(Context context) {
        Intent i = new Intent(context, HeartBeatService.class);
        context.startService(i);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        PreferencesProvider p = new PreferencesProvider(this);
        if (p.getName() == null || p.getToken() == null) {
            return;
        }

        Intent i = new Intent(this, UpdateWidgetService.class);
        i.setAction("ACTION_HEARTBEAT");
        startService(i);

        UrlGenerator urlGenerator = new UrlGenerator(this);
        String status = Boolean.toString(UberBeaconManager.getInstance(this).getIsHome());

        data = new JSONObject();
        try {
            data.put("name", p.getName().split(" ")[0]);
            data.put("email", p.getEmail());
            data.put("profile_id", p.getId());
            data.put("gcm", p.getToken());
            data.put("status", status);
            data.put("room", p.getCurrentRoom() != null ? p.getCurrentRoom().roomName : "Unknown");
            data.put("enable_tracking", p.getHasTrackingEnabled());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            String attempt = urlGenerator.getBlockingRequestWithJson(urlGenerator.generate(UrlGenerator.HEARTBEAT), data);
            Log.d("HEARTBEAT", attempt == null ? "Failed" : attempt + " : " + data.toString(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
