package sgs.env.ecabsdriver.model;

public class FirebaseDriverStatus {
    private Boolean logout;
    private String ChargingStatus;
    private String driverId;

    public FirebaseDriverStatus(){

    }

    public Boolean getLogout() {
        return logout;
    }

    public FirebaseDriverStatus(Boolean logout, String chargingStatus, String driverId) {
        this.logout = logout;
        ChargingStatus = chargingStatus;
        this.driverId = driverId;
    }

    public void setLogout(Boolean logout) {
        this.logout = logout;
    }

    public String getChargingStatus() {
        return ChargingStatus;
    }

    public void setChargingStatus(String chargingStatus) {
        ChargingStatus = chargingStatus;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    @Override
    public String toString() {
        return "FirebaseDriverStatus{" +
                "logout=" + logout +
                ", ChargingStatus='" + ChargingStatus + '\'' +
                ", driverId='" + driverId + '\'' +
                '}';
    }
}
