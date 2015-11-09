package com.futurehax.marvin.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.futurehax.marvin.MyWidgetProvider;
import com.futurehax.marvin.PreferencesProvider;
import com.futurehax.marvin.R;
import com.futurehax.marvin.UberBeaconManager;
import com.futurehax.marvin.activities.LoginActivity;

public class UpdateWidgetService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        PreferencesProvider p = new PreferencesProvider(this);
        boolean isHome = UberBeaconManager.getInstance(this).getIsHome();

        ComponentName thisWidget = new ComponentName(this.getApplicationContext(),
                MyWidgetProvider.class);

        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(this.getPackageName(),
                    R.layout.widget_layout);
            remoteViews.setTextViewText(R.id.update, isHome ? p.getCurrentRoom().roomName : "Away");

            Intent i = new Intent(this.getApplicationContext(), MyWidgetProvider.class);
            i.setAction("android.appwidget.action.APPWIDGET_CLICK");
            i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(),
                    0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.layout, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
} 