package sgs.env.ecabsdriver.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import sgs.env.ecabsdriver.activity.LoginActivity;
import sgs.env.ecabsdriver.data.local.ECabsDriverDbHelper;
import sgs.env.ecabsdriver.model.CustomerDestinationLocation;
import sgs.env.ecabsdriver.model.CustomerSourceLocation;
import sgs.env.ecabsdriver.model.DriverCurrentLocation;
import sgs.env.ecabsdriver.service.LocationService;
import sgs.env.ecabsdriver.service.RetrieveFirestoreData;

public class DriverAlgorithm extends AppCompatActivity {

    private DriverCurrentLocation mDriverLocation;
    private CustomerSourceLocation mCstSrcLocation;
    private CustomerDestinationLocation mCstDstLocation;
    private GoogleMap mMap;
    private static final String TAG = "DriverAlgorithm";

    private static final DriverAlgorithm mInstance = new DriverAlgorithm();

    public static DriverAlgorithm getInstance() {
        return mInstance;
    }

    public DriverCurrentLocation getmDriverLocation() {
        return mDriverLocation;
    }

    public void setmDriverLocation(DriverCurrentLocation mDriverLocation) {
        this.mDriverLocation = mDriverLocation;
    }

    public CustomerSourceLocation getmCstSrcLocation() {
        return mCstSrcLocation;
    }

    public void setmCstSrcLocation(CustomerSourceLocation mCstSrcLocation) {
        this.mCstSrcLocation = mCstSrcLocation;
    }

    public CustomerDestinationLocation getmCstDstLocation() {
        return mCstDstLocation;
    }

    public void setmCstDstLocation(CustomerDestinationLocation mCstDstLocation) {
        this.mCstDstLocation = mCstDstLocation;
    }

    public MarkerOptions addMarker(int drawable, Context mContext, LatLng sourceLatlang, String title, GoogleMap map) {
        mMap = map;
        Drawable circleDrawable = mContext.getResources().getDrawable(drawable);
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
        MarkerOptions sourceMarker = new MarkerOptions();
        sourceMarker.position(sourceLatlang);
        sourceMarker.title(title);
        sourceMarker.icon(markerIcon);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sourceLatlang, 16F));
        mMap.addMarker(sourceMarker);
        return sourceMarker;
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static String getAddressFromLatLang(Context context, double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses = null;
        String address = "";
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if (addresses != null && !addresses.isEmpty()) {
                address = addresses.get(0).getAddressLine(0);

            }
        } catch (IOException e) {
            Log.d(TAG, "getAddressFromLatLang: exce " + e.getMessage());
        }
        return address;
    }

    public static void logoutfromApp(Context context) {
        SharedPrefsHelper.getInstance().clearAllData();
        SharedPrefsHelper.getInstance().save(AppConstants.ACTIVE, false);
        SharedPrefsHelper.getInstance().save(AppConstants.DRIVER_LOGIN, false);
        SharedPrefsHelper.getInstance().save(AppConstants.GEN_MASTER_ID, true);
        new ECabsDriverDbHelper(context).delBreak();
        new ECabsDriverDbHelper(context).delSoc();
        context.stopService(new Intent(context, RetrieveFirestoreData.class));
        context.stopService(new Intent(context, LocationService.class));
        NotificationManagerCompat.from(context).cancelAll();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

}
