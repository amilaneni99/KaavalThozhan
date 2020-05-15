package com.abhinav.kaavalthozhan.model;

import java.util.List;

public class Vehicle {
    private String registrationNumber;
    private String type;
    private int offenses;
    private List<Incident> incidents;

    public Vehicle(String registrationNumber, String type, int offenses, List<Incident> incidents) {
        this.registrationNumber = registrationNumber;
        this.type = type;
        this.offenses = offenses;
        this.incidents = incidents;
    }

    public Vehicle() {
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getOffenses() {
        return offenses;
    }

    public void setOffenses(int offenses) {
        this.offenses = offenses;
    }

    public List<Incident> getIncidents() {
        return incidents;
    }

    public void setIncidents(List<Incident> incidents) {
        this.incidents = incidents;
    }
}
