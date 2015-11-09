package com.futurehax.marvin;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.futurehax.marvin.models.BeaconMap;
import com.futurehax.marvin.models.UberRoom;
import com.futurehax.marvin.service.HeartBeatService;

import java.io.File;
import java.util.UUID;

/**
 * Created by FutureHax on 10/13/15.
 */
public class PreferencesProvider {

    private static final String MAC_NOT_FOUND_CONSTANT = "02:00:00:00:00:00";
    SharedPreferences prefs;
    Context ctx;

    public PreferencesProvider(Context ctx) {
        this.ctx = ctx;
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    private void setUUID(String uuid) {
        prefs.edit().putString("uuid", uuid).apply();
    }

    public String getUUID() {
        String uuid = prefs.getString("uuid", null);
        if (uuid == null) {
            setUUID(UUID.randomUUID().toString());
            uuid = getUUID();
        }
        return uuid;
    }

    public void setLast(long last) {
        prefs.edit().putLong("last", last).apply();
    }

    public long getLast() {
        return prefs.getLong("last", 0);
    }

    public String getName() {
        return prefs.getString("name", null);
    }

    public boolean getBackupEnabled() {
        return prefs.getBoolean("enable_backup", true);
    }
    public boolean getAlertsEnabled() {
        return prefs.getBoolean("enable_alerts", true);
    }

    public void sentTokenToServer(boolean value) {
        prefs.edit().putBoolean("token_sent", value).apply();
    }

    public boolean getTokenIsSent() {
        return prefs.getBoolean("token_sent", false);
    }

    public void storeToken(String token) {
        prefs.edit().putString("token", token).apply();
        HeartBeatService.sendHeartBeat(ctx);
    }

    public String getToken() {
        return prefs.getString("token", null);
    }

    public void setCurrentRoom(boolean seenAny, UberMapping newMap, Integer bestRssi) {
        UberRoomManager rMan = UberRoomManager.getInstance(ctx);
        if (newMap != null) {
            UberRoom room = rMan.getById(newMap.beacon);
            if (!isWorthUpdatingFor(room)) {
                return;
            }
            Log.d("UPDATING CURRENT ROOM", newMap.beacon.getMac());

            prefs.edit().putString("current_room", room.roomName).
                    putInt("current_room_rssi", bestRssi).
                    putBoolean("moved", false).
                    apply();

            HeartBeatService.sendHeartBeat(ctx);
        }

        if (!seenAny) {
            HeartBeatService.sendHeartBeat(ctx);
        }
    }

    private boolean isWorthUpdatingFor(UberRoom newRoom) {
        if (newRoom == null) {
            return false;
        }

        boolean isUnknown = getCurrentRoom() == null;
        boolean roomIsTheSame = !isUnknown && getCurrentRoom().equals(newRoom);
        boolean needToUpdate = (!roomIsTheSame && getHasMoved()) || isUnknown;

        if (needToUpdate) {
            UberBeaconManager beaconManager = UberBeaconManager.getInstance(ctx);

            UberMapping lastDataForLastRoom = beaconManager.getLastMappingForRoom(getCurrentRoom());
            UberMapping currentDataForLastRoom = beaconManager.getCurrentMappingForRoom(getCurrentRoom());

            UberMapping lastDataForNewRoom = beaconManager.getLastMappingForRoom(newRoom);
            UberMapping currentDataForNewRoom = beaconManager.getCurrentMappingForRoom(newRoom);


            if (lastDataForLastRoom != null && lastDataForNewRoom != null) {
                double newDelta = (currentDataForNewRoom.distance - lastDataForNewRoom.distance);
                double oldDelta = (lastDataForLastRoom.distance - currentDataForLastRoom.distance);
                Log.d("DELTAS", newDelta + " = " + oldDelta);
//                if (Math.abs(lastDataForLastRoom.distance - currentDataForLastRoom.distance) > 1) {
//                    return true;
//                }
            }
        }
        return needToUpdate;
    }

    public UberRoom getCurrentRoom() {
        UberRoomManager rMan = UberRoomManager.getInstance(ctx);
        return rMan.get(prefs.getString("current_room", null));
    }

    public int getCurrentRssi() {
        return prefs.getInt("current_room_rssi", 0);
    }


    public void setProfile(String personName, String profile_id, String email, String photoUrl) {
        prefs.edit().
                putString("name", personName).
                putString("email", email).
                putString("id", profile_id).
                putString("photoUrl", photoUrl).
                apply();
    }

    public String getEmail() {
        return prefs.getString("email", null);
    }

    public String getId() {
        return prefs.getString("id", null);
    }

    public String getPhotoUrl() {
        return prefs.getString("photoUrl", null);
    }
    public String getHost() {
        return prefs.getString("host", null);
    }

    public boolean getHasMoved() {
        return prefs.getBoolean("moved", false);
    }

    public void setHasMoved(boolean hasMoved) {
        prefs.edit().putBoolean("moved", hasMoved).apply();
    }

    public void setImageUploaded(File image, boolean hasBeenPushed) {
        prefs.edit().putBoolean(image.getName() + "_uploaded", hasBeenPushed).apply();
    }
}
