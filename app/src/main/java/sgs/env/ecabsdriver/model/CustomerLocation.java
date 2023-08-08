package sgs.env.ecabsdriver.model;

public class CustomerLocation {
    private double latitude;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public CustomerLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public CustomerLocation() {
    }

    @Override
    public String toString() {
        return "CustomerLocation{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
