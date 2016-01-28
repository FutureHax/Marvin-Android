package com.futurehax.marvin.api;

import android.content.Context;
import android.util.Log;

import com.futurehax.marvin.manager.UberBeaconManager;
import com.futurehax.marvin.models.UberRoom;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class ToggleRoomTask extends BasicTask {
    UberRoom room;

    public ToggleRoomTask(Context ctx, UberRoom room) {
        super(ctx, null);
        this.room = room;
    }

    @Override
    protected String doInBackground(Void... params) {
        if (!UberBeaconManager.getInstance(mContext).getIsHome()) {
            return "Failed";
        }
        try {
            JSONObject o = new JSONObject();
            o.put("user", mPreferences.getName().split(" ")[0]);
            String attempt = mUrlGenerator.getBlockingRequestWithJson(mUrlGenerator.generate(UrlGenerator.TOGGLE), o);
            return attempt == null ? "false" : attempt;
        } catch (IllegalStateException | ExecutionException | JSONException | InterruptedException e) {
            e.printStackTrace();
            return "Failed";
        }

    }

    @Override
    protected void onPostExecute(final String success) {
        Log.d("THE TOGGLE RESULT", success);
    }

}
