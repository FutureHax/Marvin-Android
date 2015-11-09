package com.futurehax.marvin.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import com.futurehax.marvin.R;
import com.futurehax.marvin.api.RoommateTask;

public class RoommateFragment extends Fragment {
    RecyclerView recyclerView;
    View root;
    ViewFlipper flippy;

    Handler handy = new Handler();

    Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            getRoommates();
            handy.postDelayed(this, 1000);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        handy.removeCallbacks(updateRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        handy.removeCallbacks(updateRunnable);
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
        return root;
    }

    private void setupRecyclerView() {
        recyclerView = (RecyclerView) root.findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);

        handy.post(updateRunnable);
    }

    public void getRoommates() {
        new RoommateTask(getActivity(), flippy, recyclerView).execute();
    }

}