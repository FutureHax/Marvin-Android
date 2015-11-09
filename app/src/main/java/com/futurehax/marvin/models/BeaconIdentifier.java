package com.futurehax.marvin.models;

import java.io.Serializable;

/**
 * Created by FutureHax on 10/29/15.
 */
public class BeaconIdentifier implements Serializable {
    public final String mac, uuid;

    @Override
    public String toString() {
        return "BeaconIdentifier{" +
                "mac='" + mac + '\'' +
                ", uuid='" + uuid + '\'' +
                '}';
    }

    public BeaconIdentifier(String mac, String uuid) {
        this.mac = mac;
        this.uuid = uuid;
    }
}
