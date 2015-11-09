package com.futurehax.marvin.models;

import com.estimote.sdk.Region;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/**
 * Created by FutureHax on 10/29/15.
 */
public class UberRoom implements Serializable {
    @Override
    public String toString() {
        return "UberRoom{" +
                "roomName='" + roomName + '\'' +
                ", devices=" + devices +
                ", beacons=" + beacons +
                '}';
    }

    public final String roomName;
    public final ArrayList<AttachedDevice> devices;
    public final ArrayList<BeaconIdentifier> beacons;

    public UberRoom(String roomName, ArrayList<AttachedDevice> devices, ArrayList<BeaconIdentifier> beacons) {
        this.roomName = roomName;
        this.devices = devices;
        this.beacons = beacons;
    }

    public UberRoom(String name, JsonArray beacons, JsonArray devices) {
        this.devices = new ArrayList<>();
        this.beacons = new ArrayList<>();

        this.roomName = name;

        Iterator<JsonElement> bi = beacons.iterator();
        while (bi.hasNext()) {
            JsonObject beaconId = bi.next().getAsJsonObject();

            String mac = beaconId.get("mac").getAsString();
            String uuid = beaconId.get("uuid").getAsString();
            this.beacons.add(new BeaconIdentifier(mac, uuid));
        }

        Iterator<JsonElement> di = devices.iterator();
        while (di.hasNext()) {
            JsonObject device = di.next().getAsJsonObject();
            this.devices.add(new AttachedDevice(
                    device.get("name").getAsString(),
                    device.get("id").getAsString(),
                    device.get("type").getAsString()));
        }
    }

    public String getBeacons() {
        StringBuilder sb = new StringBuilder();
        for (BeaconIdentifier beacon : beacons) {
            sb.append(beacon.mac + "\n");
        }
        return sb.toString();
    }

    public String getDevices() {
        StringBuilder sb = new StringBuilder();
        for (AttachedDevice device : devices) {
            sb.append(device.type + ": " + device.name + "\n");
        }
        return sb.toString();
    }

    public Region[] getEstimoteRegions() {
        Region[] regions = new Region[beacons.size()];
        for (int i = 0; i < regions.length; i++) {
            regions[i] = new com.estimote.sdk.Region(roomName, UUID.fromString(beacons.get(i).uuid), null, null);
        }
        return regions;

    }

}
