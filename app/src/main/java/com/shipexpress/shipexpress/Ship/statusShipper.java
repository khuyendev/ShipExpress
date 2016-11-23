package com.shipexpress.shipexpress.Ship;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;
import com.shipexpress.shipexpress.Utility.sLocation;


import java.util.HashMap;

/**
 * Created by QuangCoi on 10/15/2016.
 */

public class statusShipper {
    boolean isOnline;
    boolean isAvailable;
    String nameShipper;
    String phoneNumber;
    sLocation location;
    HashMap<String,Object> timestampCreated = new HashMap<>();
    long TimeStamp;
    String UIDShipper;
    public statusShipper() {

    }

    public long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        TimeStamp = timeStamp;
    }

    public statusShipper(boolean isOnline, boolean isAvailable, String nameShipper, sLocation location, String UIDShipper, String phoneNumber) {
        this.isOnline = isOnline;
        this.isAvailable = isAvailable;
        this.nameShipper = nameShipper;
        this.location = location;
        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);

        this.timestampCreated = timestampNow;
        this.UIDShipper = UIDShipper;
        this.phoneNumber = phoneNumber;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
    public boolean isAvailable() {
        return isAvailable;
    }
    public boolean isOnline() {
        return isOnline;
    }
    public void setOnline(boolean online) {
        isOnline = online;
    }
    public String getNameShipper() {
        return nameShipper;
    }
    public void setNameShipper(String nameShipper) {
        this.nameShipper = nameShipper;
    }

    public sLocation getLocation() {
        return location;
    }

    public void setLocation(sLocation location) {
        this.location = location;
    }
    public String getUIDShipper() {
        return UIDShipper;
    }

    public void setUIDShipper(String UIDShipper) {
        this.UIDShipper = UIDShipper;
    }
    @Exclude
    public long getTimestampCreatedLong(){
        return (long)timestampCreated.get("timestamp");
    }


    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public void setTimestampCreated(HashMap<String, Object> timestampCreated) {
        this.timestampCreated = timestampCreated;
    }
    public void setAllValue(statusShipper allValue) {
        this.isOnline = allValue.isOnline;
        this.isAvailable = allValue.isAvailable;
        this.nameShipper = allValue.nameShipper;
        this.location = allValue.location;
        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;
        this.UIDShipper = allValue.UIDShipper;
        this.phoneNumber = allValue.phoneNumber;
    }
}
