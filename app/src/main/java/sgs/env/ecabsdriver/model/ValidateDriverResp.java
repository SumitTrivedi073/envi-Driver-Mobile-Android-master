package sgs.env.ecabsdriver.model;

public class ValidateDriverResp {
    private String tripStatus;
    private String dasStatus;
    private DriverTripStatus tripDetails;
    private DriverDetails driverDetails;
    private UserDetails userDetails;

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    @Override
    public String toString() {
        return "ValidateDriverResp{" +
                "tripStatus='" + tripStatus + '\'' +
                "dasStatus='" + dasStatus + '\'' +
                ", tripDetails=" + tripDetails +
                ", driverDetails=" + driverDetails +
                ", userDetails=" + userDetails +
                '}';
    }

    public String getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(String tripStatus) {
        this.tripStatus = tripStatus;
    }

    public DriverTripStatus getTripDetails() {
        return tripDetails;
    }

    public void setTripDetails(DriverTripStatus tripDetails) {
        this.tripDetails = tripDetails;
    }

    public DriverDetails getDriverDetails() {
        return driverDetails;
    }

    public void setDriverDetails(DriverDetails driverDetails) {
        this.driverDetails = driverDetails;
    }

    public String getDasStatus() {
        return dasStatus;
    }

    public void setDasStatus(String dasStatus) {
        this.dasStatus = dasStatus;
    }
}
