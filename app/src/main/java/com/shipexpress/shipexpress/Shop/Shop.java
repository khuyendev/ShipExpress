package com.shipexpress.shipexpress.Shop;

import com.shipexpress.shipexpress.Utility.sLocation;

/**
 * Created by QuangCoi on 10/28/2016.
 */

public class Shop {
    String emailShop;
    String nameShop;
    String addressShop;
    String phoneNumber;
    String detailShop;
    sLocation sLocation;
    public Shop() {

    }
    public Shop(String emailShop, String nameShop, String addressShop, String phoneNumber, String detailShop, sLocation sLocation) {
        this.emailShop = emailShop;
        this.nameShop = nameShop;
        this.addressShop = addressShop;
        this.phoneNumber = phoneNumber;
        this.detailShop = detailShop;
        this.sLocation = sLocation;
    }

    public String getNameShop() {
        return nameShop;
    }

    public void setNameShop(String nameShop) {
        this.nameShop = nameShop;
    }

    public String getAddressShop() {
        return addressShop;
    }

    public void setAddressShop(String addressShop) {
        this.addressShop = addressShop;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDetailShop() {
        return detailShop;
    }

    public void setDetailShop(String detailShop) {
        this.detailShop = detailShop;
    }



    public sLocation getsLocation() {
        return sLocation;
    }

    public void setsLocation(sLocation sLocation) {
        this.sLocation = sLocation;
    }

    public String getEmailShop() {
        return emailShop;
    }

    public void setEmailShop(String emailShop) {
        this.emailShop = emailShop;
    }


}

