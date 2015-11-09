package com.futurehax.marvin;

import android.content.Context;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.MacAddress;
import com.futurehax.marvin.api.GetAllRoomsTask;
import com.futurehax.marvin.models.BeaconIdentifier;
import com.futurehax.marvin.models.UberRoom;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by FutureHax on 10/27/15.
 */
public class UberRoomManager {

    private static UberRoomManager instance;
    Context mContext;

    private volatile ArrayList<UberRoom> rooms;

    PreferencesProvider mPreferencesProvider;

    private UberRoomManager(Context context) {
        mContext = context.getApplicationContext();
        mPreferencesProvider = new PreferencesProvider(mContext);
        rooms = new ArrayList<>();


        new GetAllRoomsTask(mContext, roomsListener).execute();
    }

    GetAllRoomsTask.IGetAllRoomsTask roomsListener = new GetAllRoomsTask.IGetAllRoomsTask() {
        @Override
        public void onRoomsFetched(ArrayList<UberRoom> rooms) {
            if (rooms != null) {
                UberRoomManager.this.rooms = rooms;
                UberBeaconManager.getInstance(mContext).handleBeaconStartup();
            }
        }
    };

    public synchronized static UberRoomManager getInstance(Context context) {
        if (instance == null) {
            instance = new UberRoomManager(context);
        }
        return instance;
    }


    public ArrayList<UberRoom> getRooms() {
        return rooms;
    }

    public UberRoom getById(Beacon b) {
        UberRoom res = getByMac(b.getMacAddress());
        if (res == null) {
            res = getByUUID(b.getProximityUUID());
        }
        return res;
    }

    private UberRoom getByMac(MacAddress macAddress) {
        for (UberRoom room : rooms) {
            for (BeaconIdentifier id : room.beacons) {
                MacAddress roomMac = MacAddress.fromString(id.mac);
                if (macAddress.toStandardString().equals(roomMac.toStandardString())) {
                    return room;
                }
            }
        }
        return null;
    }

    private UberRoom getByUUID(UUID uuid) {
        for (UberRoom room : rooms) {
            for (BeaconIdentifier id : room.beacons) {
                if (uuid.equals(UUID.fromString(id.uuid))) {
                    return room;
                }
            }
        }

        return null;
    }

    public UberRoom get(String identifier) {
        for (UberRoom room : rooms) {
            if (room.roomName.equals(identifier)) {
                return room;
            }
        }

        return null;
    }
}
