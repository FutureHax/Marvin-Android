package com.futurehax.marvin.models;

/**
 * Created by FutureHax on 10/27/15.
 */
public class UberMapping {

    public final UberBeacon beacon;
    BeaconDataStream dataStream;

    public void updateDataStream() {
        dataStream.addToStream(distance);
    }

    @Override
    public String toString() {
        return "UberMapping{" +
                "beacon=" + beacon +
                ", isInRange=" + isInRange +
                ", distance=" + distance +
                '}';
    }

    public final Boolean isInRange;

    public BeaconDataStream getDataStream() {
        return dataStream;
    }

    public final double distance;

    public UberMapping(UberBeacon beacon, Boolean isInRange, double distance) {
        dataStream = new BeaconDataStream();
        dataStream.addToStream(distance);
        this.beacon = beacon;
        this.isInRange = isInRange;
        this.distance = distance;
    }

    public void setDataStream(BeaconDataStream dataStream) {
        this.dataStream = dataStream;
    }
}
