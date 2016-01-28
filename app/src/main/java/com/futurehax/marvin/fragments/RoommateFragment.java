package com.futurehax.marvin.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import com.futurehax.marvin.R;
import com.futurehax.marvin.adapters.RoommateAdapter;
import com.futurehax.marvin.api.UrlGenerator;
import com.futurehax.marvin.models.Roommate;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;

import java.util.ArrayList;
import java.util.Collections;

public class RoommateFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerView recyclerView;
    View root;
    ViewFlipper flippy;
    private SwipeRefreshLayout swipeLayout;


    Handler handy = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handy.removeCallbacks(runnable);
            fetchRoommates();
            handy.postDelayed(this, 5000);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        handy.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (root != null) {
            setupRecyclerView();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Roommates");
        root = inflater.inflate(R.layout.content_list, container, false);
        flippy = (ViewFlipper) root.findViewById(R.id.flippy);

        swipeLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        return root;
    }

    private void setupRecyclerView() {
        recyclerView = (RecyclerView) root.findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);

        handy.post(runnable);
    }

    private void fetchRoommates() {
        UrlGenerator mUrlGenerator = new UrlGenerator(getActivity());
        mUrlGenerator.getRequestWithJson(mUrlGenerator.generate(UrlGenerator.ROOMMATES)).setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String success) {
                if (success != null &&
                        !success.equals("Failed") &&
                        !success.equals("Unauthorized")) {
                    JsonArray result = new JsonParser().parse(success).getAsJsonArray();

                    final ArrayList<Roommate> roommates = new ArrayList<>();
                    for (JsonElement aResult : result) {
                        JsonObject user = aResult.getAsJsonObject();
                        roommates.add(new Roommate(user.get("name").getAsString(), user.get("status").getAsBoolean(), user.get("room").getAsString(), user.get("lastBeat").getAsLong()));
                    }

                    Collections.sort(roommates, new Roommate.RoommateComparator());

                    recyclerView.setAdapter(new RoommateAdapter(roommates));
                    flippy.setDisplayedChild(1);
                    swipeLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        handy.post(runnable);
    }
}