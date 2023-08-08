package sgs.env.ecabsdriver.presenter;

import android.graphics.Bitmap;

public class QRCode_Detail {

    String Image,passangerTripMasterID;
    double totalfare;

    public double getTotalfare() {
        return totalfare;
    }

    public void setTotalfare(double totalfare) {
        this.totalfare = totalfare;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getPassangerTripMasterID() {
        return passangerTripMasterID;
    }

    public void setPassangerTripMasterID(String passangerTripMasterID) {
        this.passangerTripMasterID = passangerTripMasterID;
    }
}
