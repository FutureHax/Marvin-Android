package com.futurehax.marvin.api;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.futurehax.marvin.UrlGenerator;
import com.futurehax.marvin.gitignore.Constants;
import com.futurehax.marvin.models.AttachedDevice;
import com.futurehax.marvin.models.BeaconIdentifier;
import com.futurehax.marvin.models.UberRoom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
            data.put("token", Constants.TOKEN);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            String attempt = urlGenerator.getRequestWithJson(urlGenerator.generate(UrlGenerator.ADD_ROOM), data);
            Log.d("ADD ROOM", attempt == null ? "Failed" : attempt + " : " + data.toString(1));
            return attempt;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Failed";
    }

    @Override
    protected void onPostExecute(final String success) {
        if (success != null &&
                !success.equals("Failed")) {
            if (mContext instanceof Activity) {
                ((Activity) mContext).finish();
            }
        }
    }
}
