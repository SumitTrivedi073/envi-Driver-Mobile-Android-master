package sgs.env.ecabsdriver.callback;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Lenovo on 4/9/2018.
 */

public interface OnRideVerifyListner{
    void goToGoogleMaps(LatLng sourceLatLng, LatLng destinationLatLng);
}

