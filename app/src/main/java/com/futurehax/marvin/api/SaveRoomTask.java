package com.futurehax.marvin.api;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.futurehax.marvin.models.AttachedDevice;
import com.futurehax.marvin.models.BeaconIdentifier;
import com.futurehax.marvin.models.UberRoom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class SaveRoomTask extends BasicTask {
    UberRoom room;

    public SaveRoomTask(Context ctx, UberRoom room) {
        super(ctx, null);
        this.room = room;
    }

    @Override
    protected String doInBackground(Void... params) {
        UrlGenerator urlGenerator = new UrlGenerator(mContext);

        JSONObject data = new JSONObject();

        try {
            data.put("name", room.roomName);
            JSONArray devicesArray = new JSONArray();
            for (AttachedDevice device : room.devices) {
                JSONObject deviceData = new JSONObject();
                deviceData.put("name", device.name);
                deviceData.put("type", device.type);
                deviceData.put("id", device.id);
                devicesArray.put(deviceData);
            }
            data.put("devices", devicesArray);

            JSONArray beaconArray = new JSONArray();
            for (BeaconIdentifier beacon : room.beacons) {
                JSONObject deviceData = new JSONObject();
                deviceData.put("mac", beacon.mac);
                deviceData.put("uuid", beacon.uuid);
                beaconArray.put(deviceData);
            }
            data.put("beacons", beaconArray);

            String attempt = urlGenerator.getBlockingRequestWithJson(urlGenerator.generate(UrlGenerator.ADD_ROOM), data);
            Log.d("ADD ROOM", attempt == null ? "Failed" : attempt + " : " + data.toString(1));
            return attempt == null ? "Failed" : attempt;
        } catch (IllegalStateException | JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return "Failed";
    }

    @Override
    protected void onPostExecute(final String success) {
        if (!success.equals("Failed") && !success.equals("Unauthorized")) {
            if (mContext instanceof Activity) {
                ((Activity) mContext).finish();
            }
        }
    }
}
