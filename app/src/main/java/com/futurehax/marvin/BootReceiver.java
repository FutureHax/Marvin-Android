package com.futurehax.marvin;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.futurehax.marvin.service.HeartBeatService;
import com.futurehax.marvin.service.UpdaterService;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            context.startService(new Intent(context, UpdaterService.class));
            HeartBeatService.sendHeartBeat(context);
        } else {
            String url = "http://marvin.boldlygoingnowhere.org/marvin_android/version.txt";
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription("Some descrition");
            request.setTitle("Some title");
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "v.txt");

            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        }
    }
}