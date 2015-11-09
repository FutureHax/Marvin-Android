package com.futurehax.marvin.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.futurehax.marvin.AccelerometerListener;
import com.futurehax.marvin.AccelerometerManager;
import com.futurehax.marvin.PreferencesProvider;

public class AccelerometerListenerService extends Service implements AccelerometerListener {
    private static final float TOLERANCE = 4.0f;
    float[] lastReadings;
    PreferencesProvider mPreferences;
    public AccelerometerListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        lastReadings = new float[3];
        mPreferences = new PreferencesProvider(this);
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
    }

    @Override
    public void onAccelerationChanged(float x, float y, float z) {
        if (Math.abs(x + y + z -
                lastReadings[0] - lastReadings[1] - lastReadings[2]) > TOLERANCE) {
            lastReadings = new float[] {x, y, z};
            handleAccelUpdate();
        }
    }

    Handler handy = new Handler();
    Runnable resetMovedRunnable = new Runnable() {
        @Override
        public void run() {
            mPreferences.setHasMoved(false);
        }
    };

    private void handleAccelUpdate() {
        mPreferences.setHasMoved(true);
        handy.removeCallbacks(resetMovedRunnable);
        handy.postDelayed(resetMovedRunnable, 1000);
    }

    public static void start(Context ctx) {
        ctx.startService(new Intent(ctx, AccelerometerListenerService.class));
    }
}
