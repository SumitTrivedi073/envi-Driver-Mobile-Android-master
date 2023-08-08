package sgs.env.ecabsdriver.activity;

import static sgs.env.ecabsdriver.util.ECabsApp.NewTripScreenPaused;
import static sgs.env.ecabsdriver.util.ECabsApp.NewTripScreenResumed;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.animations.AntimationHelper;
import sgs.env.ecabsdriver.animations.EnumHolder;
import sgs.env.ecabsdriver.interfce.ICheckForTrip;
import sgs.env.ecabsdriver.interfce.UImethodsI;
import sgs.env.ecabsdriver.interfce.UpdateDriverStatusI;
import sgs.env.ecabsdriver.model.CustomerNotfication;
import sgs.env.ecabsdriver.model.DriverStatus;
import sgs.env.ecabsdriver.model.Location1;
import sgs.env.ecabsdriver.model.TripDataModel;
import sgs.env.ecabsdriver.presenter.CheckForNewTripPresenter;
import sgs.env.ecabsdriver.presenter.UpdateDriverStatusPresenter;
import sgs.env.ecabsdriver.service.FloatingViewService;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.DriverAlgorithm;
import sgs.env.ecabsdriver.util.Internet;
import sgs.env.ecabsdriver.util.MediaPlayerSingleton;
import sgs.env.ecabsdriver.util.ProgressBarLayout;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class WaitingForCustomerActivity extends BaseActivity implements
        UImethodsI {

    private static final String TAG = "CustomerWaiting";

    public static ChildEventListener mListener, listenerForCancellationOfTrips;

    private Button checkForTrip;

    private ProgressBarLayout progressBarLayout;

    private Location mLstLocation;

    private String mCurrentLatitude, mCurrentLongitude;

    private UpdateDriverStatusI statusI;

    AlertDialog alertDialog;

    ProgressBar progressBar;

    TripDataModel tripDataModel;

    private ListenerRegistration tripDataEventListner;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(WaitingForCustomerActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("oncreate", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_for_customer);

        registerReceiver(cancelTripBroadcastReceiver, new IntentFilter(AppConstants.TripCancel));

        statusI = new UpdateDriverStatusPresenter();
        progressBarLayout = new ProgressBarLayout();
        ProgressBar progressBarWaiting = findViewById(R.id.progressbarWaiting);
        ObjectAnimator animation1 = ObjectAnimator.ofInt(progressBarWaiting, "progress", 0, 1);
        checkForTrip = findViewById(R.id.checkForTrip);
        animation1.setDuration(4000);
        animation1.setInterpolator(new DecelerateInterpolator());
        animation1.start();
        AntimationHelper.with(EnumHolder.Pulse).duration(1500).interpolate(
                new AccelerateDecelerateInterpolator()).repeat(1500).setView(
                findViewById(R.id.textViewCustomerWaiting));
        String drvMstId = SharedPrefsHelper.getInstance().get(AppConstants.DRV_MASTER_ID, "");
        checkForTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ICheckForTrip iCheckForTrip = new CheckForNewTripPresenter();
                iCheckForTrip.checkForTrip(WaitingForCustomerActivity.this, drvMstId);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAllServices();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            alertDialog.cancel();
            progressBar.setVisibility(View.GONE);
        }

        if(tripDataEventListner!=null){
            tripDataEventListner.remove();
            tripDataEventListner = null;
        }
        if (cancelTripBroadcastReceiver != null) {
            unregisterReceiver(cancelTripBroadcastReceiver);
        }

        Log.d("onDestroy", "mlistener removed successful");

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
        if (location != null) {
            mLstLocation = location;
            mCurrentLatitude = String.valueOf(location.getLatitude());
            mCurrentLongitude = String.valueOf(location.getLongitude());
            // sending location to firebase
            sgs.env.ecabsdriver.model.Location l = new sgs.env.ecabsdriver.model.Location();
            l.setLatitude(String.valueOf(mCurrentLatitude));
            l.setLongitude(String.valueOf(mCurrentLongitude));
            postToDatabase(l);
        }
    }

    public void postToDatabase(sgs.env.ecabsdriver.model.Location location) {

        System.out.println("locationUpdateFirebase2" + location.toString());

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

    public void custmerDialog() {

        MediaPlayerSingleton mediaPlayerSingleton = MediaPlayerSingleton.getInstance();
        mediaPlayerSingleton.init(getApplicationContext());
        MediaPlayer mediaPlayer = MediaPlayerSingleton.getSingletonMedia();
        mediaPlayer.setLooping(true);

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.start();
        } else {
            mediaPlayer.start();
        }
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.accept_customer, null);
        final android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);

        builder.setView(layout);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (!isFinishing()) {
            alertDialog.show();
        }

        progressBar = layout.findViewById(R.id.progressBar);
        Button gobBtn = layout.findViewById(R.id.floatingActionButtonAccept);
        gobBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                updateTrip("allotted");

                stopMediaplayer();
                disablebuttons(gobBtn);
            }
        });

    }

    public void updateTrip(String status) {
        if (mLstLocation != null) {
            DriverStatus driverStatus = new DriverStatus();
            String drvMstId = SharedPrefsHelper.getInstance().get(AppConstants.DRV_MASTER_ID, AppConstants.NO_TOKEN);
            driverStatus.setDriverTripMasterId(drvMstId);
            Location1 location1 = new Location1();
            location1.setLatitude(String.valueOf(mLstLocation.getLatitude()));
            location1.setAddress(DriverAlgorithm.getAddressFromLatLang(WaitingForCustomerActivity.this,mLstLocation.getLatitude(), mLstLocation.getLongitude()));
            location1.setLongitide(String.valueOf(mLstLocation.getLongitude()));
            driverStatus.setDriverLocation(location1);
            driverStatus.setStatus(status);
            driverStatus.setPassengerTripMasterId(tripDataModel.getTripInfo().getPassengerTripMasterId());
            driverStatus.setDriverName(SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_NAME, ""));
            SharedPrefsHelper.getInstance().save(AppConstants.PASS_MST_ID, tripDataModel.getTripInfo().getPassengerTripMasterId());

            Log.d(TAG, "onClick: driver " + driverStatus);
            statusI.updateDriverStatusAPI(WaitingForCustomerActivity.this, driverStatus);
            Log.d(TAG, "onClick: driver " + driverStatus);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NewTripScreenResumed();
        SharedPrefsHelper.getInstance().delete(AppConstants.PAYMENT_MODE);
        SharedPrefsHelper.getInstance().delete(AppConstants.ARRIVED);
        SharedPrefsHelper.getInstance().delete(AppConstants.IsWait);
        SharedPrefsHelper.getInstance().delete(AppConstants.SpecialRemraks);
        retrieveTripData();

    }

    @Override
    protected void onPause() {
        super.onPause();
        NewTripScreenPaused();
    }

    public void retrieveTripData(){

        DocumentReference tripDataRef =  FirebaseFirestore.getInstance().collection("driver").document(SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, ""))
                .collection("running-trip").document("tripInfo");

       tripDataEventListner = tripDataRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }

                String PassangerTripMasterID;
                if(value!=null){
                    PassangerTripMasterID = value.getString("passengerTripMasterId");
                    if(PassangerTripMasterID!=null && !PassangerTripMasterID.isEmpty()
                    && !PassangerTripMasterID.equals(SharedPrefsHelper.getInstance().get(AppConstants.PASS_MST_ID))){
                        Log.e("PassangerTripMasterID",PassangerTripMasterID);
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

                                            Timestamp timestamp = tripDataModel.getUpdatedAt();
                                            long milliseconds = timestamp.getSeconds() * 1000 + timestamp.getNanoseconds() / 1000000;
                                            Date netDate = new Date(milliseconds);
                                            Date currentDate = new Date();
                                            SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy");

                                            Log.e("netdate", output.format(netDate));
                                            Log.e("currentDate", output.format(currentDate));
                                            Log.e("isSameDay", String.valueOf(output.format(netDate).equals(output.format(currentDate))));

                                            if (tripDataModel.getTripInfo() != null&&
                                                    String.valueOf(output.format(netDate)).equals(output.format(currentDate))) {


                                                if (alertDialog == null) {
                                                    custmerDialog();
                                                }
                                            }
                                        }
                                    }
                                });
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
        stopAllServices();
        SharedPrefsHelper.getInstance().save(AppConstants.ACCEPT, false);
        SharedPrefsHelper.getInstance().save(AppConstants.OFFWAIT, false);
        if(alertDialog!=null && alertDialog.isShowing()){
            alertDialog.dismiss();
            alertDialog.cancel();
        }
    }

    private void stopAllServices() {
        stopMediaplayer();
        if (Internet.isMyServiceRunning(FloatingViewService.class, this)) {
            Intent i = new Intent(this, FloatingViewService.class);
            stopService(i);
        }
    }

    private void stopMediaplayer() {
        MediaPlayerSingleton mediaPlayerSingleton = MediaPlayerSingleton.getInstance();
        mediaPlayerSingleton.init(getApplicationContext());
        MediaPlayer mediaPlayer = MediaPlayerSingleton.getSingletonMedia();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.stop();
        }

    }

    @Override
    public void startProgessBar() {
        progressBarLayout.displayDialog(WaitingForCustomerActivity.this);
    }

    @Override
    public void endProgressBar() {
        if (progressBar != null && progressBar.isShown()) {
            progressBar.setVisibility(View.GONE);
        }
        if (progressBarLayout != null)
            progressBarLayout.hideProgressDialog();
    }

    public void setRequestDetailsFromFirebaseToLocalDb() {
        SharedPrefsHelper.getInstance().delete(AppConstants.PASS_MST_ID);
        retrieveTripData();
    }



}
