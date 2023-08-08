package sgs.env.ecabsdriver.model;

import com.google.android.gms.maps.model.LatLng;

public class CustomerSourceLocation {
    private double mCstSrcLat;
    private double mCstSrcLong;
    private String mCstSrcAdress;
    private String mCstSrcPlace;
    private int mCstSrcLogo;    // optional

    public double getmCstSrcLat() {
        return mCstSrcLat;
    }

    public void setmCstSrcLat(double mCstSrcLat) {
        this.mCstSrcLat = mCstSrcLat;
    }

    public double getmCstSrcLong() {
        return mCstSrcLong;
    }

    public void setmCstSrcLong(double mCstSrcLong) {
        this.mCstSrcLong = mCstSrcLong;
    }

    public String getmCstSrcAdress() {
        return mCstSrcAdress;
    }

    public void setmCstSrcAdress(String mCstSrcAdress) {
        this.mCstSrcAdress = mCstSrcAdress;
    }

    public String getmCstSrcPlace() {
        return mCstSrcPlace;
    }

    public void setmCstSrcPlace(String mCstSrcPlace) {
        this.mCstSrcPlace = mCstSrcPlace;
    }

    public int getmCstSrcLogo() {
        return mCstSrcLogo;
    }

    public void setmCstSrcLogo(int mCstSrcLogo) {
        this.mCstSrcLogo = mCstSrcLogo;
    }
}
