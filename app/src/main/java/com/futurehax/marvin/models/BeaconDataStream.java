package com.futurehax.marvin.models;

import java.util.ArrayList;

/**
 * Created by FutureHax on 10/30/15.
 */
public class BeaconDataStream {
    private static final int MAX_STREAM_COUNT = 15;

    public double getCurrentStream() {
        double v = 0;
        for (int i = 0; i < currentStream.size() - 1; i++) {
            v = v + currentStream.get(i);
        }

        v = v / currentStream.size();
        return v;
    }

    private volatile ArrayList<Double> currentStream = new ArrayList<>();

    public void addToStream(double toAdd) {
        if ((toAdd != 0) && !isOutlier(toAdd)) {
            if (currentStream.size() > MAX_STREAM_COUNT) {
                for (int i = 0; i < currentStream.size() - 1; i++) {
                    currentStream.set(i, currentStream.get(i + 1));
                }
                currentStream.set(currentStream.size() - 1, toAdd);
            } else {
                currentStream.add(toAdd);
            }
        }
    }

    private boolean isOutlier(double toAdd) {
//        if (currentStream.isEmpty()) {
            return false;
//        }
//        double cStream = getCurrentStream();
//        return toAdd < cStream - cStream / 4 || toAdd > cStream + cStream / 4;
    }
}
