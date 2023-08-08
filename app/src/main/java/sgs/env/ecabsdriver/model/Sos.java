package sgs.env.ecabsdriver.model;

public class Sos {

    private String driverTripmasterId;
    private String id;
    private String name;
    private String phone;
    private String vehicleId;
    private String userId;
    private LocationPoints location;

    public String getDriverTripmasterId() {
        return driverTripmasterId;
    }

    public void setDriverTripmasterId(String driverTripmasterId) {
        this.driverTripmasterId = driverTripmasterId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocationPoints getLocation() {
        return location;
    }

    public void setLocation(LocationPoints location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Sos{" +
                "driverTripmasterId='" + driverTripmasterId + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", userId='" + userId + '\'' +
                ", location=" + location +
                '}';
    }
}
