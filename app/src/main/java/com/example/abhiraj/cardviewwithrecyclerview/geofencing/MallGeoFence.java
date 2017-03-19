package com.example.abhiraj.cardviewwithrecyclerview.geofencing;

/**
 * Created by Abhiraj on 14-03-2017.
 */

public class MallGeoFence {

    private double latitude;
    private double longitude;
    private float radius;


    public MallGeoFence(double latitude, double longitude, float radius) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
