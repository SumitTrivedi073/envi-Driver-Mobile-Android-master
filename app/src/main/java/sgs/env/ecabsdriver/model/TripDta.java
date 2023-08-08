package sgs.env.ecabsdriver.model;

import java.util.Arrays;

public class TripDta {

    private String name;
    private String start_time;
    private String end_time;
    private String distance;
    private String paymentMode;
    private Price price;
    private String status;
    private double[] fromLocation;
    private double[] toLocation;
    private String fromAddress;
    private String toAddress;
    private String paymentStatus;
    private String passengerTripMasterId;
    private String driverTripMasterId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public double[] getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(double[] fromLocation) {
        this.fromLocation = fromLocation;
    }

    public double[] getToLocation() {
        return toLocation;
    }

    public void setToLocation(double[] toLocation) {
        this.toLocation = toLocation;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPassengerTripMasterId() {
        return passengerTripMasterId;
    }

    public void setPassengerTripMasterId(String passengerTripMasterId) {
        this.passengerTripMasterId = passengerTripMasterId;
    }

    public void setDriverTripMasterId(String driverTripMasterId) {
        this.driverTripMasterId = driverTripMasterId;
    }

    public String getDriverTripMasterId() {
        return driverTripMasterId;
    }

    @Override
    public String toString() {
        return "TripDta{" +
                "name='" + name + '\'' +
                ", start_time='" + start_time + '\'' +
                ", end_time='" + end_time + '\'' +
                ", distance='" + distance + '\'' +
                ", paymentMode='" + paymentMode + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                ", fromLocation=" + Arrays.toString(fromLocation) +
                ", toLocation=" + Arrays.toString(toLocation) +
                ", fromAddress='" + fromAddress + '\'' +
                ", toAddress='" + toAddress + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", passengerTripMasterId='" + passengerTripMasterId + '\'' +
                ", driverTripMasterId='" + driverTripMasterId + '\'' +
                '}';
    }


}
