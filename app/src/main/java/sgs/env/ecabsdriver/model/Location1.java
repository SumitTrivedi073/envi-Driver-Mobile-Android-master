package sgs.env.ecabsdriver.model;

public class Location1 {
    private String latitude;
    private String longitude;
    private String address;

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitide() {
        return longitude;
    }

    public void setLongitide(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Location1{" +
                "latitude='" + latitude + '\'' +
                "address='" + address + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }
}
