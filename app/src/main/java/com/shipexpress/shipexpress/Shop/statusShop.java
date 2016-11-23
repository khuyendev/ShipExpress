package com.shipexpress.shipexpress.Shop;


import java.util.List;

import com.shipexpress.shipexpress.Order.DetailOrder;
import com.shipexpress.shipexpress.Utility.sLocation;
/**
 * Created by QuangCoi on 10/16/2016.
 */

public class statusShop {
    String nameShop;
    String UID;
    sLocation latLng;
    boolean isAvableOrder;
    List<DetailOrder> detailOrders;

    public statusShop() {
    }

    public String getNameShop() {
        return nameShop;
    }

    public void setNameShop(String nameShop) {
        this.nameShop = nameShop;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public sLocation getLatLng() {
        return latLng;
    }

    public void setLatLng(sLocation latLng) {
        this.latLng = latLng;
    }

    public boolean isAvableOrder() {
        return isAvableOrder;
    }

    public void setAvableOrder(boolean avableOrder) {
        isAvableOrder = avableOrder;
    }

    public List<DetailOrder> getDetailOrders() {
        return detailOrders;
    }

    public void setDetailOrders(List<DetailOrder> detailOrders) {
        this.detailOrders = detailOrders;
    }

    public statusShop(String nameShop, String UID, sLocation latLng, boolean isAvableOrder, List<DetailOrder> detailOrders) {
        this.nameShop = nameShop;
        this.UID = UID;
        this.latLng = latLng;
        this.isAvableOrder = isAvableOrder;
        this.detailOrders = detailOrders;
    }
}
