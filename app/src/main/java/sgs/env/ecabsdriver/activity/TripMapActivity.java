package sgs.env.ecabsdriver.activity;

import static sgs.env.ecabsdriver.util.ECabsApp.NewTripScreenPaused;
import static sgs.env.ecabsdriver.util.ECabsApp.NewTripScreenResumed;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.animations.AntimationHelper;
import sgs.env.ecabsdriver.animations.EnumHolder;
import sgs.env.ecabsdriver.interfce.Notification;
import sgs.env.ecabsdriver.interfce.StatusCall;
import sgs.env.ecabsdriver.interfce.UImethodsI;
import sgs.env.ecabsdriver.interfce.UiMethods;
import sgs.env.ecabsdriver.interfce.UpdateDriverStatusI;
import sgs.env.ecabsdriver.model.DriverStatus;
import sgs.env.ecabsdriver.model.Location1;
import sgs.env.ecabsdriver.model.LocationPoints;
import sgs.env.ecabsdriver.model.Sos;
import sgs.env.ecabsdriver.model.TripDataModel;
import sgs.env.ecabsdriver.model.TripInfo;
import sgs.env.ecabsdriver.presenter.NotifyPresenter;
import sgs.env.ecabsdriver.presenter.SosPresenter;
import sgs.env.ecabsdriver.presenter.UpdateDriverStatusPresenter;
import sgs.env.ecabsdriver.service.FloatingViewService;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.DriverAlgorithm;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.Internet;
import sgs.env.ecabsdriver.util.OtpView;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class TripMapActivity extends BaseActivity implements OnMapReadyCallback, UImethodsI,
        UImethodsI.StartTrip,
        UImethodsI.EndTrip,
        UImethodsI.ApiFailed, UiMethods,
        StatusCall {

    private static final String TAG = "TripMapActivity";

    private static long START_TIME_IN_MILLIS;

    private static boolean isWait = false;

    AlertDialog alert;

    private GoogleMap mMap;

    private Location mLstLocation;

    private Button btnStart1, btnStart, btnCancel, mButtonVerifty, btnStop, btnSource,
            btnDestination, goToCustomerDestination, goToCustomerPickUp, sosBtn;

    private String mapUri, mCurrentLatitude, mCurrentLongitude, rideStatus, otpNum, drvName;

    private CardView customerLayout;

    private TripDataModel tripDataModel;

    ProgressBar progressBarCancel, progressBar;

    private ImageButton ibCall, ibCall2;

    private UpdateDriverStatusI statusI;

    private SharedPrefsHelper prefsHelper;

    private TextView tvCstName, tvCountDown, isScheduleTrip, specialRemark;

    private List<TripInfo> tripInfos = new ArrayList<>();

    private final boolean goBack = true;

    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    private long mEndTime;
    private long remainingTimeInMillis;
    Date arrivedTime = null;
    Date scheduledAt = null;
    String sourceArrivalTime;
    SharedPreferences prefs;
    private ListenerRegistration tripDataEventListner;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {
            if (resultCode == RESULT_OK) {
                if (mapUri != null) {
                    callGoogleMap(mapUri);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NewTripScreenResumed();
        if (Internet.isMyServiceRunning(FloatingViewService.class, TripMapActivity.this)) {
            Intent stopIntent = new Intent(this, FloatingViewService.class);
            stopService(stopIntent);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        NewTripScreenPaused();
    }

    @Override
    protected void onStart() {
        super.onStart();
        retrieveTripData();

    }

    private void checkTimerAndManageScreen() {
        manageScreenAccordingDB();
        if (SharedPrefsHelper.getInstance().get(AppConstants.ARRIVED, "") != null &&
                !SharedPrefsHelper.getInstance().get(AppConstants.ARRIVED, "").isEmpty()
                && SharedPrefsHelper.getInstance().get(AppConstants.ARRIVED, "").equals(AppConstants.ARRIVED)) {

            prefs = getSharedPreferences("prefs", MODE_PRIVATE);

            mTimeLeftInMillis = prefs.getLong("millisLeft", START_TIME_IN_MILLIS);
            mTimerRunning = prefs.getBoolean("timerRunning", false);
            mEndTime = prefs.getLong("endTime", 0);

            if (mEndTime == 0L) {
                remainingTimeInMillis = (mTimeLeftInMillis);
            } else {
                long timeDiff = (mEndTime - System.currentTimeMillis());
                //to convert into positive number
                timeDiff = Math.abs(timeDiff);

                long timeDiffInSeconds = (timeDiff / 1000) % 60;
                long timeDiffInMillis = timeDiffInSeconds * 1000;
                long timeDiffInMillisPlusTimerRemaining = remainingTimeInMillis = mTimeLeftInMillis - timeDiffInMillis;

                if (timeDiffInMillisPlusTimerRemaining < 0) {
                    timeDiffInMillisPlusTimerRemaining = Math.abs(timeDiffInMillisPlusTimerRemaining);
                    remainingTimeInMillis = START_TIME_IN_MILLIS - timeDiffInMillisPlusTimerRemaining;
                }
            }
            btnSource.setVisibility(View.GONE);
            btnStart.setVisibility(View.VISIBLE);
            if (mCountDownTimer == null) {
                Log.e("startTimer==>", "start");

                startTimer();
            } else {
                Log.e("updateCountDownText==>", "updateCountDownText");
                updateCountDownText();

            }
        } else {
            coundownTimerStop();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        NotificationManagerCompat.from(getApplicationContext()).cancelAll();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        initView();
        listner();
        registerReceiver(cancelTripBroadcastReceiver, new IntentFilter(AppConstants.TripCancel));
        registerReceiver(scheduleTripBroadcastReceiver, new IntentFilter(AppConstants.scheduleTripInfo));
    }

    private void initView() {
        progressBar = findViewById(R.id.progressBar);
        tvCountDown = findViewById(R.id.tvCountDown);
        tvCstName = findViewById(R.id.textViewCustomerName);
        prefsHelper = SharedPrefsHelper.getInstance();
        ibCall = findViewById(R.id.callButton);
        ibCall2 = findViewById(R.id.buttonCall);
        btnSource = findViewById(R.id.btnSource);
        btnDestination = findViewById(R.id.btnDestination);
        sosBtn = findViewById(R.id.sos);
        goToCustomerDestination = findViewById(R.id.goToCustomerDestination);
        goToCustomerDestination.setVisibility(View.GONE);
        goToCustomerPickUp = findViewById(R.id.goToCustomerPickUp);
        isScheduleTrip = findViewById(R.id.isScheduleTrip);
        AntimationHelper.with(EnumHolder.Pulse).duration(1500).
                interpolate(new AccelerateDecelerateInterpolator()).repeat(1500).
                setView(findViewById(R.id.callButton));
        btnStart1 = findViewById(R.id.startBtn);
        btnCancel = findViewById(R.id.cancelButton);
        btnStart = findViewById(R.id.btnStart);
        btnStart.setVisibility(View.GONE);
        customerLayout = findViewById(R.id.customerDetails);
        btnStop = findViewById(R.id.stopBtn);
        progressBarCancel = findViewById(R.id.progressBarCancel);
        specialRemark = findViewById(R.id.specialRemark);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

    }

    BroadcastReceiver scheduleTripBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                tripInfos = intent.getParcelableArrayListExtra(AppConstants.scheduleTripInfo);

                if (tripInfos.size() > 0) {
                    Timestamp timestamp = (Timestamp) tripInfos.get(0).getScheduledAt();
                    long milliseconds = timestamp.getSeconds() * 1000 + timestamp.getNanoseconds() / 1000000;
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd, HH:mm a");
                    Date netDate = new Date(milliseconds);
                    String date = sdf.format(netDate).toString();
                    Log.e("date",date);

                    scheduledTripPopup(tripInfos.get(0).getFromAddress(),
                            tripInfos.get(0).getToAddress(), tripInfos.get(0).getInitialPrice(), date,
                            tripInfos.get(0).getPassengerName(), tripInfos.get(0).getPassengerPhone());


                }
            }
        }
    };

    public void retrieveTripData() {

        DocumentReference tripDataRef = FirebaseFirestore.getInstance().collection("driver").document(SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, ""))
                .collection("running-trip").document("tripInfo");

        tripDataEventListner = tripDataRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }

                String PassangerTripMasterID;
                if (value != null) {
                    PassangerTripMasterID = value.getString("passengerTripMasterId");
                    if (PassangerTripMasterID != null && !PassangerTripMasterID.isEmpty()) {
                        FirebaseFirestore.getInstance().collection("trips")
                                .document("passengerTripMasterId:" + PassangerTripMasterID)
                                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                        @Nullable FirebaseFirestoreException e) {
                                        if (e != null) {
                                            Log.w(TAG, "Listen failed.", e);
                                            return;
                                        }

                                        if (snapshot != null && snapshot.exists()) {
                                            tripDataModel = snapshot.toObject(TripDataModel.class);
                                            checkTimerAndManageScreen();
                                            prefsHelper.save(AppConstants.PASS_MST_ID, tripDataModel.getTripInfo().getPassengerTripMasterId());
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
        boolean endTrip =
                SharedPrefsHelper.getInstance().get(AppConstants.TRIP_END, false);
        SharedPrefsHelper.getInstance().save(AppConstants.ACCEPT, false);
        if (!endTrip) {
            Toast.makeText(TripMapActivity.this, "Customer cancelled trip !",
                    Toast.LENGTH_LONG).show();
        }
        if (!SharedPrefsHelper.getInstance().get(AppConstants.END_TRIP_BUTTON_CLICKED,
                false)) {
            coundownTimerStop();
            removeSharedPrefrancValue();
            Intent intent =
                    new Intent(TripMapActivity.this, WaitingForCustomerActivity.class);
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    public void manageScreenAccordingDB() {

        if (tripDataModel != null) {
            if(tripDataModel.getTripInfo()!=null && tripDataModel.getTripInfo().getTripStatus()!=null &&
                    !tripDataModel.getTripInfo().getTripStatus().isEmpty()) {
                rideStatus = tripDataModel.getTripInfo().getTripStatus();
            }
            tvCstName.setText(tripDataModel.getPassengerInfo().getName());

            if (SharedPrefsHelper.getInstance().get(AppConstants.TripScheduledAt, "") != null
                    && !SharedPrefsHelper.getInstance().get(AppConstants.TripScheduledAt, "").isEmpty()) {
                try {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss.SSSXXX");
                    Date date = dateFormat.parse(SharedPrefsHelper.getInstance().get(AppConstants.TripScheduledAt, ""));
                    DateFormat formatter = new SimpleDateFormat("hh:mm a"); //If you need time just put specific format for time like 'kk:mm:ss'
                    String TripScheduledAt = formatter.format(date);
                    isScheduleTrip.setText(getString(R.string.pickupCustomerAt) + TripScheduledAt);
                    isScheduleTrip.setTextColor(ContextCompat.getColor(this, R.color.red));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            if (SharedPrefsHelper.getInstance().get(AppConstants.SpecialRemraks, "") != null && !SharedPrefsHelper.getInstance().get(AppConstants.SpecialRemraks, "").isEmpty()) {
                specialRemark.setText(SharedPrefsHelper.getInstance().get(AppConstants.SpecialRemraks, ""));
                specialRemark.setVisibility(View.VISIBLE);
            } else {
                specialRemark.setVisibility(View.GONE);
            }

            if (rideStatus.contentEquals(AppConstants.TRIP_ALLOTTED)) {
                customerLayout.setVisibility(View.VISIBLE);
                btnSource.setVisibility(View.VISIBLE);
                isScheduleTrip.setVisibility(View.VISIBLE);
            }
            if (rideStatus.contentEquals(
                    AppConstants.ARRIVED)) {
                btnSource.setVisibility(View.GONE);
                btnStart.setVisibility(View.VISIBLE);
                customerLayout.setVisibility(View.VISIBLE);
                startTimer();
            }
            if (rideStatus.contentEquals(AppConstants.TRIP_ONBOARDING)) {
                customerLayout.setVisibility(View.GONE);
                btnSource.setVisibility(View.GONE);
                isScheduleTrip.setVisibility(View.GONE);
                btnStop.setVisibility(View.VISIBLE);
            }
        }
        if (btnStop.getVisibility() == View.VISIBLE) {
            goToCustomerDestination.setVisibility(View.VISIBLE);
            goToCustomerPickUp.setVisibility(View.GONE);
        }

        statusI = new UpdateDriverStatusPresenter();
        drvName = SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_NAME, "");

    }

    private void listner() {
        goToCustomerDestination.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(TripMapActivity.this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 1234);
                } else {
                    moveToGoogleMapsNavigation();
                }
            } else {
                moveToGoogleMapsNavigation();
            }

        });
        btnStop.setOnClickListener(view -> {

            if (ECabsApp.isNetworkAvailable(TripMapActivity.this)) {
                // updating the status as free
                if (mLstLocation != null && tripDataModel != null) {

                    startProgessBar();
                    DriverStatus driverStatus = new DriverStatus();
                    String drvMstId = prefsHelper.get(AppConstants.DRV_MASTER_ID, "");
                    driverStatus.setDriverTripMasterId(drvMstId);
                    Location1 location1 = new Location1();
                    location1.setLatitude(String.valueOf(mLstLocation.getLatitude()));
                    location1.setLongitide(String.valueOf(mLstLocation.getLongitude()));
                    driverStatus.setDriverLocation(location1);
                    driverStatus.setAddress(DriverAlgorithm.getAddressFromLatLang(TripMapActivity.this,
                            mLstLocation.getLatitude(), mLstLocation.getLongitude()));
                    driverStatus.setStatus("free");
                    driverStatus.setPassengerTripMasterId(tripDataModel.getTripInfo().getPassengerTripMasterId());
                    driverStatus.setPassengerTripStageId("tripEnd");
                    driverStatus.setPaymentMode(prefsHelper.get(AppConstants.PAYMENT_MODE));
                    SharedPrefsHelper.getInstance().save(AppConstants.END_TRIP_BUTTON_CLICKED,
                            true);
                    statusI.updateDriverStatusAPI(TripMapActivity.this, driverStatus);
                    disablebuttons(btnStop);

                } else {
                    btnStop.setEnabled(true);
                    Toast.makeText(TripMapActivity.this,
                            "Please wait for some time fetching location or close and reopen " +
                                    "the app again ",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                btnStop.setEnabled(true);
                ECabsApp.noInternet(TripMapActivity.this, "No Internet !");
            }
        });
        sosBtn.setOnClickListener(view -> {

            if (ECabsApp.isNetworkAvailable(TripMapActivity.this) && mLstLocation != null) {
                String driverName = prefsHelper.get(AppConstants.DRIVER_NAME);
                String driverId = prefsHelper.get(AppConstants.DRIVER_ID);
                String vehicleNo = prefsHelper.get(AppConstants.DRIVER_VEH_NO);
                String drvMstId = prefsHelper.get(AppConstants.DRV_MASTER_ID);
                String drvPhone = prefsHelper.get(AppConstants.MOBILE_NUM);
                String usrId = prefsHelper.get(AppConstants.USER_ID);
                LocationPoints locationPoints = new LocationPoints();
                List<Double> list = new ArrayList<>();
                list.add(0, mLstLocation.getLongitude());
                list.add(1, mLstLocation.getLatitude());
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
                new SosPresenter().sosAPI(TripMapActivity.this, sos);
                disablebuttons(sosBtn);
            } else {
                Toast.makeText(TripMapActivity.this, "Please wait location capturing.....",
                        Toast.LENGTH_SHORT).show();
            }
        });
        goToCustomerPickUp.setOnClickListener(v -> {
            String newUri =
                    "https://www.google.com/maps/dir/?api=1&origin=" + mCurrentLatitude + "," + mCurrentLongitude + "&destination=" + tripDataModel.getTripInfo().getPickupLocation().getLatitude() + "," + tripDataModel.getTripInfo().getPickupLocation().getLongitude() + "&travelmode=driving";
            Log.d(TAG, "Uri: " + newUri);
            mapUri = newUri;
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(TripMapActivity.this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 1234);
                } else {
                    callGoogleMap(newUri);
                }
            } else {
                callGoogleMap(newUri);
            }
        });
        // call button
        ibCall.setOnClickListener(view -> callPhone());
        ibCall2.setOnClickListener(view -> callPhone());
        //start a trip button
        btnStart.setOnClickListener(view -> {

            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(TripMapActivity.this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 1234);
                } else {
                    dialogOTP();
                }
            } else {
                dialogOTP();
            }

        });
        btnStart1.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(TripMapActivity.this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 1234);
                } else {
                    dialogOTP();
                }
            } else {
                dialogOTP();
            }
        });
        btnCancel.setOnClickListener(view -> Toast.makeText(TripMapActivity.this, "cancelled", Toast.LENGTH_SHORT).show());

        btnSource.setOnClickListener(view -> {

            if (mLstLocation != null) {
                arrivedAPI();
                btnStart.setVisibility(View.VISIBLE);
                disablebuttons(btnSource);
            } else {
                Toast.makeText(TripMapActivity.this,
                        "Please wait for some time fetching location or close and reopen the " +
                                "app again ",
                        Toast.LENGTH_SHORT).show();
            }
        });
        btnDestination.setOnClickListener(view -> {

            if (mLstLocation != null) {
                DriverStatus driverStatus = new DriverStatus();
                String drvMstId =
                        prefsHelper.get(AppConstants.DRV_MASTER_ID, AppConstants.NO_TOKEN);
                String pasMstId =
                        prefsHelper.get(AppConstants.PASS_MST_ID, "");
                driverStatus.setDriverTripMasterId(drvMstId);
                driverStatus.setPassengerTripMasterId(pasMstId);
                Location1 location1 = new Location1();
                location1.setLatitude(String.valueOf(mLstLocation.getLatitude()));
                location1.setLongitide(String.valueOf(mLstLocation.getLongitude()));
                driverStatus.setDriverLocation(location1);
                driverStatus.setPassengerTripStageId("arrivalAtDestiantion");
                Notification notification = new NotifyPresenter();
                notification.notifyUsr(TripMapActivity.this, driverStatus);
                disablebuttons(btnDestination);
            } else {
                Toast.makeText(TripMapActivity.this,
                        "Please wait for some time fetching location or close and reopen the " +
                                "app again ",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void moveToGoogleMapsNavigation() {
        if (alert != null) {
            alert.dismiss();
        }
        coundownTimerStop();

        String newUri =
                "https://www.google.com/maps/dir/?api=1&origin=" + mCurrentLatitude + "," + mCurrentLongitude + "&destination=" + tripDataModel.getTripInfo().getDropLocation().getLatitude() + "," + tripDataModel.getTripInfo().getDropLocation().getLongitude();
        Log.d(TAG, "Uri: " + newUri);
        mapUri = newUri;
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(TripMapActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1234);
            } else {
                callGoogleMap(newUri);
            }
        } else {
            callGoogleMap(newUri);
        }
        // changing the db status to startTrip and making the stp button visible

        String mstId = prefsHelper.get(AppConstants.PASS_MST_ID);
        prefsHelper.save(AppConstants.PASS_MST_ID, mstId);
    }

    @Override
    public void startProgessBar() {

    }

    public void callGoogleMap(String uriStr) {
        Uri googleIntentURI = Uri.parse(uriStr);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, googleIntentURI);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
        Intent serviceIntent = new Intent(this, FloatingViewService.class);
        // start background service for floating view
        startService(serviceIntent);
    }

    private void callPhone() {
        String phone = "+91" + tripDataModel.getPassengerInfo().getPhone();
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
        startActivity(intent);
    }

    private void dialogOTP() {
        final Button buttonVerify;
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.otp_view, null);
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.MyDialogTheme);

        builder.setView(layout);
        builder.setCancelable(true);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (!isFinishing()) {
            alertDialog.show();
        }
        final OtpView otpView = layout.findViewById(R.id.otpView);
        buttonVerify = layout.findViewById(R.id.btnVerify);
        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProgessBar();
                Log.d(TAG, "onClick: " + view.getId());
                buttonVerify.setText("Verifying please wait .....");
                mButtonVerifty = buttonVerify;
                //                buttonVerify.setEnabled(false);
                otpNum = otpView.getOTP();
                if (ECabsApp.isNetworkAvailable(TripMapActivity.this)) {
                    // Updating the trip status
                    if (mLstLocation != null && tripDataModel != null) {
                        DriverStatus driverStatus = new DriverStatus();
                        driverStatus.setDriverTripMasterId(
                                SharedPrefsHelper.getInstance().get(AppConstants.DRV_MASTER_ID,
                                        AppConstants.NO_TOKEN));
                        driverStatus.setOtp(otpNum);
                        Location1 location1 = new Location1();

                        location1.setLatitude(String.valueOf(mLstLocation.getLatitude()));
                        location1.setLongitide(String.valueOf(mLstLocation.getLongitude()));
                        driverStatus.setDriverLocation(location1);
                        location1.setAddress(
                                DriverAlgorithm.getAddressFromLatLang(TripMapActivity.this,
                                        mLstLocation.getLatitude(), mLstLocation.getLongitude()));
                        driverStatus.setPassengerTripMasterId(tripDataModel.getTripInfo().getPassengerTripMasterId());
                        driverStatus.setStatus("booked");
                        statusI.updateDriverStatusAPI(TripMapActivity.this, driverStatus);
                        alertDialog.dismiss();
                    } else {
                        Toast.makeText(TripMapActivity.this,
                                "Please wait for some time fetching location or close and reopen " +
                                        "the app again ",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ECabsApp.noInternet(TripMapActivity.this, "No internet !");
                    return;
                }
                buttonVerify.setEnabled(true);
                hideKeyboard();
            }
        });

    }

    private void arrivedAPI() {
        if (mLstLocation != null && tripDataModel != null) {
            DriverStatus driverStatus = new DriverStatus();
            String drvMstId = prefsHelper.get(AppConstants.DRV_MASTER_ID, "");
            driverStatus.setDriverTripMasterId(drvMstId);
            Location1 location1 = new Location1();
            location1.setLatitude(String.valueOf(mLstLocation.getLatitude()));
            driverStatus.setAddress(DriverAlgorithm.getAddressFromLatLang(TripMapActivity.this,
                    mLstLocation.getLatitude(), mLstLocation.getLongitude()));
            location1.setLongitide(String.valueOf(mLstLocation.getLongitude()));
            driverStatus.setDriverLocation(location1);
            driverStatus.setStatus(AppConstants.ARRIVED);
            driverStatus.setPassengerTripMasterId(tripDataModel.getTripInfo().getPassengerTripMasterId());
            driverStatus.setDriverName(drvName);
            Log.d(TAG, "onClick: driver " + driverStatus);
            statusI.updateDriverStatusAPI(TripMapActivity.this, driverStatus);
            Log.d(TAG, "onClick: driver " + driverStatus);
        }
    }

    @Override
    public void endProgressBar() {
		/*if (progressBarLayout != null)
			progressBarLayout.hideProgressDialog();*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        if (alert != null) {
            alert.dismiss();
        }
        if (tripDataEventListner != null) {
            tripDataEventListner.remove();
            tripDataEventListner = null;
        }
        if (scheduleTripBroadcastReceiver != null) {
            unregisterReceiver(scheduleTripBroadcastReceiver);
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
        return 0;
    }

    @Override
    protected int getColor() {
        return 0;
    }

    @Override
    protected void retrivedLocation(Location location) {
        mLstLocation = location;
        currentLocationPoint(location);
        mCurrentLatitude = String.valueOf(location.getLatitude());
        mCurrentLongitude = String.valueOf(location.getLongitude());
        // sending location to firebase
        sgs.env.ecabsdriver.model.Location l = new sgs.env.ecabsdriver.model.Location();
        l.setLatitude(String.valueOf(mCurrentLatitude));
        l.setLongitude(String.valueOf(mCurrentLongitude));
        postToDatabase(l);

    }

    private void currentLocationPoint(Location location) {
        mMap.clear();
        final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition cameraPosition =
                new CameraPosition.Builder().target(latLng).zoom(16).build();
        mMap.addMarker(new MarkerOptions().position(latLng).flat(true).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.carsmall)));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void postToDatabase(sgs.env.ecabsdriver.model.Location location) {

        System.out.println("locationUpdateFirebase4" + location.toString());

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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(TripMapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                TripMapActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    // ending the trip here
    @Override
    public void endTrip() {
        Intent intent = new Intent(TripMapActivity.this, CollectCashActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void apiFailed() {
		/*if (progressBarLayout != null){
			progressBarLayout.hideProgressDialog();
	}*/
        mButtonVerifty.setText("Try again ?");
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
    public void progresStart() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void progressEnd() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void arrivedSuccess() {
        btnSource.setVisibility(View.GONE);
        btnStart.setVisibility(View.VISIBLE);
        customerLayout.setVisibility(View.VISIBLE);
        SharedPrefsHelper.getInstance().save(AppConstants.ARRIVED, AppConstants.ARRIVED);

        startTimer();
    }

    private void startTimer() {
        try {
            //Retrive Schedule at time from shared prefrence
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss.SSSXXX");
            Date date = dateFormat.parse(SharedPrefsHelper.getInstance().get(AppConstants.TripScheduledAt, ""));
            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss"); //If you need time just put specific format for time like 'kk:mm:ss'
            String TripScheduledAt = formatter.format(date);
            scheduledAt = dateFormat.parse(SharedPrefsHelper.getInstance().get(AppConstants.TripScheduledAt, ""));

            //Retrive Arrival time from shared prefrence

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
            if (SharedPrefsHelper.getInstance().get(AppConstants.SourceArrivalTime) != null &&
                    !SharedPrefsHelper.getInstance().get(AppConstants.SourceArrivalTime).toString().isEmpty()) {
                Date date1 = dateFormat.parse(SharedPrefsHelper.getInstance().get(AppConstants.SourceArrivalTime, ""));
                sourceArrivalTime = formatter.format(date1);
                arrivedTime = dateFormat.parse(SharedPrefsHelper.getInstance().get(AppConstants.SourceArrivalTime, ""));
            } else {
                arrivedTime = Calendar.getInstance().getTime();
                sourceArrivalTime = formatter.format(Calendar.getInstance().getTime());
            }
            if (SharedPrefsHelper.getInstance().get(AppConstants.IsScheduleTrip, false)) {
                //add 10 more minute to schedule at time
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
                Date d = df.parse(TripScheduledAt);
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                cal.add(Calendar.MINUTE, 10);

                //convert date formate of schedule and arrival time
                String newTime = df.format(cal.getTime());
                scheduledAt = sdf.parse(newTime);
            } else {
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
                Date d = df.parse(sourceArrivalTime);
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                cal.add(Calendar.MINUTE, 10);

                //convert date formate of schedule and arrival time
                String newTime = df.format(cal.getTime());
                arrivedTime = sdf.parse(newTime);

            }

            // apply conndition
            Log.e("scheduledAt=========>", String.valueOf(scheduledAt));
            Log.e("arrivedTime=========>", String.valueOf(arrivedTime));

            assert scheduledAt != null;
            assert arrivedTime != null;
            int dateDelta = scheduledAt.compareTo(arrivedTime);
            switch (dateDelta) {
                case 1:
                    int dateDelta1 = scheduledAt.compareTo(Calendar.getInstance().getTime());
                    if (dateDelta1 > 0) {
                        long timeDiff = (scheduledAt.getTime() - Calendar.getInstance().getTimeInMillis());
                        timeDiff = Math.abs(timeDiff);

                        START_TIME_IN_MILLIS = timeDiff;
                        remainingTimeInMillis = START_TIME_IN_MILLIS;
                        coundownTimerStart();
                    } else {
                        btnSource.setVisibility(View.GONE);
                        btnStart.setVisibility(View.VISIBLE);
                        Log.e("START_TIME_IN_MILLIS1", String.valueOf(START_TIME_IN_MILLIS));
                        if (!isWait) {
                            cancelTripDialog();
                        } else {
                            coundownTimerStart();
                        }
                    }
                    break;
                case -1:
                    int dateDelta2 = arrivedTime.compareTo(Calendar.getInstance().getTime());
                    if (dateDelta2 > 0) {
                        long timeDiff1 = (arrivedTime.getTime() - Calendar.getInstance().getTimeInMillis());
                        //to convert into positive number
                        timeDiff1 = Math.abs(timeDiff1);
                        START_TIME_IN_MILLIS = timeDiff1;
                        remainingTimeInMillis = START_TIME_IN_MILLIS;
                        coundownTimerStart();
                    } else {
                        btnSource.setVisibility(View.GONE);
                        btnStart.setVisibility(View.VISIBLE);
                        Log.e("START_TIME_IN_MILLIS2", String.valueOf(START_TIME_IN_MILLIS));
                        if (!isWait) {
                            cancelTripDialog();
                        } else {
                            coundownTimerStart();
                        }
                    }

                    break;
            }

        } catch (Exception e) {
            Log.e("error====>", e.getMessage());
            e.printStackTrace();
        }

    }

    private void coundownTimerStart() {
        if (mCountDownTimer == null) {
            mCountDownTimer = new CountDownTimer(remainingTimeInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    remainingTimeInMillis = millisUntilFinished;
                    mTimeLeftInMillis = millisUntilFinished;
                    START_TIME_IN_MILLIS = millisUntilFinished;

                    btnSource.setVisibility(View.GONE);
                    tvCountDown.setVisibility(View.VISIBLE);
                    updateCountDownText();

                }

                @Override
                public void onFinish() {
                    updateCountDownText();
                    Log.e("START_TIME_IN_MILLIS", String.valueOf(START_TIME_IN_MILLIS));
                    if (isWait) {
                        if (!isFinishing()) {
                            cancelAPI();
                        }
                    } else {
                        cancelTripDialog();
                    }

                }
            }.start();
        }

    }

    private void updateCountDownText() {
        String timeLeftFormatted;
        int seconds = (int) (remainingTimeInMillis / 1000) % 60;
        int minutes = (int) (remainingTimeInMillis / 1000) / 60;

        timeLeftFormatted = getResources().getString(R.string.tripAutoCancel)
                + " " + String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(remainingTimeInMillis),
                TimeUnit.MILLISECONDS.toSeconds(remainingTimeInMillis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingTimeInMillis)));
        //timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        tvCountDown.setText(timeLeftFormatted);
    }

    public void coundownTimerStop() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        tvCountDown.setVisibility(View.GONE);

    }

    @Override
    public void arrivedFailure() {
        btnSource.setVisibility(View.VISIBLE);
        isScheduleTrip.setVisibility(View.VISIBLE);
    }

    @Override
    public void cancelSuccess() {
        isWait = false;
        START_TIME_IN_MILLIS = 0;
        coundownTimerStop();
        moveToHomeActivity();

    }

    @Override
    public void cancelFailure() {
        Toast.makeText(this, "Please cancel the trip again", Toast.LENGTH_LONG).show();
        cancelTripDialog();
    }

    private void moveToHomeActivity() {
        removeSharedPrefrancValue();
        Intent intent = new Intent(TripMapActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void removeSharedPrefrancValue() {
        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("millisLeft");
        editor.remove("timerRunning");
        editor.remove("endTime");
        editor.commit();

        SharedPrefsHelper.getInstance().save(AppConstants.ARRIVED, "");
        SharedPrefsHelper.getInstance().save(AppConstants.IsScheduleTrip, false);
        SharedPrefsHelper.getInstance().save(AppConstants.TripScheduledAt, "");
        SharedPrefsHelper.getInstance().save(AppConstants.SourceArrivalTime, "");

    }

    public void cancelAPI() {
        DriverStatus driverStatus = new DriverStatus();
        String drvMstId = prefsHelper.get(AppConstants.DRV_MASTER_ID, AppConstants.NO_TOKEN);
        driverStatus.setDriverTripMasterId(drvMstId);
        driverStatus.setStatus("cancel");
        String passId = prefsHelper.get(AppConstants.PASS_MST_ID, "");
        driverStatus.setPassengerTripMasterId(passId);
        driverStatus.setDriverName(drvName);

        Log.d(TAG, "onClick: driver " + driverStatus);
        if (mLstLocation != null) {
            Location1 location1 = new Location1();
            location1.setLatitude(String.valueOf(mLstLocation.getLatitude()));
            location1.setAddress(DriverAlgorithm.getAddressFromLatLang(TripMapActivity.this,
                    mLstLocation.getLatitude(), mLstLocation.getLongitude()));
            location1.setLongitide(String.valueOf(mLstLocation.getLongitude()));
            driverStatus.setDriverLocation(location1);
        }
        Log.d(TAG, "onClick: driver " + driverStatus);
        statusI.updateDriverStatusAPI(TripMapActivity.this, driverStatus);

    }

    private void cancelTripDialog() {
        tvCountDown.setVisibility(View.GONE);
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.waiting_cusotmer, null);
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.MyDialogTheme);

        builder.setView(layout);
        builder.setCancelable(false);
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (!isFinishing()) {
            alertDialog.show();
        }
        Button imageButton = layout.findViewById(R.id.floatingActionButtonAccept);
        final Button waitBtn = layout.findViewById(R.id.waitButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAPI();
                alertDialog.cancel();
                alertDialog.dismiss();
            }
        });

        waitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coundownTimerStop();
                isWait = true;
                START_TIME_IN_MILLIS = 120000;
                remainingTimeInMillis = START_TIME_IN_MILLIS;

                btnSource.setVisibility(View.GONE);
                btnStart.setVisibility(View.VISIBLE);

                coundownTimerStart();
                alertDialog.cancel();
                alertDialog.dismiss();

            }
        });
    }

}