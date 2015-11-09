package com.futurehax.marvin.models;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.MacAddress;

import java.util.UUID;

/**
 * Created by FutureHax on 10/19/15.
 */
public class UberBeacon extends Beacon {

    public UberBeacon(Beacon b) {
        super(b.getProximityUUID(), b.getMacAddress(), b.getMajor(), b.getMinor(), b.getMeasuredPower(), b.getRssi());
    }

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        } else if(o != null && this.getClass() == o.getClass()) {
            UberBeacon beacon = (UberBeacon)o;
            return getMac().equals(beacon.getMac());
        } else if(o != null && Beacon.class == o.getClass()) {
            Beacon beacon = (Beacon)o;
            return getMac().equals(beacon.getMacAddress().toStandardString());
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = getProximityUUID().hashCode();
        result = 31 * result + getMajor();
        result = 31 * result + getMinor();
        result = 31 * result + getMacAddress().hashCode();
        return result;
    }

    public String getMac() {
        return getMacAddress().toStandardString();
    }
}
