package sgs.env.ecabsdriver.model;

import com.google.android.gms.maps.model.LatLng;

public class DriverCurrentLocation {
    private double mDCurLat;
    private double mDCurLong;
    private String mDCurAddress;
    private String mDCurPlace;
    private int mCarLogo; // optional
    private LatLng mDCurLatLng;

    public LatLng getmDCurLatLng() {
        return mDCurLatLng;
    }

    public void setmDCurLatLng(LatLng mDCurLatLng) {
        this.mDCurLatLng = mDCurLatLng;
    }

    public double getmDCurLat() {
        return mDCurLat;
    }

    public void setmDCurLat(double mDCurLat) {
        this.mDCurLat = mDCurLat;
    }

    public double getmDCurLong() {
        return mDCurLong;
    }

    public void setmDCurLong(double mDCurLong) {
        this.mDCurLong = mDCurLong;
    }

    public String getmDCurAddress() {
        return mDCurAddress;
    }

    public void setmDCurAddress(String mDCurAddress) {
        this.mDCurAddress = mDCurAddress;
    }

    public String getmDCurPlace() {
        return mDCurPlace;
    }

    public void setmDCurPlace(String mDCurPlace) {
        this.mDCurPlace = mDCurPlace;
    }

    public int getmCarLogo() {
        return mCarLogo;
    }

    public void setmCarLogo(int mCarLogo) {
        this.mCarLogo = mCarLogo;
    }

    @Override
    public String toString() {
        return "DriverCurrentLocation{" +
                "mDCurLat=" + mDCurLat +
                ", mDCurLong=" + mDCurLong +
                ", mDCurAddress='" + mDCurAddress + '\'' +
                ", mDCurPlace=" + mDCurPlace +
                ", mCarLogo=" + mCarLogo +
                '}';
    }
}
