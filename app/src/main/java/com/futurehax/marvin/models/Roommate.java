package com.futurehax.marvin.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Comparator;

public class Roommate implements Parcelable {
    private String name;
    public boolean isHome;

    public String getName() {
        return name.contains(" ") ? name.split(" ")[0] : name;
    }

    public String room;
    public long lastCheckin;

    public Roommate(String name, boolean isHome, String room, long lastCheckin) {
        this.name = name;
        this.isHome = isHome;
        this.room = room;
        this.lastCheckin = lastCheckin;
    }

    public Roommate(Parcel in) {
        String[] data = new String[4];

        in.readStringArray(data);
        this.name = data[0];
        this.isHome = Boolean.parseBoolean(data[1]);
        this.room = data[2];
        this.lastCheckin = Long.valueOf(data[3]);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.name,
                Boolean.toString(this.isHome), this.room, Long.toString(this.lastCheckin)});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Roommate createFromParcel(Parcel in) {
            return new Roommate(in);
        }

        public Roommate[] newArray(int size) {
            return new Roommate[size];
        }
    };

    public static class RoommateComparator implements Comparator<Roommate> {
        @Override
        public int compare(Roommate o1, Roommate o2) {
            return o1.name.compareTo(o2.name);
        }
    }
}