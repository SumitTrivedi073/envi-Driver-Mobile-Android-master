package sgs.env.ecabsdriver.model;

public class DriverTripStatus {
    private String driverId;
    private String passengerId;
    private String paymentStatus;
    private String paymentMode;
    private String status;
    private String driverTripMasterId;
    private String passengerTripMasterId;
    private String fromAddress;
    private String toAddress;
    private int vehicleId;
    private MongoLocation fromLocation;
    private MongoLocation toLocation;
    private String tripEndTime;
    private String tripStartTime;
    private String differenceInTripTime;
    private boolean scheduledTrip;
    private String scheduledAt;
    private  String specialRemarks ;

    public ArrivalAtSource getArrivalAtSource() {
        return arrivalAtSource;
    }

    public void setArrivalAtSource(ArrivalAtSource arrivalAtSource) {
        this.arrivalAtSource = arrivalAtSource;
    }

    private ArrivalAtSource arrivalAtSource;

    public TripDetails getArrivalAtDestination() {
        return arrivalAtDestination;
    }

    public void setArrivalAtDestination(TripDetails arrivalAtDestination) {
        this.arrivalAtDestination = arrivalAtDestination;
    }

    private TripDetails arrivalAtDestination;

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

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getDriverTripMasterId() {
        return driverTripMasterId;
    }

    public void setDriverTripMasterId(String driverTripMasterId) {
        this.driverTripMasterId = driverTripMasterId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public void setPassengerTripMasterId(String passengerTripMasterId) {
        this.passengerTripMasterId = passengerTripMasterId;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public String getStatus() {
        return status;
    }

    public String getPassengerTripMasterId() {
        return passengerTripMasterId;
    }

    public MongoLocation getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(MongoLocation fromLocation) {
        this.fromLocation = fromLocation;
    }

    public MongoLocation getToLocation() {
        return toLocation;
    }

    public void setToLocation(MongoLocation toLocation) {
        this.toLocation = toLocation;
    }

    public String getTripEndTime() {
        return tripEndTime;
    }

    public void setTripEndTime(String tripEndTime) {
        this.tripEndTime = tripEndTime;
    }

    public String getTripStartTime() {
        return tripStartTime;
    }

    public void setTripStartTime(String tripStartTime) {
        this.tripStartTime = tripStartTime;
    }

    public String getDifferenceInTripTime() {
        return differenceInTripTime;
    }

    public void setDifferenceInTripTime(String differenceInTripTime) {
        this.differenceInTripTime = differenceInTripTime;
    }
    public boolean isScheduledTrip() {
        return scheduledTrip;
    }

    public void setScheduledTrip(boolean scheduledTrip) {
        this.scheduledTrip = scheduledTrip;
    }

    public String getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(String scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public String getSpecialRemraks() {
        return specialRemarks ;
    }

    public void setSpecialRemraks(String specialRemarks ) {
        this.specialRemarks  = specialRemarks ;
    }

    @Override
    public String toString() {
        return "DriverTripStatus{" +
                "driverId='" + driverId + '\'' +
                ", passengerId='" + passengerId + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", paymentMode='" + paymentMode + '\'' +
                ", status='" + status + '\'' +
                ", driverTripMasterId='" + driverTripMasterId + '\'' +
                ", passengerTripMasterId='" + passengerTripMasterId + '\'' +
                ", fromAddress='" + fromAddress + '\'' +
                ", toAddress='" + toAddress + '\'' +
                ", vehicleId=" + vehicleId +
                ", fromLocation=" + fromLocation +
                ", toLocation=" + toLocation +
                ", tripEndTime='" + tripEndTime + '\'' +
                ", tripStartTime='" + tripStartTime + '\'' +
                ", differenceInTripTime='" + differenceInTripTime + '\'' +
                ", scheduledTrip=" + scheduledTrip +
                ", scheduledAt='" + scheduledAt + '\'' +
                ", specialRemarks ='" + specialRemarks  + '\'' +
                ", arrivalAtSource=" + arrivalAtSource +
                ", arrivalAtDestination=" + arrivalAtDestination +
                '}';
    }
}
