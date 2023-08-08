package sgs.env.ecabsdriver.model;

public class DriverStatus {
    private String driverTripMasterId;
    private String status;
    private String otp;
    private String passengerTripMasterId;
    private String passengerTripStageId;
    private String address;
    private int dif;
    private String driverName;
    private String passengerId;
    private String paymentMode;
    public  Coupons coupons;

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public int getDif() {
        return dif;
    }

    public void setDif(int dif) {
        this.dif = dif;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassengerTripStageId() {
        return passengerTripStageId;
    }

    public void setPassengerTripStageId(String passengerTripStageId) {
        this.passengerTripStageId = passengerTripStageId;
    }

    private Location1 driverLocation;

    public String getDriverTripMasterId() {
        return driverTripMasterId;
    }

    public void setDriverTripMasterId(String driverTripMasterId) {
        this.driverTripMasterId = driverTripMasterId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Location1 getDriverLocation() {
        return driverLocation;
    }

    public void setDriverLocation(Location1 driverLocation) {
        this.driverLocation = driverLocation;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }


    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    @Override
    public String toString() {
        return "DriverStatus{" +
                "driverTripMasterId='" + driverTripMasterId + '\'' +
                ", status='" + status + '\'' +
                ", otp='" + otp + '\'' +
                ", passengerTripMasterId='" + passengerTripMasterId + '\'' +
                ", passengerTripStageId='" + passengerTripStageId + '\'' +
                ", driverLocation=" + driverLocation +
                '}';
    }

    public String getPassengerTripMasterId() {
        return passengerTripMasterId;
    }

    public void setPassengerTripMasterId(String passengerTripMasterId) {
        this.passengerTripMasterId = passengerTripMasterId;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public Coupons getCoupons() {
        return coupons;
    }

    public void setCoupons(Coupons coupons) {
        this.coupons = coupons;
    }
}
