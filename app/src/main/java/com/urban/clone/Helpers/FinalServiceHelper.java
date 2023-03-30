package com.urban.clone.Helpers;

import com.urban.clone.model.ModelLocation;
import com.urban.clone.model.ModelPayment;
import com.urban.clone.model.ModelVehicle;

public class FinalServiceHelper {

    String serviceID,date,time,phone;
    VehicleHelper vehicle;
    LocationHelper location;
    PaymentHelper payment;


    public FinalServiceHelper(String serviceID,String phone, String date, String time, VehicleHelper vehicle, LocationHelper location, PaymentHelper payment) {
        this.serviceID = serviceID;
        this.date = date;
        this.time = time;
        this.vehicle = vehicle;
        this.location = location;
        this.payment = payment;
        this.phone = phone;

    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

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

    public VehicleHelper getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleHelper vehicle) {
        this.vehicle = vehicle;
    }

    public LocationHelper getlocation() {
        return location;
    }

    public void setlocation(LocationHelper location) {
        this.location = location;
    }

    public PaymentHelper getPayment() {
        return payment;
    }

    public void setPayment(PaymentHelper payment) {
        this.payment = payment;
    }
}
