package sgs.env.ecabsdriver.service;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.model.DirectionsResult;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.activity.HomeActivity;
import sgs.env.ecabsdriver.activity.SplashScreen;
import sgs.env.ecabsdriver.interfce.RegisterService;
import sgs.env.ecabsdriver.model.LocResponse;
import sgs.env.ecabsdriver.model.Location1;
import sgs.env.ecabsdriver.model.UpdateLoc;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.PermissionUtils;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private static final String TAG = "LocationService";

    public static final String NOTIFICATION_CHANNEL_ID = "1001";

    public int METER_DISTANCE = 20, MinSoc = 20;

    public int TIME_INTERVAL = 30 * 1000;

    private LocationRequest mLocationRequest;

    private GoogleApiClient mGoogleApiClient;

    public static boolean isServiceRunning;

    private SharedPrefsHelper sharedPrefsHelper;

    private static String mstId, TripStatus;

    private int soc;

    HomeActivity homeActivity;

    DocumentReference documentReference;

    @Override
    public void onCreate() {
        super.onCreate();
        isServiceRunning = true;

        buildGoogleApiClient();
        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        prepareForegroundNotification();

        homeActivity = HomeActivity.instance;
    }


    private void prepareForegroundNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Location Service Channel",
                            NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }

        final int flag = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;
        Intent notificationIntent = new Intent(this, SplashScreen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1234, notificationIntent, flag);

        Notification notification =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).setContentTitle("Location").setContentText(
                        "Location fetch running ").setSmallIcon(R.mipmap.ic_launcher_round).setContentIntent(pendingIntent).build();
        startForeground(111, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceRunning = false;

        Log.i(TAG, "onDestroy");
        //stopLocationUpdate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(SafetyNet.API).addOnConnectionFailedListener(
                this).addConnectionCallbacks(this).addApi(LocationServices.API).build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "onConnected" + bundle);
        boolean granted =
                PermissionUtils.checkLocationPermission(this);  // runtimePermis is granted
        if (granted) {
            @SuppressLint("MissingPermission") Location l =
                    FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (l != null) {
                Log.i(TAG, "lat " + l.getLatitude());
                Log.i(TAG, "lng " + l.getLongitude());
                startLocationUpdate();
                updateDLocationAPI(l);
            }

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended " + i);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        initLocationRequest();

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    // updating the location to server...
    public void updateDLocationAPI(Location location) {

        Location1 location1 = new Location1();
        location1.setLatitude(String.valueOf(location.getLatitude()));
        location1.setLongitide(String.valueOf(location.getLongitude()));
        UpdateLoc updateLoc = new UpdateLoc();
        updateLoc.setDriverLocation(location1);
        //not sending driver trip master ID because if it's not lies in db so location not updated
        //updateLoc.setDriverTripMasterId("00");
        updateLoc.setVehicleId(SharedPrefsHelper.getInstance().get(AppConstants.DRV_VEH_ID, 1));
        RegisterService updateLocation = ECabsApp.getRetrofit().create(RegisterService.class);
        final String token = SharedPrefsHelper.getInstance().get(AppConstants.KEY_JWT_TOKEN, "");

        retrofit2.Call<LocResponse> loginResponseCall =
                updateLocation.updateLocation(token, updateLoc);
        Log.d(TAG, "LocationUpdateAPI: url" + loginResponseCall.request().url());
        loginResponseCall.enqueue(new Callback<LocResponse>() {
            @Override
            public void onResponse(@NonNull Call<LocResponse> call,
                                   @NonNull Response<LocResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    SharedPrefsHelper.getInstance().save(AppConstants.LAST_KNOW_LATITUDE,
                            String.valueOf(location.getLatitude()));
                    SharedPrefsHelper.getInstance().save(AppConstants.LAST_KNOWN_LONGITUDE,
                            String.valueOf(location.getLongitude()));

                    soc = response.body().getVehicle().getSoc();
                    //	SocValues socValues = dbHelper.getSoc();
                    sharedPrefsHelper.save(AppConstants.LATEST_SOC, String.valueOf(soc));

                    if (sharedPrefsHelper.get(AppConstants.MinSoc) != null) {
                        if (!sharedPrefsHelper.get(AppConstants.MinSoc).toString().isEmpty()) {
                            MinSoc = Integer.parseInt(
                                    sharedPrefsHelper.get(AppConstants.MinSoc));
                        }
                    }
                    if (soc <= MinSoc) {
                        if (homeActivity != null) {
                            homeActivity.showAlertResponse("Please go to charging station, Car battery is low! ");
                        }
                    }

                } else {
                    if (homeActivity != null) {
                        homeActivity.showAlertResponse("Your Location is not updated properly, Please check your Internet Connection");
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<LocResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "LocationUpdateonFailure: " + t.getMessage());
                if (homeActivity != null) {
                    homeActivity.showAlertResponse("Your Location is not updated properly, Please check your Internet Connection");
                }
            }
        });

    }

    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        if (sharedPrefsHelper.get(AppConstants.searchFrequency,30) != null && !sharedPrefsHelper.get(
                AppConstants.searchFrequency,30).toString().isEmpty()) {
            TIME_INTERVAL = sharedPrefsHelper.get(AppConstants.searchFrequency,30)* 1000;
        }
        mLocationRequest.setInterval(TIME_INTERVAL);
        mLocationRequest.setFastestInterval(TIME_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        boolean active = sharedPrefsHelper.get(AppConstants.ACTIVE, false);
        boolean inBreak = sharedPrefsHelper.get(AppConstants.IN_BREAK, false);
        if (sharedPrefsHelper.get(AppConstants.TripStatus) != null && !sharedPrefsHelper.get(AppConstants.TripStatus).toString().isEmpty()) {
            TripStatus = sharedPrefsHelper.get(AppConstants.TripStatus);
        }else {
            TripStatus = "noPrevTripsAvailable";
        }
        float distanceInMeters = 1000;
        if (active && !inBreak && ECabsApp.isNetworkAvailable(this)) {

            if (sharedPrefsHelper.get(AppConstants.LAST_KNOW_LATITUDE) != null) {
                Location loc1 = new Location("");
                loc1.setLatitude(
                        Double.parseDouble(sharedPrefsHelper.get(AppConstants.LAST_KNOW_LATITUDE)));
                loc1.setLongitude(Double.parseDouble(
                        sharedPrefsHelper.get(AppConstants.LAST_KNOWN_LONGITUDE)));
                Location loc2 = new Location("");
                loc2.setLatitude(location.getLatitude());
                loc2.setLongitude(location.getLongitude());
                 distanceInMeters = loc1.distanceTo(loc2);
                if (sharedPrefsHelper.get(AppConstants.seacrhMinDistance,20) != null && !sharedPrefsHelper.get(
                        AppConstants.seacrhMinDistance,20).toString().isEmpty()) {
                    METER_DISTANCE = sharedPrefsHelper.get(AppConstants.seacrhMinDistance,20);
                }

            }
            if (distanceInMeters >= METER_DISTANCE) {
                updateDLocationAPI(location);
            }

                if (TripStatus.equals(AppConstants.TRIP_ALLOTTED)||distanceInMeters>=METER_DISTANCE) {
                    LocationUpdateonFirestore(location);
            }

        } else {
            if (homeActivity != null) {
                homeActivity.showAlertResponse("Please check your internet connection!");
            } else {
                Toast.makeText(getApplicationContext(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void LocationUpdateonFirestore(Location location) {

        System.out.println("locationUpdateFirebase" + location.toString());

        if (SharedPrefsHelper.getInstance().get(AppConstants.PASS_MST_ID, "") != null &&
                    !SharedPrefsHelper.getInstance().get(AppConstants.PASS_MST_ID, "").isEmpty()) {

                Map<String, Object> driverLocation = new HashMap<>();
                driverLocation.put("latitude", location.getLatitude());
                driverLocation.put("longitude", location.getLongitude());
                documentReference = FirebaseFirestore.getInstance()
                        .collection("trips").document("passengerTripMasterId:" + SharedPrefsHelper.getInstance().get(AppConstants.PASS_MST_ID, ""));

                documentReference.update("driverLocation", driverLocation).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("UpdatedLocation===========>true");
                    }
                });
            }
        }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed ");
    }

    public long getDistance(DirectionsResult results) {
        return results.routes[0].legs[0].distance.inMeters;
    }

}
