package com.shipexpress.shipexpress.Utility;



/**
 * Created by QuangCoi on 10/5/2016.
 */

public class sLocation {
    double lat, lng;
    double latCurrentLocation, lngCurrentLocation;

    public sLocation(double lat, double lng, double latCurrentLocation, double lngCurrentLocation) {
        this.lat = lat;
        this.lng = lng;
        this.latCurrentLocation = latCurrentLocation;
        this.lngCurrentLocation = lngCurrentLocation;
    }

    public sLocation() {
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLatCurrentLocation() {
        return latCurrentLocation;
    }

    public void setLatCurrentLocation(double latCurrentLocation) {
        this.latCurrentLocation = latCurrentLocation;
    }

    public double getLngCurrentLocation() {
        return lngCurrentLocation;
    }

    public void setLngCurrentLocation(double lngCurrentLocation) {
        this.lngCurrentLocation = lngCurrentLocation;
    }
}
