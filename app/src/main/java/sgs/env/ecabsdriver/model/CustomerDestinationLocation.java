package sgs.env.ecabsdriver.model;

import com.google.android.gms.maps.model.LatLng;

public class CustomerDestinationLocation {
    private double mCstDstLat;
    private double mCstDstLong;
    private String mCstDstAdress;
    private String mCstDstPlace;
    private int mCstDstLogo;    // optional

    public double getmCstDstLat() {
        return mCstDstLat;
    }

    public void setmCstDstLat(double mCstDstLat) {
        this.mCstDstLat = mCstDstLat;
    }

    public double getmCstDstLong() {
        return mCstDstLong;
    }

    public void setmCstDstLong(double mCstDstLong) {
        this.mCstDstLong = mCstDstLong;
    }

    public String getmCstDstAdress() {
        return mCstDstAdress;
    }

    public void setmCstDstAdress(String mCstDstAdress) {
        this.mCstDstAdress = mCstDstAdress;
    }

    public String getmCstDstPlace() {
        return mCstDstPlace;
    }

    public void setmCstDstPlace(String mCstDstPlace) {
        this.mCstDstPlace = mCstDstPlace;
    }

    public int getmCstDstLogo() {
        return mCstDstLogo;
    }

    public void setmCstDstLogo(int mCstDstLogo) {
        this.mCstDstLogo = mCstDstLogo;
    }

    @Override
    public String toString() {
        return "CustomerDestinationLocation{" +
                "mCstDstLat=" + mCstDstLat +
                ", mCstDstLong=" + mCstDstLong +
                ", mCstDstAdress='" + mCstDstAdress + '\'' +
                ", mCstDstPlace='" + mCstDstPlace + '\'' +
                ", mCstDstLogo=" + mCstDstLogo +
                '}';
    }
}
