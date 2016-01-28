package com.futurehax.marvin.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.futurehax.marvin.adapters.DBAdapter;
import com.futurehax.marvin.R;
import com.futurehax.marvin.fragments.AppSettingsFragment;
import com.futurehax.marvin.models.InterestingApp;


public class AppDetailActivity extends AppCompatActivity {
    InterestingApp app;

    boolean deleting = false;
    DBAdapter db;

    ImageView appIcon;
    TextView appName;
    Switch enableSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_list_detail);

        app = getIntent().getParcelableExtra("app");
        appIcon = (ImageView) findViewById(R.id.app_icon);
        appName = (TextView) findViewById(R.id.app_name);
        enableSwitch = (Switch) findViewById(R.id.enable_switch);
        findViewById(R.id.header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableSwitch.setChecked(!enableSwitch.isChecked());
            }
        });
        db = new DBAdapter(this);
        db.open();

        setAppDetailsActionBar();

        getSupportFragmentManager().beginTransaction().replace(R.id.content, AppSettingsFragment.newInstance(app), "pref").commit();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (db.isAppInteresting(app.getPackageName())) {
            MenuItem item = menu.findItem(R.id.action_delete);
            item.setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
        } else if (id == R.id.action_delete) {
            delete();
        }
        return super.onOptionsItemSelected(item);
    }

    private void delete() {
        deleting = true;
        db.deleteInterestingPackage(app.getPackageName());
        Intent i = new Intent();
        i.putExtra("result", app);
        setResult(420, i);
        finish();
    }

    @Override
    public void finish() {
        super.finish();

        if (!deleting) {
            Intent i = new Intent();
            i.putExtra("result", app.getPackageName());
            setResult(RESULT_OK, i);
            db.insertAppData(app);
            db.close();
        }
    }

    private void setAppDetailsActionBar() {
        PackageManager pMan = getPackageManager();
        try {
            appIcon.setImageDrawable(pMan.getApplicationIcon(app.getPackageName()));
            appName.setText(app.getAppName());
            enableSwitch.setChecked(app.isActive());
            enableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    app.setActive(isChecked);
                }
            });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}