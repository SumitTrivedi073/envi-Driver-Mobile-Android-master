package sgs.env.ecabsdriver.model;

public class TripInvoice {
    private String distance;
    private String paymentMode;
    private String customerMailId;
    private Price price;
    private String fromAddress;
    private String toAddress;
    private String driverPhoto;
    private String driverName;
    private String tripStartTime;
    private String tripEndTime;
    private String differenceInTripTime;
    private String passengerTripMasterId;
    private String driverTripMasterId;

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public String getdistance() {
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

    public String getDistance() {
        return distance;
    }

    public String getPassengerTripMasterId() {
        return passengerTripMasterId;
    }

    public void setPassengerTripMasterId(String passengerTripMasterId) {
        this.passengerTripMasterId = passengerTripMasterId;
    }

    public String getDriverTripMasterId() {
        return driverTripMasterId;
    }

    public void setDriverTripMasterId(String driverTripMasterId) {
        this.driverTripMasterId = driverTripMasterId;
    }

    @Override
    public String toString() {
        return "TripInvoice{" +
                "distance='" + distance + '\'' +
                ", paymentMode='" + paymentMode + '\'' +
                ", customerMailId='" + customerMailId + '\'' +
                ", price=" + price +
                ", fromAddress='" + fromAddress + '\'' +
                ", toAddress='" + toAddress + '\'' +
                ", driverPhoto='" + driverPhoto + '\'' +
                ", driverName='" + driverName + '\'' +
                ", tripStartTime='" + tripStartTime + '\'' +
                ", tripEndTime='" + tripEndTime + '\'' +
                ", differenceInTripTime='" + differenceInTripTime + '\'' +
                ", passengerTripMasterId='" + passengerTripMasterId + '\'' +
                ", driverTripMasterId='" + driverTripMasterId + '\'' +
                '}';
    }
}
