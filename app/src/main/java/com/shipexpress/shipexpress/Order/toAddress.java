package com.shipexpress.shipexpress.Order;

import com.shipexpress.shipexpress.Utility.sLocation;

/**
 * Created by QuangCoi on 10/5/2016.
 */

public class toAddress {
    String nameAddress;
    String  nameReceived;
    int phoneNumber;
    sLocation sLocation;
    String note;

    public String getNameAddress() {
        return nameAddress;
    }

    public void setNameAddress(String nameAddress) {
        this.nameAddress = nameAddress;
    }

    public String getNameReceived() {
        return nameReceived;
    }

    public void setNameReceived(String nameReceived) {
        this.nameReceived = nameReceived;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public sLocation getsLocation() {
        return sLocation;
    }

    public void setsLocation(sLocation sLocation) {
        this.sLocation = sLocation;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public toAddress() {

    }

    public toAddress(String nameAddress, String nameReceived, int phoneNumber,sLocation sLocation, String note) {

        this.nameAddress = nameAddress;
        this.nameReceived = nameReceived;
        this.phoneNumber = phoneNumber;
        this.sLocation = sLocation;
        this.note = note;
    }
}
