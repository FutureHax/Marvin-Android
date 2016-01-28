package com.futurehax.marvin.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.futurehax.marvin.manager.PreferencesProvider;
import com.futurehax.marvin.R;
import com.futurehax.marvin.UberEstimoteApplication;
import com.futurehax.marvin.manager.UberRoomManager;
import com.futurehax.marvin.api.SpeakTask;
import com.futurehax.marvin.fragments.BeaconSetupFragment;
import com.futurehax.marvin.fragments.GeneralPreferenceFragment;
import com.futurehax.marvin.fragments.LogFragment;
import com.futurehax.marvin.fragments.NotificationsFragment;
import com.futurehax.marvin.fragments.RoommateFragment;
import com.futurehax.marvin.service.HeartBeatService;
import com.futurehax.marvin.service.UpdaterService;
import com.koushikdutta.ion.Ion;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    PreferencesProvider mPreferences;
    TextView userName;
    FloatingActionButton fab;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        BeaconSetupFragment setup = (BeaconSetupFragment) getSupportFragmentManager().findFragmentByTag("setup");
        if (setup != null && setup.isVisible()) {
            setup.setupRecyclerView();
        }
    }

    private View.OnClickListener fabListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            android.support.v4.app.Fragment setup = getSupportFragmentManager().findFragmentByTag("setup");
            if (setup != null && setup.isVisible()) {
                startActivityForResult(new Intent(view.getContext(), AddRoomActivity.class), 0);
            } else {
                startService(new Intent(view.getContext(), HeartBeatService.class));

                Snackbar.make(fab, "Sending heartbeart...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    };


    UberEstimoteApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        app = ((UberEstimoteApplication) getApplication());

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(fabListener);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        LinearLayout headerView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
        navigationView.addHeaderView(headerView);

        mPreferences = new PreferencesProvider(this);

        userName = (TextView) headerView.findViewById(R.id.username);
        userName.setText(String.format("Welcome %s", mPreferences.getName().split(" ")[0]));

        Ion.with((ImageView) headerView.findViewById(R.id.profile_image))
                .placeholder(R.drawable.ic_launcher)
                .error(R.drawable.ic_list)
                .load(mPreferences.getPhotoUrl());

        UberRoomManager.getInstance(this);
        UpdaterService.startService(this);
        HeartBeatService.sendHeartBeat(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame, new RoommateFragment()).commitAllowingStateLoss();

        if (mPreferences.getHost() == null) {
            Snackbar.make(fab, "No hostname provided", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Update Host", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new GeneralPreferenceFragment(), "settings").commit();
                            fab.setImageResource(R.drawable.ic_sync);
                        }
                    }).show();
        }

        if (mPreferences.getBackupEnabled()) {
            app.startupListener();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_roommates) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new RoommateFragment(), "roommates").commit();
            fab.setImageResource(R.drawable.ic_sync);
        } else if (id == R.id.nav_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new GeneralPreferenceFragment(), "settings").commit();
            fab.setImageResource(R.drawable.ic_sync);
        } else if (id == R.id.nav_log) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new LogFragment(), "log").commit();
            fab.setImageResource(R.drawable.ic_sync);
        } else if (id == R.id.nav_notifications) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new NotificationsFragment(), "notifications").commit();
//            fab.setImageResource(R.drawable.ic_sync);
        } else if (id == R.id.nav_speak) {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            final EditText input = new EditText(this);
            b.setView(input).setTitle("Marvin Talks!").setMessage("Enter text for Marvin.")
                    .setPositiveButton("Speak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new SpeakTask(MainActivity.this, input.getText().toString()).execute();
                        }
                    }).show();
        } else if (id == R.id.nav_beacons) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new BeaconSetupFragment(), "setup").commit();
            fab.setImageResource(R.drawable.ic_action_add);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
