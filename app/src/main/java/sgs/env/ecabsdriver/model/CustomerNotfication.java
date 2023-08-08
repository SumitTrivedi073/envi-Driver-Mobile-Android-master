package sgs.env.ecabsdriver.model;

import java.util.Date;

public class CustomerNotfication {

   private String userId;
   private String name;
   private String countrycode;
   private String phone;
   private CustomerLocation fromLocation;
   private CustomerLocation toLocation;
   private String passengerTripMasterId;
   private int dif;
    private String createdDate;
    public int getDif() {
        return dif;
    }

    public void setDif(int dif) {
        this.dif = dif;
    }

    // for db backup parmeters if driver closes the app forcefully
    private String driverPage;
    private String rideStatus;

    public String getPassengerTripMasterId() {
        return passengerTripMasterId;
    }

    public void setPassengerTripMasterId(String passengerTripMasterId) {
        this.passengerTripMasterId = passengerTripMasterId;
    }

    public String getDriverPage() {
        return driverPage;
    }

    public void setDriverPage(String driverPage) {
        this.driverPage = driverPage;
    }

    public String getRideStatus() {
        return rideStatus;
    }

    public void setRideStatus(String rideStatus) {
        this.rideStatus = rideStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountrycode() {
        return countrycode;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public CustomerLocation getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(CustomerLocation fromLocation) {
        this.fromLocation = fromLocation;
    }

    public CustomerLocation getToLocation() {
        return toLocation;
    }

    public void setToLocation(CustomerLocation toLocation) {
        this.toLocation = toLocation;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "CustomerNotfication{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", countrycode='" + countrycode + '\'' +
                ", phone='" + phone + '\'' +
                ", fromLocation=" + fromLocation +
                ", toLocation=" + toLocation +
                ", passengerTripMasterId='" + passengerTripMasterId + '\'' +
                ", dif=" + dif +
                ", createdDate=" + createdDate +
                ", driverPage='" + driverPage + '\'' +
                ", rideStatus='" + rideStatus + '\'' +
                '}';
    }
}
