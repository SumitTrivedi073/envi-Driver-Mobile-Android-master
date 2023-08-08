package sgs.env.ecabsdriver.model;

public class DriverDetails {
    private String name;
    private String gender;
    private String mailid;
    private String propic;
    private String phone;
    private String deviceId;
    private String FcmToken;
    private String id;

    @Override
    public String toString() {
        return "DriverDetails{" +
                "name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", mailid='" + mailid + '\'' +
                ", propic='" + propic + '\'' +
                ", phone='" + phone + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", FcmToken='" + FcmToken + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMailid() {
        return mailid;
    }

    public void setMailid(String mailid) {
        this.mailid = mailid;
    }

    public String getPropic() {
        return propic;
    }

    public void setPropic(String propic) {
        this.propic = propic;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
