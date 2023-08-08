package sgs.env.ecabsdriver.model;

public class DrivMstIDContent {
    private String driverTripMasterId;
    private String status;

    public String getDriverTripmasterId() {
        return driverTripMasterId;
    }

    public void setDriverTripmasterId(String driverTripmasterId) {
        this.driverTripMasterId = driverTripmasterId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "DrivMstIDContent{" +
                "driverTripmasterId='" + driverTripMasterId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
