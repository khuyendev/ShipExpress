package com.shipexpress.shipexpress.Order;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;
import com.shipexpress.shipexpress.Utility.sLocation;

import java.util.HashMap;

/**
 * Created by QuangCoi on 10/5/2016.
 */

public class DetailOrder {
    String Name;
    String key;
    String keyship;
    toAddress toAddress;
    int price;
    int freight;
    HashMap<String, Object> timestampCreated;
    sLocation sLocation;
    String statusOrder;
    String UIDShop,UIDShip;
    long timeStamp;

// thằng ship có thể biết shop nào đăng


    public DetailOrder() {
    }

    public DetailOrder(String name, String key, String keyship, com.shipexpress.shipexpress.Order.toAddress toAddress, int price, int freight, HashMap<String, Object> timestampCreated, com.shipexpress.shipexpress.Utility.sLocation sLocation, String statusOrder, String UIDShop, String UIDShip) {
        Name = name;
        this.key = key;
        this.keyship = keyship;
        this.toAddress = toAddress;
        this.price = price;
        this.freight = freight;
        this.timestampCreated = timestampCreated;
        this.sLocation = sLocation;
        this.statusOrder = statusOrder;
        this.UIDShop = UIDShop;
        this.UIDShip = UIDShip;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setKeyship(String keyship) {
        this.keyship = keyship;
    }

    public void setTimestampCreated(HashMap<String, Object> timestampCreated) {
        this.timestampCreated = timestampCreated;
    }

    public String getKeyship() {
        return keyship;
    }

    public DetailOrder(String name, toAddress toAddress, int price, int freight, sLocation sLocation, String statusOrder, String UIDShop, String UIDShip) {
        this.Name = name;
        this.toAddress = toAddress;
        this.price = price;
        this.freight = freight;
        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;
        this.sLocation = sLocation;
        this.statusOrder = statusOrder;
        this.UIDShop = UIDShop;
        this.UIDShip = UIDShip;
    }

    public String getName() {
        return Name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        Name = name;
    }

    public toAddress getToAddress() {
        return toAddress;
    }

    public void setToAddress(toAddress toAddress) {
        this.toAddress = toAddress;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getFreight() {
        return freight;
    }

    public void setFreight(int freight) {
        this.freight = freight;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public void setTimestampCreated() {
        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;
    }

    public sLocation getsLocation() {
        return sLocation;
    }

    public void setsLocation(sLocation sLocation) {
        this.sLocation = sLocation;
    }

    public String getStatusOrder() {
        return statusOrder;
    }

    public void setStatusOrder(String statusOrder) {
        this.statusOrder = statusOrder;
    }

    public String getUIDShop() {
        return UIDShop;
    }

    public void setUIDShop(String UIDShop) {
        this.UIDShop = UIDShop;
    }

    public String getUIDShip() {
        return UIDShip;
    }

    public void setUIDShip(String UIDShip) {
        this.UIDShip = UIDShip;
    }
    @Exclude
    public long getTimestampCreatedLong(){
        return (long)timestampCreated.get("timestamp");
    }
}
