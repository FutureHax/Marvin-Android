package com.futurehax.marvin.api;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.futurehax.marvin.UrlGenerator;
import com.futurehax.marvin.models.UberRoom;

public class ToggleRoomTask extends BasicTask {
    UberRoom room;

    public ToggleRoomTask(Context ctx, UberRoom room) {
        super(ctx, null);
        this.room = room;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            String attempt = mUrlGenerator.getRequest(mUrlGenerator.generate(UrlGenerator.TOGGLE),
                    mPreferences.getName().split(" ")[0] + "&user");
            return attempt == null ? "false" : attempt;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed";
        }
    }

    @Override
    protected void onPostExecute(final String success) {
//        if (!success.equals("false")) {
            Log.d("THE TOGGLE RESULT", success);
//        }
    }

}
