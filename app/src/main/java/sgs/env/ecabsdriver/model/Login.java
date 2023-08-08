package sgs.env.ecabsdriver.model;

public class Login {

    private String countrycode;
    private String phone;
    private String FcmToken;
    private String deviceId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getFcmToken() {
        return FcmToken;
    }

    public void setFcmToken(String fcmToken) {
        FcmToken = fcmToken;
    }

    public String getCountrycode() {
        return countrycode;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Login{" +
                "countrycode='" + countrycode + '\'' +
                ", phone='" + phone + '\'' +
                ", FcmToken='" + FcmToken + '\'' +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }
}
