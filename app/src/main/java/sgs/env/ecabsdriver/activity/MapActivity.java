package sgs.env.ecabsdriver.activity;

import static sgs.env.ecabsdriver.util.ECabsApp.NewTripScreenPaused;
import static sgs.env.ecabsdriver.util.ECabsApp.NewTripScreenResumed;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.data.local.ECabsDriverDbHelper;
import sgs.env.ecabsdriver.interfce.UiMethods;
import sgs.env.ecabsdriver.model.CustomerNotfication;
import sgs.env.ecabsdriver.model.LocationPoints;
import sgs.env.ecabsdriver.model.OnBoard;
import sgs.env.ecabsdriver.model.Sos;
import sgs.env.ecabsdriver.model.TripDataModel;
import sgs.env.ecabsdriver.presenter.SosPresenter;
import sgs.env.ecabsdriver.reciver.FirestoreBroadcastReceiver;
import sgs.env.ecabsdriver.service.FloatingViewService;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.MediaPlayerSingleton;
import sgs.env.ecabsdriver.util.OtpView;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class MapActivity extends BaseActivity implements OnMapReadyCallback, UiMethods {

    private static final int REQUEST_PERMISSION_GOOGLE_NEARBY = 19;

    private static final String TAG = "MapActivity";

    public static boolean isFirstTime = true;

    OtpView otpView;

    TripDataModel tripDataModel;

    LatLng destLatLng;

    private ImageButton callButton;

    private Button buttonStart, buttonStop, buttonCancel;

    private GoogleMap map;

    private SupportMapFragment mapFragment;

    private String mapUri;

    private double mCurrentLatitude, mCurrentLongitude;

    private TextView textViewName, specialRemark;

    private final boolean goBack = true;

    private ProgressBar progressBar;

    private Button sosBtn;

    private Location mLstLoc;

    private SharedPrefsHelper helper;

    private Button goToMapsWithCutomerPickUpLocation;

    private TextView isScheduleTrip;

    private ListenerRegistration tripDataEventListner;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {
            if (resultCode == Activity.RESULT_OK) {
                if (mapUri != null) {
                    callGoogleMap(mapUri);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!goBack) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Can't go back in ride", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NewTripScreenResumed();
        retrieveTripData();
        MediaPlayerSingleton.getInstance().init(getApplicationContext());
        MediaPlayer mediaPlayer = MediaPlayerSingleton.getSingletonMedia();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.stop();
            } else {
                mediaPlayer.stop();
            }
        }
        // after droping at location activity will be destroyed....
        Log.d(TAG, "onResume: ");
        if (isMyServiceRunning(FloatingViewService.class)) {
            Intent navigoIntent = new Intent(this, FloatingViewService.class);
            stopService(navigoIntent);
        }
    }

    // if app goes in to the background then starting the firebaseLocServie in which sending the
    // location to firebase
    @Override
    protected void onPause() {
        super.onPause();
        NewTripScreenPaused();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        inIt();
        listner();

    }

    private void inIt() {
        otpView = findViewById(R.id.otpView);
        progressBar = findViewById(R.id.progressBar);
        helper = SharedPrefsHelper.getInstance();
        Log.d(TAG, "onCreate: ");
        callButton = findViewById(R.id.callButton);
        SharedPrefsHelper.getInstance().delete(AppConstants.NOTIFICATION_TRIP);
        sosBtn = findViewById(R.id.sos);
        goToMapsWithCutomerPickUpLocation = findViewById(R.id.goToMapsWithCutomerPickUpLocation);
        textViewName = findViewById(R.id.textViewCustomerName);
        specialRemark = findViewById(R.id.specialRemark);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googlemap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        buttonStart = findViewById(R.id.btnStart);
        buttonStart.setText("Pick Up");
        buttonStop = findViewById(R.id.buttonStop);
        buttonCancel = findViewById(R.id.buttoncancel);
        isScheduleTrip = findViewById(R.id.isScheduleTrip);
    }

    private void listner() {
        sosBtn.setOnClickListener(view -> {

            if (mLstLoc != null && ECabsApp.isNetworkAvailable(MapActivity.this)) {
                String driverName = helper.get(AppConstants.DRIVER_NAME);
                String driverId = helper.get(AppConstants.DRIVER_ID);
                String vehicleNo = helper.get(AppConstants.DRIVER_VEH_NO);
                String drvMstId = helper.get(AppConstants.DRV_MASTER_ID);
                String drvPhone = helper.get(AppConstants.MOBILE_NUM);
                String usrId = helper.get(AppConstants.USER_ID);
                LocationPoints locationPoints = new LocationPoints();
                List<Double> list = new ArrayList<>();
                list.add(0, mLstLoc.getLongitude());
                list.add(1, mLstLoc.getLatitude());
                locationPoints.setType(AppConstants.POINTS);
                locationPoints.setCoordinates(list);
                Sos sos = new Sos();
                sos.setDriverTripmasterId(drvMstId);
                sos.setId(driverId);
                sos.setLocation(locationPoints);
                sos.setName(driverName);
                sos.setVehicleId(vehicleNo);
                sos.setPhone(drvPhone);
                sos.setUserId(usrId);
                new SosPresenter().sosAPI(MapActivity.this, sos);
                disablebuttons(sosBtn);
            } else {
                Toast.makeText(MapActivity.this, "Please wait location capturing.....",
                        Toast.LENGTH_SHORT).show();
            }
        });
        goToMapsWithCutomerPickUpLocation.setOnClickListener(v -> {
            if (ECabsApp.isNetworkAvailable(MapActivity.this) && mCurrentLatitude != 0.0 && mCurrentLongitude != 0.0
                    && tripDataModel != null && tripDataModel.getTripInfo().getPickupLocation().getLatitude() != 0.0 && tripDataModel.getTripInfo().getPickupLocation().getLongitude() != 0.0) {
                String newUri =
                        "https://www.google.com/maps/dir/?api=1&origin=" + mCurrentLatitude +
                                "," + mCurrentLongitude + "&destination=" + tripDataModel.getTripInfo().getPickupLocation().getLatitude() + "," + tripDataModel.getTripInfo().getPickupLocation().getLongitude() + "&travelmode=driving";
                Log.d(TAG, "Uri: " + newUri);
                mapUri = newUri;
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!Settings.canDrawOverlays(MapActivity.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 1234);
                    } else {
                        callGoogleMap(newUri);
                    }
                } else {
                    callGoogleMap(newUri);
                }
            } else {
                Toast.makeText(MapActivity.this, "Please waiting fetching location ",
                        Toast.LENGTH_SHORT).show();
            }
        });
        buttonStart.setOnClickListener(view -> {
            if (ECabsApp.isNetworkAvailable(MapActivity.this) && mCurrentLatitude != 0.0 && mCurrentLongitude != 0.0 &&
                    tripDataModel != null && tripDataModel.getTripInfo().getPickupLocation() != null
                    && tripDataModel.getTripInfo().getPickupLocation().getLatitude() != 0.0
                    && tripDataModel.getTripInfo().getPickupLocation().getLongitude() != 0.0) {
                String newUri =
                        "https://www.google.com/maps/dir/?api=1&origin=" + mCurrentLatitude +
                                "," + mCurrentLongitude + "&destination=" + tripDataModel.getTripInfo().getPickupLocation().getLatitude() + "," + tripDataModel.getTripInfo().getPickupLocation().getLongitude() + "&mode=driving" + "&alternatives=true";

                mapUri = newUri;
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!Settings.canDrawOverlays(MapActivity.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 1234);
                    } else {
                        callGoogleMap(newUri);
                    }
                } else {
                    callGoogleMap(newUri);
                }
            } else {
                Toast.makeText(MapActivity.this, "Please waiting fetching location ",
                        Toast.LENGTH_SHORT).show();
            }
        });
        buttonCancel.setOnClickListener(view -> {
            buttonStart.setVisibility(View.GONE);
            buttonStop.setVisibility(View.GONE);
            cancelDialog();
        });
        callButton.setOnClickListener(view -> {
            if (tripDataModel != null && tripDataModel.getPassengerInfo().getPhone() != null && !tripDataModel.getPassengerInfo().getPhone().isEmpty()) {
                String phone = "+91" + tripDataModel.getPassengerInfo().getPhone();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Can't make calls", Toast.LENGTH_SHORT).show();
            }
        });


        if (helper.get(AppConstants.TripScheduledAt, "") != null && !helper.get(AppConstants.TripScheduledAt, "").isEmpty()) {

            try {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                Date date = dateFormat.parse(SharedPrefsHelper.getInstance().get(AppConstants.TripScheduledAt, ""));
                DateFormat formatter = new SimpleDateFormat("hh:mm a"); //If you need time just put specific format for time like 'HH:mm:ss'
                String TripScheduledAt = formatter.format(date);

                isScheduleTrip.setText(getString(R.string.pickupCustomerAt) + TripScheduledAt);
                isScheduleTrip.setTextColor(ContextCompat.getColor(this, R.color.red));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (helper.get(AppConstants.SpecialRemraks, "") != null && !helper.get(AppConstants.SpecialRemraks, "").isEmpty()) {
            specialRemark.setText(helper.get(AppConstants.SpecialRemraks, ""));
            specialRemark.setVisibility(View.VISIBLE);
        } else {
            specialRemark.setVisibility(View.GONE);
        }

    }

    public void retrieveTripData(){
        registerReceiver(cancelTripBroadcastReceiver, new IntentFilter(AppConstants.TripCancel));

       DocumentReference tripDataRef =  FirebaseFirestore.getInstance().collection("driver").document(SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, ""))
                .collection("running-trip").document("tripInfo");

        tripDataEventListner =  tripDataRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }

                String PassangerTripMasterID;
                if(value!=null){
                    PassangerTripMasterID = value.getString("passengerTripMasterId");
                    if(PassangerTripMasterID!=null && !PassangerTripMasterID.isEmpty()){
                        FirebaseFirestore.getInstance().collection("trips")
                                .document("passengerTripMasterId:"+PassangerTripMasterID)
                                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                        @Nullable FirebaseFirestoreException e) {
                                        if (e != null) {
                                            Log.w(TAG, "Listen failed.", e);
                                            return;
                                        }

                                        if (snapshot != null && snapshot.exists()) {
                                            Log.d(TAG, "Trip data: " + snapshot.getData());
                                             tripDataModel = snapshot.toObject(TripDataModel.class);
                                            if (tripDataModel != null) {
                                                textViewName.setText(tripDataModel.getPassengerInfo().getName());
                                            }

                                        }
                                    }
                                });

                    }else {
                        CancleTrip();
                    }
                }
            }
        });

    }

    BroadcastReceiver cancelTripBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            CancleTrip();

        }
    };

    private void CancleTrip() {
        SharedPrefsHelper.getInstance().save(AppConstants.ACCEPT, false);
            Intent intent = new Intent(MapActivity.this, WaitingForCustomerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        SharedPrefsHelper.getInstance().save(AppConstants.OFFWAIT, false);
    }




    // implict intent for googleNavaigation
    public void callGoogleMap(String uriStr) {
        Uri googleIntentURI = Uri.parse(uriStr);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, googleIntentURI);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
        Intent serviceIntent = new Intent(this, FloatingViewService.class);
        // start background service for floating view
        startService(serviceIntent);
    }

    private void cancelDialog() {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.trip_cancel, null);
        alertBuilder.setView(dialogView);
        TextView tv = dialogView.findViewById(R.id.textViewCustomerNmae);
        ImageButton imageButton = dialogView.findViewById(R.id.floatingActionButtonAccept);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        AlertDialog alert = alertBuilder.create();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setCancelable(false);
        alert.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tripDataEventListner!=null){
            tripDataEventListner.remove();
            tripDataEventListner = null;
        }
        if (cancelTripBroadcastReceiver != null) {
            unregisterReceiver(cancelTripBroadcastReceiver);
        }
    }

    @Override
    protected void refresh() {
    }

    @Override
    protected int setLayoutIfneeded() {
        return R.layout.no_internet_layout;
    }

    @Override
    protected int getColor() {
        return R.color.bg_screen2;
    }

    @Override
    protected void retrivedLocation(Location location) {
        if (location != null && location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
            mLstLoc = location;
            mCurrentLatitude = location.getLatitude();
            mCurrentLongitude = location.getLongitude();

            // sending location to firebase
            sgs.env.ecabsdriver.model.Location l = new sgs.env.ecabsdriver.model.Location();
            l.setLatitude(String.valueOf(mCurrentLatitude));
            l.setLongitude(String.valueOf(mCurrentLongitude));
            postToDatabase(l);
            destLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            currentLocationPoint(location, destLatLng);
        }
    }

    public void postToDatabase(sgs.env.ecabsdriver.model.Location location) {

        if (location != null) {
            System.out.println("locationUpdateFirebase3" + location.toString());

            if (SharedPrefsHelper.getInstance().get(AppConstants.PASS_MST_ID, "") != null &&
                    !SharedPrefsHelper.getInstance().get(AppConstants.PASS_MST_ID, "").isEmpty()) {
                Map<String, Object> driverLocation = new HashMap<>();
                driverLocation.put("latitude", Double.parseDouble(location.getLatitude()));
                driverLocation.put("longitude", Double.parseDouble(location.getLongitude()));

                DocumentReference documentReference = FirebaseFirestore.getInstance()
                        .collection("trips").document("passengerTripMasterId:" + SharedPrefsHelper.getInstance().get(AppConstants.PASS_MST_ID, ""));

                documentReference.update("driverLocation", driverLocation).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("UpdatedLocation===========>true");
                    }
                });
            }
           }
    }

    private void currentLocationPoint(Location location, LatLng destLatLng) {
        if (location != null) {
            buttonStart.setVisibility(View.VISIBLE);
            disableMapSettings();
            map.clear();
            final LatLng carLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            final LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(carLatLng);
            builder.include(destLatLng);
            map.addMarker(new MarkerOptions().position(carLatLng).flat(true).icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.carsmall)));
            LatLngBounds bounds = builder.build();
            bounds.getCenter();
            map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(carLatLng, 16), 2500, null);
                }
            });
        } else {
            Toast.makeText(this, "location not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void disableMapSettings() {
        map.getUiSettings().setScrollGesturesEnabled(true);
        map.getUiSettings().setTiltGesturesEnabled(false);
        map.getUiSettings().setRotateGesturesEnabled(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_GOOGLE_NEARBY) {
            for (String permission : permissions) {
                Log.d(TAG, "onRequestPerm Permission " + permission);
            }
            for (int result : grantResults) {
                Log.d(TAG, "onRequestPerm result  " + result);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(MapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MapActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
    }

    @Override
    public void progresStart() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void progressEnd() {
        progressBar.setVisibility(View.GONE);
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        if (manager.getRunningServices(Integer.MAX_VALUE) != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                    Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }
}



