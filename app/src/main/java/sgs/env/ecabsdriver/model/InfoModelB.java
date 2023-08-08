package sgs.env.ecabsdriver.model;

public class InfoModelB {
    private String driverId;
    private Location driverLocation;
    private String driverName;
    private String driverPhoto;
    private int driverRating;
    private int vehicleId;
    private String vehicleNumber;
    private String driverPhoneNumber;

    public Location getDriverLocation() {
        return driverLocation;
    }

    public void setDriverLocation(Location driverLocation) {
        this.driverLocation = driverLocation;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public Location getLocation() {
        return driverLocation;
    }

    public void setLocation(Location location) {
        this.driverLocation = location;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhoto() {
        return driverPhoto;
    }

    public void setDriverPhoto(String driverPhoto) {
        this.driverPhoto = driverPhoto;
    }

    public int getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(int driverRating) {
        this.driverRating = driverRating;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getDriverPhoneNumber() {
        return driverPhoneNumber;
    }

    public void setDriverPhoneNumber(String driverPhoneNumber) {
        this.driverPhoneNumber = driverPhoneNumber;
    }

    @Override
    public String toString() {
        return "InfoModelB{" +
                "driverId='" + driverId + '\'' +
                ", driverLocation=" + driverLocation +
                ", driverName='" + driverName + '\'' +
                ", driverPhoto='" + driverPhoto + '\'' +
                ", driverRating=" + driverRating +
                ", vehicleId=" + vehicleId +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", driverPhoneNumber='" + driverPhoneNumber + '\'' +
                '}';
    }
}
