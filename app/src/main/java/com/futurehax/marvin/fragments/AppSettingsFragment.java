package com.futurehax.marvin.fragments;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import com.futurehax.marvin.R;
import com.futurehax.marvin.models.InterestingApp;


public class AppSettingsFragment extends PreferenceFragmentCompat {
    private static final String COLOR_KEY = "calendar_colorkey";
    private static final CharSequence NUMBER_KEY = "number_picker_key";
    InterestingApp app;

    private Preference.OnPreferenceChangeListener preferenceChangedListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference.getKey().equals(NUMBER_KEY)) {
                app.setLength((Integer) newValue);
            } else if (preference.getKey().equals(COLOR_KEY)) {
                app.setHue((Integer) newValue);
            }
            return true;
        }
    };

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.app_settings, container, false);
//        ListView listView = (ListView) view.findViewById(android.R.id.list);
//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//
//                ListView listView = (ListView) parent;
//                ListAdapter listAdapter = listView.getAdapter();
//                final Object obj = listAdapter.getItem(position);
//
//                if (obj != null && obj instanceof View.OnLongClickListener) {
//                    View.OnLongClickListener longListener = (View.OnLongClickListener) obj;
//                    return longListener.onLongClick(view);
//                } else {
//                    AlertDialog.Builder b = new AlertDialog.Builder(view.getContext());
//                    b.setTitle("Confirm Delete").setMessage("Are you sure you want to delete this group?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            deleteGroup(((Preference) obj).getKey());
//                        }
//                    }).show();
//                }
//                return true;
//            }
//        });
//        return view;
//    }

    private void deleteGroup(final String key) {


    }


    public static AppSettingsFragment newInstance(InterestingApp app) {
        Bundle b = new Bundle();
        b.putParcelable("app", app);
        AppSettingsFragment instance = new AppSettingsFragment();
        instance.setArguments(b);
        return instance;
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.app_detail_prefs);

        app = getArguments().getParcelable("app");
//        getActivity().setTitle(app.getAppName());
//        ColorPickerPreference colorPickerPreference = (it.gmariotti.android.example.colorpicker.calendarstock.ColorPickerPreference) findPreference(COLOR_KEY);
//        colorPickerPreference.setValue(app.getHue());
//        colorPickerPreference.setOnPreferenceChangeListener(preferenceChangedListener);
//
//        NumberPickerPreference numberPickerPreference = (NumberPickerPreference) findPreference(NUMBER_KEY);
//        numberPickerPreference.setValue(app.getLength());
//        numberPickerPreference.setOnPreferenceChangeListener(preferenceChangedListener);

        setupGroups();
    }

    public void setupGroups() {
//        PreferenceCategory cat = (PreferenceCategory) findPreference("bulbs");
//        cat.removeAll();
//        for (PHGroup group : bridge.getResourceCache().getAllGroups()) {
//            final CheckBoxPreference pref = new CheckBoxPreference(getActivity());
//            pref.setTitle(group.getName());
//            pref.setKey(group.getIdentifier());
//            pref.setChecked(app.getGroupIdList().contains(pref.getKey()));
//            StringBuilder summaryBuilder = new StringBuilder();
//            int pos = 0;
//            for (String lightId : group.getLightIdentifiers()) {
//                if (pos > 0) {
//                    summaryBuilder.append(" - ");
//                }
//                PHLight light = bridge.getResourceCache().getLights().get(lightId);
//                summaryBuilder.append(light.getName());
//                pos++;
//            }
//            pref.setSummary(summaryBuilder.toString());
//            cat.addPreference(pref);
//
//            pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    app.handleGroupId(preference.getKey(), (boolean) newValue);
//                    return true;
//                }
//            });
//        }

        Preference newGroupPref = new Preference(getActivity());
        newGroupPref.setTitle("Create New Group");
        newGroupPref.setSummary("Manage your light groups");
        newGroupPref.setKey("new_group");
        newGroupPref.setIcon(R.drawable.ic_action_add);
        newGroupPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                addNewGroup();
                return true;
            }
        });
//        cat.addPreference(newGroupPref);
    }

    private void addNewGroup() {
//        LinearLayout content = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.group_name_view, null);
//        final EditText nameInput = (EditText) content.findViewById(R.id.input);
//        final ArrayList<String> selectedItems = new ArrayList();
//        final String[] bulbNames = new String[bridge.getResourceCache().getAllLights().size()];
//        final String[] bulbIds = new String[bridge.getResourceCache().getAllLights().size()];
//
//        int pos = 0;
//        for (PHLight bulb : bridge.getResourceCache().getAllLights()) {
//            bulbNames[pos] = bulb.getName();
//            bulbIds[pos] = bulb.getIdentifier();
//            pos++;
//        }

//        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
//        b.setTitle("Create new group");
//        b.setMultiChoiceItems(bulbNames, null, new DialogInterface.OnMultiChoiceClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                if (isChecked) {
//                    selectedItems.add(bulbIds[which]);
//                } else if (selectedItems.contains(bulbIds[which])) {
//                    selectedItems.remove(bulbIds[which]);
//                }
//            }
//        });
//        b.setPositiveButton("Create", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (!nameInput.getText().toString().isEmpty()) {
//                            bridge.createGroup(nameInput.getText().toString(), selectedItems, new PHGroupListener() {
//                                @Override
//                                public void onCreated(PHGroup phGroup) {
//                                    Log.d("NEW GROUP", "onCreated");
//                                    ((SettingsFragment) getFragmentManager().findFragmentByTag("pref")).setupGroups();
//                                }
//
//                                @Override
//                                public void onReceivingGroupDetails(PHGroup phGroup) {
//                                }
//
//                                @Override
//                                public void onReceivingAllGroups(List<PHBridgeResource> phBridgeResources) {
//                                }
//
//                                @Override
//                                public void onSuccess() {
//                                 }
//
//                                @Override
//                                public void onError(int i, String s) {
//                                }
//
//                                @Override
//                                public void onStateUpdate(Map<String, String> stringStringMap, List<PHHueError> phHueErrors) {
//                                }
//                            });
//                        } else {
//                            addNewGroup();
//                            Toast.makeText(getActivity(), "Please enter a group name.", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                }
//
//        );
//        AlertDialog d = b.create();
//        d.setView(content, 48, 0, 48, 0);
//        d.show();
    }
}