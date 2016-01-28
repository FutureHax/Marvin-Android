package com.futurehax.marvin.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.futurehax.marvin.manager.PreferencesProvider;
import com.futurehax.marvin.R;
import com.futurehax.marvin.api.UrlGenerator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UpdaterService extends IntentService {
    PreferencesProvider mPrefs;
    static boolean started = false;

    public UpdaterService() {
        super("UpdaterService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mPrefs = new PreferencesProvider(this);

        UrlGenerator urlGenerator = new UrlGenerator(UpdaterService.this);
        try {
            String attempt = urlGenerator.getBlockingRequestWithJson(urlGenerator.generate(UrlGenerator.VERSION));
            if (attempt != null) {
                handleResponse(new JsonParser().parse(attempt).getAsJsonObject());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleResponse(JsonObject result) {
        int versionCode = result.get("version_code").getAsInt();
        int currentVersionCode = getCurrentVersionCode();
        boolean updating = currentVersionCode < versionCode;

        Log.d("UPDATER", "Current = " + currentVersionCode + ", Latest = " + versionCode + ". " + (updating ? "Prompting update." : "Nothing to do."));
        if (updating) {
            notifyUpdate(result.get("url").getAsString(), versionCode);
        }
    }

    private void notifyUpdate(String url, int nVersion) {
        Intent intent = new Intent(this, DownloadUpdateService.class);
        intent.putExtra(DownloadUpdateService.EXTRA_URL, url);
        PendingIntent pIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification n = new NotificationCompat.Builder(this)
                .setContentTitle("Marvin Update Available")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentText("Update to v" + Integer.toString(nVersion))
                .setContentIntent(pIntent).build();
        n.defaults = Notification.DEFAULT_ALL;


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);
    }

    public int getCurrentVersionCode() {
        int currentVersionCode = 0;

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException var4) {

        }

        return currentVersionCode;
    }

    public static void startService(Context context) {
        Intent i = new Intent(context, UpdaterService.class);
        context.startService(i);
    }
}
