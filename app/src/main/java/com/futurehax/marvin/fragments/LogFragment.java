package com.futurehax.marvin.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.futurehax.marvin.R;
import com.futurehax.marvin.api.CheckLogTask;

public class LogFragment extends Fragment {

    View root;
    TextView logView;
    ViewFlipper flippy;

    Handler handy = new Handler();
    Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            new CheckLogTask(getActivity(), 50, logView, flippy).execute();
            handy.postDelayed(this, 3000);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handy.removeCallbacks(updateRunnable);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.frag_log, container, false);
        logView = (TextView) root.findViewById(R.id.text);
        flippy = (ViewFlipper) root.findViewById(R.id.flippy);
        handy.post(updateRunnable);
        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle("Server Log");
    }


}
