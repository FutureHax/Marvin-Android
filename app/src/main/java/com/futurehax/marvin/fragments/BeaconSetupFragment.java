package com.futurehax.marvin.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import com.futurehax.marvin.R;
import com.futurehax.marvin.activities.AddRoomActivity;
import com.futurehax.marvin.adapters.UberRoomAdapter;
import com.futurehax.marvin.manager.UberRoomManager;
import com.futurehax.marvin.models.UberRoom;

import java.util.ArrayList;


public class BeaconSetupFragment extends Fragment {
    RecyclerView recyclerView;
    UberRoomAdapter adapter;
    View root;

    ViewFlipper flippy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Setup");
        root = inflater.inflate(R.layout.content_list, container, false);
        flippy = (ViewFlipper) root.findViewById(R.id.flippy);
        setupRecyclerView();
        return root;
    }

    public void setupRecyclerView() {
        recyclerView = (RecyclerView) root.findViewById(R.id.rv);

        flippy.setDisplayedChild(1);
        ArrayList<UberRoom> rooms = UberRoomManager.getInstance(getContext()).getRooms();
        adapter = new UberRoomAdapter(rooms, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("THE TAG", v.getTag().toString());
                Intent i = new Intent(v.getContext(), AddRoomActivity.class);
                i.putExtra("data", ((UberRoom) v.getTag()));
                getActivity().startActivityForResult(i, 1);
            }
        });
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}