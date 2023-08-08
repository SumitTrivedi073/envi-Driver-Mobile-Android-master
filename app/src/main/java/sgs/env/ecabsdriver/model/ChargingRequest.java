package sgs.env.ecabsdriver.model;

import java.util.Date;

public class ChargingRequest {
    private String chargingRequestId;
    private String connectorId;
    private Integer carId;
    private String carRegsitrationNumber;
    private String socAtCharging;
    private String kwhCharged;
    private String kwhRequested;
    private String statusOfConnector;
    private String chargerId;
    private String otp;
    private Date createdDate;
    private String status;
    private String chargingAmount;
    private String driverId;
    private String driverPhone;
    private Boolean activeCharging;
    private String message;

    public String getChargingRequestId() {
        return chargingRequestId;
    }

    public void setChargingRequestId(String chargingRequestId) {
        this.chargingRequestId = chargingRequestId;
    }

    public String getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(String connectorId) {
        this.connectorId = connectorId;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public String getCarRegsitrationNumber() {
        return carRegsitrationNumber;
    }

    public void setCarRegsitrationNumber(String carRegsitrationNumber) {
        this.carRegsitrationNumber = carRegsitrationNumber;
    }

    public String getSocAtCharging() {
        return socAtCharging;
    }

    public void setSocAtCharging(String socAtCharging) {
        this.socAtCharging = socAtCharging;
    }

    public String getKwhCharged() {
        return kwhCharged;
    }

    public void setKwhCharged(String kwhCharged) {
        this.kwhCharged = kwhCharged;
    }

    public String getKwhRequested() {
        return kwhRequested;
    }

    public void setKwhRequested(String kwhRequested) {
        this.kwhRequested = kwhRequested;
    }

    public String getStatusOfConnector() {
        return statusOfConnector;
    }

    public void setStatusOfConnector(String statusOfConnector) {
        this.statusOfConnector = statusOfConnector;
    }

    public String getChargerId() {
        return chargerId;
    }

    public void setChargerId(String chargerId) {
        this.chargerId = chargerId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChargingAmount() {
        return chargingAmount;
    }

    public void setChargingAmount(String chargingAmount) {
        this.chargingAmount = chargingAmount;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public Boolean getActiveCharging() {
        return activeCharging;
    }

    public void setActiveCharging(Boolean activeCharging) {
        this.activeCharging = activeCharging;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ChargingRequest{" +
                "chargingRequestId='" + chargingRequestId + '\'' +
                ", connectorId='" + connectorId + '\'' +
                ", carId='" + carId + '\'' +
                ", carRegsitrationNumber='" + carRegsitrationNumber + '\'' +
                ", socAtCharging='" + socAtCharging + '\'' +
                ", kwhCharged='" + kwhCharged + '\'' +
                ", kwhRequested='" + kwhRequested + '\'' +
                ", statusOfConnector='" + statusOfConnector + '\'' +
                ", chargerId='" + chargerId + '\'' +
                ", otp='" + otp + '\'' +
                ", createdDate=" + createdDate +
                ", status='" + status + '\'' +
                ", chargingAmount='" + chargingAmount + '\'' +
                ", driverId='" + driverId + '\'' +
                ", driverPhone='" + driverPhone + '\'' +
                ", activeCharging=" + activeCharging +
                ", message='" + message + '\'' +
                '}';
    }
}
