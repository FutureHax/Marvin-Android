package com.futurehax.marvin.api;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.widget.ViewFlipper;

import com.futurehax.marvin.RoommateAdapter;
import com.futurehax.marvin.UrlGenerator;
import com.futurehax.marvin.models.Roommate;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class RoommateTask extends BasicTask {

    RecyclerView mRecyclerView;

    public RoommateTask(Activity ctx, ViewFlipper flippy, RecyclerView mRecyclerView) {
        super(ctx, flippy);
        this.mRecyclerView = mRecyclerView;
    }

    @Override
        protected String doInBackground(Void... params) {
            try {
                String attempt = mUrlGenerator.getRequest(mUrlGenerator.generate(UrlGenerator.ROOMMATES));
                return attempt == null ? "Failed" : attempt;
            } catch (Exception e) {
                return "Failed";
            }
        }


        @Override
        protected void onPostExecute(final String success) {
            if (!success.equals("Failed")) {
                try {
                    JsonArray result = new JsonParser().parse(success).getAsJsonArray();

                    final ArrayList<Roommate> roommates = new ArrayList<>();
                    Iterator<JsonElement> it = result.iterator();
                    while (it.hasNext()) {
                        JsonObject user = it.next().getAsJsonObject();
                        roommates.add(new Roommate(user.get("name").getAsString(), user.get("status").getAsBoolean(), user.get("room").getAsString(), user.get("lastBeat").getAsLong()));
                    }

                    Collections.sort(roommates, new Roommate.RoommateComparator());
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.setAdapter(new RoommateAdapter(roommates));
                            mFlipper.setDisplayedChild(1);
                        }
                    });
                } catch (Exception e) {

                }
            }
        }
    }
