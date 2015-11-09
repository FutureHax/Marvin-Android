package com.futurehax.marvin.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;

import com.futurehax.marvin.activities.MainActivity;
import com.futurehax.marvin.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DownloadUpdateService extends IntentService {

    public static final String EXTRA_URL = "com.futurehax.marvin.extra.URL";


    public DownloadUpdateService() {
        super("DownloadUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            handleAction(intent.getStringExtra(EXTRA_URL));
        }
    }


    private void handleAction(String iurl) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Marvin Update")
                .setAutoCancel(false)
                .setProgress(0, 0, true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1 /* ID of notification */, notificationBuilder.build());

        try {
            if (!iurl.contains("http")) {
                iurl = "http://" + iurl;
            }
            URL url = new URL(iurl);
            String fileName = iurl.split("/")[iurl.split("/").length - 1];
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();

            String PATH = Environment.getExternalStorageDirectory() + "/download/";
            File file = new File(PATH);
            file.mkdirs();
            File outputFile = new File(file, fileName);
            FileOutputStream fos = new FileOutputStream(outputFile);

            InputStream is = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
            fos.close();
            is.close();

            Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                    .setDataAndType(Uri.fromFile(new File(PATH + fileName)), "application/vnd.android.package-archive");
            promptInstall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(promptInstall);

        } catch (IOException e) {
            e.printStackTrace();
        }

        notificationManager.cancel(1);
    }
}
