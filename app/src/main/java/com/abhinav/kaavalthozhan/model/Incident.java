package com.abhinav.kaavalthozhan.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class Incident {
    private String timeStamp;
    private GeoPoint geoPoint;
    private String driverName;
    private String location;
    private String remarks;

    public Incident(String timeStamp, GeoPoint geoPoint, String driverName, String location, String remarks) {
        this.timeStamp = timeStamp;
        this.geoPoint = geoPoint;
        this.driverName = driverName;
        this.location = location;
        this.remarks = remarks;
    }

    public Incident() {
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
}
