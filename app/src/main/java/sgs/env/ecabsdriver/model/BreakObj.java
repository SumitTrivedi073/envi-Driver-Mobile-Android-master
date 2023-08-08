package sgs.env.ecabsdriver.model;



public class BreakObj {
    private Location driverLocation;
    private String breakType;
    private String driverTripMasterId;

    public Location getDriverLocation() {
        return driverLocation;
    }

    public void setDriverLocation(Location driverLocation) {
        this.driverLocation = driverLocation;
    }

    public String getBreakType() {
        return breakType;
    }

    public void setBreakType(String breakType) {
        this.breakType = breakType;
    }

    public String getDriverTripMasterId() {
        return driverTripMasterId;
    }

    public void setDriverTripMasterId(String driverTripMasterId) {
        this.driverTripMasterId = driverTripMasterId;
    }

    @Override
    public String toString() {
        return "BreakObj{" +
                "driverLocation=" + driverLocation +
                ", breakType='" + breakType + '\'' +
                ", driverTripMasterId='" + driverTripMasterId + '\'' +
                '}';
    }
}
