package com.futurehax.marvin.models;

import java.io.Serializable;

/**
 * Created by FutureHax on 10/28/15.
 */
public class AttachedDevice implements Serializable {

    public final String name, id, type;

    @Override
    public String toString() {
        return type + ": " + name;
    }

    public AttachedDevice(String name, String id, String type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }
}
