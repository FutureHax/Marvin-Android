package com.futurehax.marvin.manager;

import android.content.Context;
import android.os.Handler;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.futurehax.marvin.models.UberMapping;
import com.futurehax.marvin.models.BeaconDataStream;
import com.futurehax.marvin.models.BeaconIdentifier;
import com.futurehax.marvin.models.UberBeacon;
import com.futurehax.marvin.models.UberRoom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FutureHax on 10/27/15.
 */
public class UberBeaconManager {

    private static UberBeaconManager instance;
    Context mContext;

    private volatile ArrayList<UberMapping> currentMap, lastMap;
    private volatile UberMapping currentBest;

    private static final long backgroundScanPeriod = 2500L;
    private static final long backgroundBetweenScanPeriod = 125000L;
    private BeaconManager beaconManager;
    Handler handy = new Handler();
    PreferencesProvider mPreferencesProvider;
    UberRoomManager roomManager;

    private UberBeaconManager(Context context) {
        mContext = context.getApplicationContext();
        mPreferencesProvider = new PreferencesProvider(mContext);
        currentMap = new ArrayList<>();
        roomManager = UberRoomManager.getInstance(mContext);
    }

    public synchronized static UberBeaconManager getInstance(Context context) {
        if (instance == null) {
            instance = new UberBeaconManager(context);
        }
        return instance;
    }

    public ArrayList<UberMapping> getCurrentMap() {
        return currentMap;
    }

    public ArrayList<UberMapping> getLastMap() {
        return lastMap;
    }

    public UberMapping getMappingForBeacon(UberBeacon b) {
        for (UberMapping mapping : currentMap) {
            if (mapping.beacon == b) {
                return mapping;
            }
        }

        return null;
    }

    public void handleBeaconStartup() {
        beaconManager = new BeaconManager(mContext);
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                for (UberRoom room : roomManager.getRooms()) {
                    for (Region r : room.getEstimoteRegions()) {
                        beaconManager.startMonitoring(r);
                    }
                }
            }
        });
        beaconManager.setBackgroundScanPeriod(backgroundScanPeriod, backgroundBetweenScanPeriod);

        beaconManager.setMonitoringListener(monitorListener);
        beaconManager.setRangingListener(rangeListener);
        handy.post(statusRunnable);
    }

    BeaconManager.MonitoringListener monitorListener = new BeaconManager.MonitoringListener() {
        @Override
        public void onEnteredRegion(Region region, List<Beacon> beacons) {
            beaconManager.startRanging(region);
        }

        @Override
        public void onExitedRegion(Region region) {
            UberBeacon toUpdate = getMappingForRegion(region.getIdentifier());
            if (toUpdate != null && getMappingForBeacon(toUpdate) != null) {
                updateMap(toUpdate, false);
            }
        }
    };

    BeaconManager.RangingListener rangeListener = new BeaconManager.RangingListener() {
        @Override
        public void onBeaconsDiscovered(Region region, final List<Beacon> rangedBeacons) {
            if (!rangedBeacons.isEmpty()) {
                UberBeacon mapped = getMappingForRegion(region.getIdentifier());
                boolean found = false;
                for (Beacon b : rangedBeacons) {
                    UberBeacon beacon = new UberBeacon(b);
                    if (mapped != null && mapped.equals(beacon)) {
                        found = true;
                    }
                    updateMap(beacon, true);
                }

                if (mapped != null && !found) {
                    monitorListener.onExitedRegion(region);
                }
            } else {
                monitorListener.onExitedRegion(region);
            }
        }
    };

    private UberBeacon getMappingForRegion(String identifier) {
        UberRoom room = roomManager.get(identifier);
        for(UberMapping mapping : getCurrentMap()) {
            UberBeacon b = mapping.beacon;

            for (BeaconIdentifier id : room.beacons) {
                if (id.mac.equalsIgnoreCase(b.getMac())) {
                    return b;
                }
            }
        }
        return null;
    }

    public boolean getIsHome() {
        for (UberMapping mapping : currentMap) {
            if (mapping.isInRange) {
                return true;
            }
        }
        return false;
    }

    private void updateMap(UberBeacon updatedBeacon, boolean isSeen) {
        UberMapping newMapping = new UberMapping(updatedBeacon, isSeen, calculateAccuracy(updatedBeacon.getMeasuredPower(), updatedBeacon.getRssi()));
        BeaconDataStream streamToUpdate = null;

        lastMap = currentMap;
        ArrayList<UberMapping> tmpMapping = new ArrayList<>();
        Integer bestRssi = null;
        Double bestDistance = null;
        boolean seenAny = false;

        for (UberMapping mapping : currentMap) {
            int currentRssi = mapping.beacon.getRssi();
            double currentDistance = mapping.getDataStream().getCurrentStream();
            if (bestRssi == null) {
                bestRssi = currentRssi;
                bestDistance = currentDistance;
                currentBest = mapping;
            } else {
                if (bestDistance > currentDistance) {
                    bestRssi = currentRssi;
                    bestDistance = currentDistance;
                    currentBest = mapping;
                }
            }
            if (!mapping.beacon.equals(updatedBeacon)) {
                if (!seenAny) {
                    seenAny = mapping.isInRange;
                }
                tmpMapping.add(mapping);
            } else {
                streamToUpdate = mapping.getDataStream();
            }
        }
        if (streamToUpdate != null) {
            streamToUpdate.addToStream(newMapping.distance);
            newMapping.setDataStream(streamToUpdate);
        }
        tmpMapping.add(newMapping);
        currentMap = tmpMapping;
        mPreferencesProvider.setCurrentRoom(seenAny, currentBest, bestRssi);
    }

    protected static double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return accuracy;
        }
    }

    Runnable statusRunnable = new Runnable() {
        @Override
        public void run() {
            handy.postDelayed(this, 1000);
        }
    };

    public UberMapping getCurrentMappingForRoom(UberRoom room) {
        if (room == null) {
            return null;
        }
        for(UberMapping mapping : getCurrentMap()) {
            if (room.equals(roomManager.getById(mapping.beacon))) {
                return mapping;
            }
        }

        return null;
    }

    public UberMapping getLastMappingForRoom(UberRoom room) {
        if (room == null) {
            return null;
        }
        for(UberMapping mapping : getLastMap()) {
            if (room.equals(roomManager.getById(mapping.beacon))) {
                return mapping;
            }
        }

        return null;
    }
}
