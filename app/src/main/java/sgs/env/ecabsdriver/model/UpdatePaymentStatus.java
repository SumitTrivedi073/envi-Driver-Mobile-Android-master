package sgs.env.ecabsdriver.model;

import androidx.annotation.NonNull;

public class UpdatePaymentStatus {
    private String passengerTripMasterId;
    private String paymentStatus;
    private String driverTripMasterId;

    public String getPassengerTripMasterId() {
        return passengerTripMasterId;
    }

    public void setPassengerTripMasterId(String passengerTripMasterId) {
        this.passengerTripMasterId = passengerTripMasterId;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setDriverTripMasterId(String driverTripMasterId) {
        this.driverTripMasterId = driverTripMasterId;
    }

    public String getDriverTripMasterId() {
        return driverTripMasterId;
    }

    @NonNull
    @Override
    public String toString() {
        return "UpdatePaymentStatus{" +
                "passengerTripMasterId='" + passengerTripMasterId + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", driverTripMasterId='" + driverTripMasterId + '\'' +
                '}';
    }

}
