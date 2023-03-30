package com.urban.clone.model;

public class ModelFinalService {

    String serviceID,date,time;
    ModelVehicle vehicle;
    ModelLocation location;
    ModelPayment payment;

    public String getServiceID() {
        return serviceID;
    }

    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ModelVehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(ModelVehicle vehicle) {
        this.vehicle = vehicle;
    }

    public ModelLocation getLocation() {
        return location;
    }

    public void setLocation(ModelLocation location) {
        this.location = location;
    }

    public ModelPayment getPayment() {
        return payment;
    }

    public void setPayment(ModelPayment payment) {
        this.payment = payment;
    }
}
