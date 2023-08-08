package sgs.env.ecabsdriver.model;

public class UpdateLoc {
    private Location1 driverLocation;
    private String driverTripMasterId;
    private int vehicleId;

    public Location1 getDriverLocation() {
        return driverLocation;
    }

    public void setDriverLocation(Location1 driverLocation) {
        this.driverLocation = driverLocation;
    }

    public String getDriverTripMasterId() {
        return driverTripMasterId;
    }

    public void setDriverTripMasterId(String driverTripMasterId) {
        this.driverTripMasterId = driverTripMasterId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    @Override
    public String toString() {
        return "UpdateLoc{" +
                "driverLocation=" + driverLocation +
                ", driverTripMasterId='" + driverTripMasterId + '\'' +
                ", vehicleId=" + vehicleId +
                '}';
    }
}
