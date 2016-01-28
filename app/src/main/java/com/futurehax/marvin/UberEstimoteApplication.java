package com.futurehax.marvin;

import android.app.Application;
import android.provider.MediaStore;

import com.estimote.sdk.EstimoteSDK;
import com.futurehax.marvin.api.SaveRoomTask;
import com.futurehax.marvin.manager.UberBeaconManager;
import com.futurehax.marvin.manager.UberRoomManager;
import com.futurehax.marvin.models.BeaconMap;
import com.futurehax.marvin.models.UberRoom;
import com.futurehax.marvin.service.AccelerometerListenerService;

/**
 * Created by FutureHax on 10/14/15.
 */
public class UberEstimoteApplication extends Application {

    UberBeaconManager beaconManager;

    PhotosObserver mObserver;

    public void onCreate() {
        super.onCreate();
        EstimoteSDK.initialize(this, "ken-kyger-s-your-own-app-ic1", "5bbb89d318c367bf087f7846e7de761d");

        beaconManager = UberBeaconManager.getInstance(this);

        AccelerometerListenerService.start(this);

        UberRoomManager.getInstance(this);
        for (UberRoom room : BeaconMap.getHardcodedMap()) {
            new SaveRoomTask(this, room).execute();
        }
    }

    public void startupListener() {
        mObserver = new PhotosObserver(this);
        mObserver.searchCurrent();
        getContentResolver()
                .registerContentObserver(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false,
                        mObserver);
    }

    public void stopListener() {
        getContentResolver()
                .unregisterContentObserver(mObserver);
    }
}

