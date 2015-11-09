package com.futurehax.marvin.api;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.futurehax.marvin.UrlGenerator;
import com.futurehax.marvin.models.AttachedDevice;
import com.futurehax.marvin.models.UberRoom;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class GetAllRoomsTask extends BasicTask {

    public interface IGetAllRoomsTask {
         void onRoomsFetched(ArrayList<UberRoom> rooms);
    }

    final IGetAllRoomsTask listener;

    public GetAllRoomsTask(Context ctx, IGetAllRoomsTask listener) {
        super(ctx, null);
        this.listener = listener;
    }

    @Override
        protected String doInBackground(Void... params) {
            try {
                String attempt = mUrlGenerator.getRequest(mUrlGenerator.generate(UrlGenerator.ROOMS));
                return attempt == null ? "Failed" : attempt;
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed";
            }
        }


        @Override
        protected void onPostExecute(final String success) {
            if (!success.equals("Failed")) {
                try {
                    JsonArray o = new JsonParser().parse(success).getAsJsonArray();

                    final ArrayList<UberRoom> rooms = new ArrayList<>();
                    Iterator<JsonElement> it = o.iterator();
                    while (it.hasNext()) {
                        JsonObject room = it.next().getAsJsonObject();
                        rooms.add(new UberRoom(
                                room.get("_name").getAsString(),
                                room.get("_beaconsArray").getAsJsonArray(),
                                room.get("_devicesArray").getAsJsonArray()));
                    }

                    listener.onRoomsFetched(rooms);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mActivity, "Error getting devices", Toast.LENGTH_LONG).show();
                    listener.onRoomsFetched(null);
                }
            }
        }
    }
