package com.futurehax.marvin;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.futurehax.marvin.models.InterestingApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ApkAdapter extends RecyclerView.Adapter<ApkAdapter.SimpleViewHolder> {

    DBAdapter db;
    private Handler handy = new Handler();

    private View.OnClickListener enableListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InterestingApp interestingApp = getItem((Integer) v.getTag());
            if (!interestingApp.isActive()) {
                activateApp((Integer) v.getTag());
            } else {
                deactivateApp((Integer) v.getTag());
            }
        }
    };

    public void onActivityResult(int requestCode) {
        generateList(new ApkAdapter.ListListener() {
            @Override
            public void onListReady(final List<InterestingApp> results) {
                items = results;
                notifyDataSetChanged();
            }
        });
    }

    public interface ListListener {
        public void onListReady(List<InterestingApp> list);
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView statusTextView, appNameView;
        ImageView statusIconView, appIconView, settings;
        View status, statusBackground, appBackground;

        public SimpleViewHolder(View view) {
            super(view);

            appIconView = (ImageView) view.findViewById(R.id.appicon);
            appNameView = (TextView) view.findViewById(R.id.appname);
            statusIconView = (ImageView) view
                    .findViewById(R.id.status_icon);
            statusTextView = (TextView) view
                    .findViewById(R.id.status_text);

            status = view.findViewById(R.id.status);
            settings = (ImageView) view.findViewById(R.id.more);
            appBackground = view.findViewById(R.id.not_buttons);
            statusBackground = view.findViewById(R.id.status_holder);
        }
    }

    public void generateList(final ListListener listListener) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> interestingList = db.getInterestingAppPackageNames();
                List<ApplicationInfo> installedApps = context.getPackageManager()
                        .getInstalledApplications(PackageManager.PERMISSION_GRANTED);
                final List<InterestingApp> launchableInstalledApps = new ArrayList<InterestingApp>();
                for (int i = 0; i < installedApps.size(); i++) {
                    if (context.getPackageManager()
                            .getLaunchIntentForPackage(installedApps.get(i).packageName) != null) {
                        InterestingApp app = db.getInterestingApp(installedApps.get(i).packageName);
                        if (app == null) {
                            app = new InterestingApp(installedApps.get(i).packageName, interestingList.contains(installedApps.get(i).packageName), context);
                            try {
                                app.setAppName(context.getPackageManager().getApplicationLabel(context.getPackageManager().getApplicationInfo(installedApps.get(i).packageName, 0)).toString());
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                        launchableInstalledApps.add(app);
                    }
                }

                Collections.sort(launchableInstalledApps, new Comparator<InterestingApp>() {
                    @Override
                    public int compare(InterestingApp lhs, InterestingApp rhs) {
                        return lhs.getAppName().compareToIgnoreCase(rhs.getAppName());
                    }
                });

                new Handler(context.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        listListener.onListReady(launchableInstalledApps);
                    }
                });
            }
        });

        t.start();

    }

    public ApkAdapter(DBAdapter db, Context context, RecyclerView recyclerView,
                      OnItemClickedListener itemClickedListener) {
        this.recyclerView = recyclerView;
        this.context = context;
        packageManager = context.getPackageManager();
//        phHueSDK = PHHueSDK.create();
//        bridge = phHueSDK.getSelectedBridge();
        this.itemClickedListener = itemClickedListener;
        this.db = db;
        items = new ArrayList<>();
//        generateList(new ListListener() {
//            @Override
//            public void onListReady(List<InterestingApp> list) {
//                items = list;
//                notifyDataSetChanged();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    Context context;
    PackageManager packageManager;
    RecyclerView recyclerView;
    List<InterestingApp> items;

//    PHHueSDK phHueSDK;
//    PHBridge bridge;

    public InterestingApp getItem(int position) {
        return items.get(position);
    }

    public int generateTextColor(int c) {
        return (Math.sqrt(
                Color.red(c) * Color.red(c) * .241 +
                        Color.green(c) * Color.green(c) * .691 +
                        Color.blue(c) * Color.blue(c) * .068
        ) > 130) ? Color.BLACK : Color.WHITE;
    }

    @Override
    public ApkAdapter.SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SimpleViewHolder(LayoutInflater.from(context).inflate(R.layout.apk_card, parent, false));
    }

    @Override
    public void onBindViewHolder(ApkAdapter.SimpleViewHolder holder, final int position) {
        final InterestingApp interestingApp = (getItem(position));

        LayerDrawable background = (LayerDrawable) holder.status.getBackground();
        background.findDrawableByLayerId(R.id.rounded_footer_background).setColorFilter(new
                PorterDuffColorFilter(interestingApp.getHue(), PorterDuff.Mode.MULTIPLY));
        if (interestingApp.isActive()) {
            holder.statusTextView.setText("Enabled");
            holder.statusIconView.setImageResource(R.drawable.ic_launcher);
        } else {
            holder.statusTextView.setText("Disabled");
            holder.statusIconView.setImageResource(R.drawable.ic_launcher);
        }

        holder.statusBackground.setTag(position);
        holder.statusBackground.setOnClickListener(enableListener);

        holder.statusTextView.setTextColor(generateTextColor(interestingApp.getHue()));
        holder.settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.action_test) {
                            for (String groupId : interestingApp.getGroupIds().split(":")) {
//                                PHGroup group = bridge.getResourceCache().getGroups().get(groupId);
//                                if (group == null) {
//                                    continue;
//                                }
//                                int r = Color.red(interestingApp.getHue());
//                                int g = Color.green(interestingApp.getHue());
//                                int b = Color.blue(interestingApp.getHue());
//
//                                float xy[] = PHUtilities.calculateXYFromRGB(r, g, b,
//                                        bridge.getResourceCache().getLights().get(group.getLightIdentifiers().get(0)).getModelNumber());
//                                PHLightState lightState = new PHLightState();
//                                lightState.setX(xy[0]);
//                                lightState.setY(xy[1]);
//
//                                new ColoredBulbFlash(bridge)
//                                        .withGroup(group)
//                                        .withState(lightState)
//                                        .withTargetFlashesCount(interestingApp.getLength())
//                                        .start();
                            }
                        }
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.settings_menu);
                v.post(new Runnable() {
                    @Override
                    public void run() {
                        popupMenu.show();
                    }
                });
            }
        });

        try {
            holder.appIconView.setImageDrawable(packageManager.getApplicationIcon(packageManager.getApplicationInfo(interestingApp.getPackageName(), 0)));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        holder.appNameView.setText(interestingApp.getAppName());
        holder.appBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickedListener.onItemClicked(position);
            }
        });

        if (interestingApp.getHue() == R.color.background_material_dark) {
            holder.settings.setVisibility(View.INVISIBLE);
        } else {
            holder.settings.setVisibility(View.VISIBLE);
            holder.settings.setColorFilter(new
                    PorterDuffColorFilter(generateTextColor(interestingApp.getHue()), PorterDuff.Mode.MULTIPLY));

        }
    }

    private void activateApp(int position) {
        InterestingApp app = getItem(position);
        app.setActive(true);
        if (app.getHue() == -1) {
            app.setHue(Color.parseColor("#2196F3"));
        }
        db.insertAppData(app);

        handleUpdate();
    }

    public void handleUpdate() {
        generateList(new ApkAdapter.ListListener() {
            @Override
            public void onListReady(final List<InterestingApp> results) {
                items = results;
                notifyDataSetChanged();
            }
        });
    }

    private void deactivateApp(int position) {
        InterestingApp app = getItem(position);
        db.deleteInterestingPackage(app.getPackageName());
        handleUpdate();
    }

    OnItemClickedListener itemClickedListener;

    public interface OnItemClickedListener {
        void onItemClicked(int position);
    }

}