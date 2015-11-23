package com.futurehax.marvin.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.preference.PreferenceFragment;

import com.futurehax.marvin.PreferencesProvider;
import com.futurehax.marvin.R;
import com.futurehax.marvin.UberEstimoteApplication;
import com.futurehax.marvin.activities.LoginActivity;

public class GeneralPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        setHasOptionsMenu(true);
        getActivity().setTitle("Settings");

        Preference host = findPreference("host");
        String hostName = new PreferencesProvider(getActivity()).getHost();
        if (hostName != null) {
            host.setSummary(hostName);
        }
        host.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Intent i = new Intent(preference.getContext(), LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                preference.getContext().startActivity(i);
                return true;
            }
        });

        findPreference("enable_backup").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                UberEstimoteApplication app = ((UberEstimoteApplication) getActivity().getApplication());
                boolean value = (boolean) newValue;
                if (value) {
                    app.startupListener();
                } else {
                    app.stopListener();
                }
                return true;
            }
        });
    }
}
