package com.futurehax.marvin;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
//                HeartBeatService.forceSendHeartBeat(context);
                UberBeaconManager.getInstance(context).handleBeaconStartup();

//            } else {
//                HeartBeatService.forceSendHeartBeat(context);
            }
        }
    }
}