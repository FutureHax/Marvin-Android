package com.futurehax.marvin.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import com.futurehax.marvin.adapters.ApkAdapter;
import com.futurehax.marvin.adapters.DBAdapter;
import com.futurehax.marvin.manager.PreferencesProvider;
import com.futurehax.marvin.R;
import com.futurehax.marvin.activities.AppDetailActivity;
import com.futurehax.marvin.models.InterestingApp;

import java.util.List;

public class NotificationsFragment extends Fragment {
    RecyclerView list;
    View root;
    public ApkAdapter adapter;
    PreferencesProvider mPreferences;
    DBAdapter db;

//    FAB fab;


    public ApkAdapter getAdapter() {
        return adapter;
    }

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static NotificationsFragment newInstance() {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public NotificationsFragment() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DBAdapter(getActivity());
        db.open();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_app_list, container, false);
        mPreferences = new PreferencesProvider(getActivity());
//        fab = (FAB) root.findViewById(R.id.fabbutton);
//        fab.setDrawable(getResources().getDrawable(settings.getIsAppEnabled() ? R.drawable.ic_launcher : R.drawable.ic_launcher_off));
//        fab.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Toast.makeText(v.getContext(), "Click to enable/disable HueNotifier.", Toast.LENGTH_LONG).show();
//                return true;
//            }
//        });
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean newState = !settings.getIsAppEnabled();
//                settings.setIsAppEnabled(newState);
//                WidgetProvider.updateWidget(v.getContext(), newState);
//                getActivity().getActionBar().setIcon(
//                        newState ? R.drawable.ic_launcher
//                                : R.drawable.ic_launcher_off
//                );
//                fab.setDrawable(getResources().getDrawable(newState ? R.drawable.ic_launcher : R.drawable.ic_launcher_off));
//                if (settings.needsEnableToast()) {
//                    Toast.makeText(v.getContext(), "HueNotifier is now " + (settings.getIsAppEnabled() ? "enabled" : "disabled"), Toast.LENGTH_LONG).show();
//                }
//                settings.incrementEnableToast();
//            }
//
//        });

        list = (RecyclerView) root.findViewById(R.id.list);
        list.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        adapter = new ApkAdapter(db, getActivity(), list, new ApkAdapter.OnItemClickedListener() {
            @Override
            public void onItemClicked(int position) {
                Intent i = new Intent(getActivity(), AppDetailActivity.class);
                i.putExtra("app", getAdapter().getItem(position));
                startActivityForResult(i, 100);

//                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, AppSettingsFragment.newInstance(adapter.getItem(position)), "app_settings").commit();

            }
        });
        list.setAdapter(adapter);
        adapter.generateList(new ApkAdapter.ListListener() {
            @Override
            public void onListReady(final List<InterestingApp> results) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        list.setAdapter(adapter);
                        ((ViewFlipper) root.findViewById(R.id.flippy)).showNext();
                    }
                });
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.handleUpdate();
        }
    }

}