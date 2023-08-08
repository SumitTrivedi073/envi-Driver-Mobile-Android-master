package sgs.env.ecabsdriver.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TripEndResponse extends GeneralResponse {

    private Distance distance;
    private String paymentMode;
    private Price price;
    private String customerMailId;
    private String fromAddress;
    private String toAddress;
    private String driverPhoto;
    private String driverName;
    private String tripStartTime;
    private String tripEndTime;
    private String differenceInTripTime;
    private TripDetails tripDetails;

    public TripDetails getTripDetails() {
        return tripDetails;
    }

    public void setTripDetails(TripDetails tripDetails) {
        this.tripDetails = tripDetails;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public String getCustomerMailId() {
        return customerMailId;
    }

    public void setCustomerMailId(String customerMailId) {
        this.customerMailId = customerMailId;
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

    public String getDriverPhoto() {
        return driverPhoto;
    }

    public void setDriverPhoto(String driverPhoto) {
        this.driverPhoto = driverPhoto;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getTripStartTime() {
        return tripStartTime;
    }

    public void setTripStartTime(String tripStartTime) {
        this.tripStartTime = tripStartTime;
    }

    public String getTripEndTime() {
        return tripEndTime;
    }

    public void setTripEndTime(String tripEndTime) {
        this.tripEndTime = tripEndTime;
    }

    public String getDifferenceInTripTime() {
        return differenceInTripTime;
    }

    public void setDifferenceInTripTime(String differenceInTripTime) {
        this.differenceInTripTime = differenceInTripTime;
    }

    public static class TripDetails {

        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("passengerTripMasterId")
        @Expose
        private String passengerTripMasterId;

        @SerializedName("scheduledTrip")
        @Expose
        private Boolean scheduledTrip;

        @SerializedName("scheduledAt")
        @Expose
        private String scheduledAt;

        @SerializedName("specialRemarks")
        @Expose
        private String specialRemarks;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPassengerTripMasterId() {
            return passengerTripMasterId;
        }

        public void setPassengerTripMasterId(String passengerTripMasterId) {
            this.passengerTripMasterId = passengerTripMasterId;
        }

        public Boolean getScheduledTrip() {
            return scheduledTrip;
        }

        public void setScheduledTrip(Boolean scheduledTrip) {
            this.scheduledTrip = scheduledTrip;
        }

        public String getScheduledAt() {
            return scheduledAt;
        }

        public void setScheduledAt(String scheduledAt) {
            this.scheduledAt = scheduledAt;
        }

        public String getSpecialRemarks() {
            return specialRemarks;
        }

        public void setSpecialRemarks(String specialRemarks) {
            this.specialRemarks = specialRemarks;
        }

        @NonNull
        @Override
        public String toString() {
            return "TripDetails{" +
                    "status=" + status +
                    ", passengerTripMasterId=" + passengerTripMasterId +
                    ", scheduledTrip=" + scheduledTrip +
                    ", scheduledAt=" + scheduledAt +
                    ", specialRemarks=" + specialRemarks +
                    '}';
        }

    }

    @NonNull
    @Override
    public String toString() {
        return "TripEndResponse{" +
                "distance=" + distance +
                ", price=" + price +
                ", fromAddress=" + fromAddress +
                ", toAddress=" + toAddress +
                ", tripDetails=" + tripDetails +
                '}';
    }
}
