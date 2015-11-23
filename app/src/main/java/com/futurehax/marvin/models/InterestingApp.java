package com.futurehax.marvin.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;


import com.futurehax.marvin.R;

import java.util.ArrayList;
import java.util.Arrays;

public class InterestingApp implements Parcelable {
    private String appName, packageName, groupIds = "";
    int hue = -1, length;
    
    boolean active;

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(packageName);
        dest.writeString(appName);

        dest.writeInt(hue);
        dest.writeInt(length);
        dest.writeString(groupIds);
        dest.writeInt(isActive() ? 1 : 0);

    }

    /* Parcelable interface implementation */
    public static final Creator<InterestingApp> CREATOR = new Creator<InterestingApp>() {

        @Override
        public InterestingApp createFromParcel(final Parcel in) {
            return new InterestingApp(in);
        }

        @Override
        public InterestingApp[] newArray(final int size) {
            return new InterestingApp[size];
        }
    };
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    private void readFromParcel(final Parcel in) {
        packageName = in.readString();
        appName = in.readString();

        hue = in.readInt();
        length = in.readInt();
        groupIds = in.readString();
        active = in.readInt() == 1;
    }

    public InterestingApp(final Parcel in) {
        readFromParcel(in);
    }

	public InterestingApp(String packageName, Boolean active, Context c) {
		super();
        setPackageName(packageName);
        setActive(active);
	}

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getAppName() {
		return appName;
	}

	public int getHue() {
        return hue == -1 ? R.color.colorAccent : hue;
	}

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
		return length;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

    public String getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(String groupIds) {
        this.groupIds = groupIds;
    }

    public void setHue(int hue) {
		this.hue = hue;
	}

	public void setName(String appName) {
		this.appName = appName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

    public void handleGroupId(String key, boolean newValue) {
        ArrayList<String> ids = getGroupIdList();
        
        if (ids.contains(key)) {
            if (!newValue) {
                ids.remove(key+":");
            }
        } else {
            if (newValue) {
                ids.add(key+":");
            }
        }
        
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        for (String id : ids) {
            sb.append(id);
            if (pos > 0) {
                sb.append(":");
            }
            pos++;
        }
        setGroupIds(sb.toString());
    }

    public ArrayList<String> getGroupIdList() {
        return new ArrayList<>(Arrays.asList(getGroupIds().split(":")));
    }
}