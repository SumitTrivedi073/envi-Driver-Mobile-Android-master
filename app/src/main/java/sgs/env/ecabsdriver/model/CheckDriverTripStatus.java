package sgs.env.ecabsdriver.model;


public class CheckDriverTripStatus {
    private String phone;
    private String deviceId;

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

    public CheckDriverTripStatus(String phone, String deviceId) {
        this.phone = phone;
        this.deviceId = deviceId;
    }

    @Override
    public boolean equals( Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
