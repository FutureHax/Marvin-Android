package com.futurehax.marvin;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import com.futurehax.marvin.api.ToggleRoomTask;

public class MyWidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals("android.appwidget.action.APPWIDGET_CLICK")) {
            PreferencesProvider p = new PreferencesProvider(context);
            new ToggleRoomTask(context, p.getCurrentRoom()).execute();
        }
    }
} 