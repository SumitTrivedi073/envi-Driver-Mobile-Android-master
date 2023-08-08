package sgs.env.ecabsdriver.model;

public class CloseDay {
    private String driverTripMasterId;
    private Location driverLocation;
    private String  driverId;

    public String getDrierId() {
        return driverId;
    }

    public void setDrierId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverTripMasterId() {
        return driverTripMasterId;
    }

    public void setDriverTripMasterId(String driverTripMasterId) {
        this.driverTripMasterId = driverTripMasterId;
    }

    public Location getDriverLocation() {
        return driverLocation;
    }

    public void setDriverLocation(Location driverLocation) {
        this.driverLocation = driverLocation;
    }

    @Override
    public String toString() {
        return "CloseDay{" +
                "driverTripMasterId='" + driverTripMasterId + '\'' +
                ", driverLocation=" + driverLocation +
                '}';
    }
}
