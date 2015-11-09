package com.futurehax.marvin.api;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.futurehax.marvin.RoommateAdapter;
import com.futurehax.marvin.UrlGenerator;
import com.futurehax.marvin.models.AttachedDevice;
import com.futurehax.marvin.models.Roommate;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class GetAllDevicesTask extends BasicTask {

    public interface IGetAllDevicesTask {
         void onDevicesFetched(ArrayList<AttachedDevice> devices);
    }
    AlertDialog.Builder b;
    final IGetAllDevicesTask listener;

    public GetAllDevicesTask(Activity ctx, IGetAllDevicesTask listener) {
        super(ctx, null);
        this.listener = listener;
    }

    @Override
        protected String doInBackground(Void... params) {
            try {
                String attempt = mUrlGenerator.getRequest(mUrlGenerator.generate(UrlGenerator.DEVICES));
                return attempt == null ? "Failed" : attempt;
            } catch (Exception e) {
                return "Failed";
            }
        }


        @Override
        protected void onPostExecute(final String success) {
            if (!success.equals("Failed")) {
                try {
                    JsonObject o = new JsonParser().parse(success).getAsJsonObject();

                    final ArrayList<AttachedDevice> devices = new ArrayList<>();
                    Iterator<Map.Entry<String, JsonElement>> it = o.entrySet().iterator();
                    while (it.hasNext()) {
                        JsonObject device = it.next().getValue().getAsJsonObject();
                        devices.add(new AttachedDevice(
                                device.get("name").getAsString(),
                                device.get("id").getAsString(),
                                device.get("type").getAsString()));
                    }

                    listener.onDevicesFetched(devices);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mActivity, "Error getting devices", Toast.LENGTH_LONG).show();
                    listener.onDevicesFetched(null);
                }
            }
        }
    }
