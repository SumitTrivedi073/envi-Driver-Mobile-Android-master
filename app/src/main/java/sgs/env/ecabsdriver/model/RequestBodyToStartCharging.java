package sgs.env.ecabsdriver.model;

public class RequestBodyToStartCharging {
    private String otp;
    private String driverName;
    private String carRegistrationNumber;
    private String driverMobileNumber;
    private String socAtCharging;
    private String connectorId;
    private String chargingRequestId;
    private String message;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getCarRegistrationNumber() {
        return carRegistrationNumber;
    }

    public void setCarRegistrationNumber(String carRegistrationNumber) {
        this.carRegistrationNumber = carRegistrationNumber;
    }

    public String getDriverMobileNumber() {
        return driverMobileNumber;
    }

    public void setDriverMobileNumber(String driverMobileNumber) {
        this.driverMobileNumber = driverMobileNumber;
    }

    public String getSocAtCharging() {
        return socAtCharging;
    }

    public void setSocAtCharging(String socAtCharging) {
        this.socAtCharging = socAtCharging;
    }

    public String getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(String connectorId) {
        this.connectorId = connectorId;
    }

    public String getChargingRequestId() {
        return chargingRequestId;
    }

    public void setChargingRequestId(String chargingRequestId) {
        this.chargingRequestId = chargingRequestId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "RequestBodyToStartCharging{" +
                "otp='" + otp + '\'' +
                ", driverName='" + driverName + '\'' +
                ", carRegistrationNumber='" + carRegistrationNumber + '\'' +
                ", driverMobileNumber='" + driverMobileNumber + '\'' +
                ", socAtCharging='" + socAtCharging + '\'' +
                ", connectorId='" + connectorId + '\'' +
                ", chargingRequestId='" + chargingRequestId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
