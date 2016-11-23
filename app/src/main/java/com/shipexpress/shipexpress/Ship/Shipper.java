package com.shipexpress.shipexpress.Ship;


import com.shipexpress.shipexpress.Utility.sLocation;

/**
 * Created by QuangCoi on 10/5/2016.
 */

public class Shipper {
    String emailShipper;
    String nameShipper;
    String addressShipper;
    String phoneNumber;
    String detailShipper;
    sLocation sLocation;

    public Shipper(String emailShipper, String nameShipper, String addressShipper, String phoneNumber, String detailShipper, com.shipexpress.shipexpress.Utility.sLocation sLocation) {
        this.emailShipper = emailShipper;
        this.nameShipper = nameShipper;
        this.addressShipper = addressShipper;
        this.phoneNumber = phoneNumber;
        this.detailShipper = detailShipper;
        this.sLocation = sLocation;
    }

    public Shipper() {
    }

    public String getEmailShipper() {
        return emailShipper;
    }

    public void setEmailShipper(String emailShipper) {
        this.emailShipper = emailShipper;
    }

    public String getNameShipper() {
        return nameShipper;
    }

    public void setNameShipper(String nameShipper) {
        this.nameShipper = nameShipper;
    }

    public String getAddressShipper() {
        return addressShipper;
    }

    public void setAddressShipper(String addressShipper) {
        this.addressShipper = addressShipper;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDetailShipper() {
        return detailShipper;
    }

    public void setDetailShipper(String detailShipper) {
        this.detailShipper = detailShipper;
    }

    public com.shipexpress.shipexpress.Utility.sLocation getsLocation() {
        return sLocation;
    }

    public void setsLocation(com.shipexpress.shipexpress.Utility.sLocation sLocation) {
        this.sLocation = sLocation;
    }
}
