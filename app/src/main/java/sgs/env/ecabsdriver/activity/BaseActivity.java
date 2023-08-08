package sgs.env.ecabsdriver.activity;

import static sgs.env.ecabsdriver.service.RetrieveFirestoreData.logoutDocumentRef;
import static sgs.env.ecabsdriver.util.ECabsApp.isNewTripScreenVisible;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.data.local.ECabsDriverDbHelper;
import sgs.env.ecabsdriver.dialog.ShowDialog;
import sgs.env.ecabsdriver.model.FirestoreBroadcastChatModel;
import sgs.env.ecabsdriver.model.FirestoreChatModel;
import sgs.env.ecabsdriver.model.TripDataModel;
import sgs.env.ecabsdriver.model.TripInfo;
import sgs.env.ecabsdriver.reciver.FirestoreBroadcastReceiver;
import sgs.env.ecabsdriver.service.LocationService;
import sgs.env.ecabsdriver.service.RetrieveFirestoreData;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.PermissionUtils;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public abstract class BaseActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_APP_SETTINGS = 1;
    private static final int REQUEST_PERMISSION_CODE = 5;
    public boolean isActivityFinished;
    private ProgressDialog progressDialog;
    private static Drawable mBackButton = null;
    private Dialog dialog;
    private static final String TAG = "BaseActivity";

    private final long UPDATE_INTERVAL = 10000;  /* 10 secs */
    private final long FASTEST_INTERVAL = 10000; /* 10 sec */
    GoogleApiClient mGoogleClient;
    LocationRequest locationRequest;
    public Location mLastLocation;
    private int count;

    android.app.AlertDialog alertDialog, informationAlertDialogue, acknowledgeAlertDialogue, responseAlertDialogue;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 10000;
    private ListenerRegistration removeDocRef;
    FirestoreBroadcastChatModel broadcastAllMessageList;
    MediaPlayer mediaPlayer;

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();
        }
       stopMediaPlayer();
    }

    private void stopMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onStart() {
        if (mGoogleClient != null)
            mGoogleClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (mGoogleClient != null)
            mGoogleClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isGooglePlayServicesAvailable()) {
            showDialog();
            return;
        }
        if (!isLocationEnabled())
            showAlert();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mGoogleClient = new GoogleApiClient.Builder(this)
                .addApi(SafetyNet.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        registerReceiver(logoutbroadcastReceiver, new IntentFilter(AppConstants.logoutFromFirebase));
        registerReceiver(messageBroadcastReceiver, new IntentFilter(AppConstants.allMessageList));
        registerReceiver(InfoBroadcast, new IntentFilter(AppConstants.InfoBroadcast));
        registerReceiver(AckBroadcast, new IntentFilter(AppConstants.AckBroadcast));
        registerReceiver(ResBroadcast, new IntentFilter(AppConstants.ResBroadcast));

        registerReceiver(SwVersionConfigBroadcastReceiver, new IntentFilter(AppConstants.SwVersionConfig));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFirestoreData();
    }

    private void getFirestoreData() {
        if (SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, "") != null && !SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, "").toString().isEmpty()) {

            if (!RetrieveFirestoreData.isServiceRunning) {
                Intent intent = new Intent(getApplicationContext(), RetrieveFirestoreData.class);
                if (!ECabsApp.isBackgroundRunning(this)) {
                    startService(intent);
                }
            }
        }

    }

    BroadcastReceiver logoutbroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            logoutFromFirebase();

        }
    };

    BroadcastReceiver messageBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            FirestoreChatModel firestoreChatModels = intent.getParcelableExtra(AppConstants.allMessageList);
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
                alertDialog = null;
            }

            showMessagePopup(firestoreChatModels);

        }
    };

    BroadcastReceiver InfoBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            broadcastAllMessageList = intent.getParcelableExtra(AppConstants.broadcastAllMessageList);
            if (informationAlertDialogue != null && informationAlertDialogue.isShowing()) {
                informationAlertDialogue.dismiss();
                informationAlertDialogue = null;
            }

            showInformationMessagePopup(broadcastAllMessageList);
        }
    };

    BroadcastReceiver AckBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            broadcastAllMessageList = intent.getParcelableExtra(AppConstants.broadcastAllMessageList);
            if (acknowledgeAlertDialogue != null && acknowledgeAlertDialogue.isShowing()) {
                acknowledgeAlertDialogue.dismiss();
                acknowledgeAlertDialogue = null;
            }
            showAcknowledgeMessagePopup(broadcastAllMessageList);
        }
    };

    BroadcastReceiver ResBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            broadcastAllMessageList = intent.getParcelableExtra(AppConstants.broadcastAllMessageList);
            if (responseAlertDialogue != null && responseAlertDialogue.isShowing()) {
                responseAlertDialogue.dismiss();
                responseAlertDialogue = null;
            }
            showResponseMessagePopup(broadcastAllMessageList);
        }
    };

    BroadcastReceiver SwVersionConfigBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                    alertDialog = null;
                }
                showUpdateVersionPopUp(SharedPrefsHelper.getInstance().get(AppConstants.AppURL));

        }
    };

    private void showUpdateVersionPopUp(String androidAppUrl) {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.show_message_info, null);
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.MyDialogTheme);

        builder.setView(layout);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (!isFinishing()) {
            alertDialog.show();
        }

        TextView title = layout.findViewById(R.id.title);
        TextView messagetxt = layout.findViewById(R.id.messagetxt);
        TextView okbtn = layout.findViewById(R.id.okbtn);
        RelativeLayout cancle_relative = layout.findViewById(R.id.cancle_relative);
        cancle_relative.setVisibility(View.GONE);

        title.setText(getResources().getString(R.string.app_name));
        messagetxt.setText(R.string.envi_app_updation);

        okbtn.setText(getResources().getString(R.string.ok));

        okbtn.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(androidAppUrl)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(androidAppUrl)));
            }

        });
    }

    private void logoutFromFirebase() {
        SharedPrefsHelper.getInstance().clearAllData();
        SharedPrefsHelper.getInstance().save(AppConstants.ACTIVE, false);
        SharedPrefsHelper.getInstance().save(AppConstants.DRIVER_LOGIN, false);
        SharedPrefsHelper.getInstance().save(AppConstants.GEN_MASTER_ID, true);
        new ECabsDriverDbHelper(this).delBreak();
        new ECabsDriverDbHelper(this).delSoc();

        stopService(new Intent(this, LocationService.class));
        stopService(new Intent(this, RetrieveFirestoreData.class));
        NotificationManagerCompat.from(this).cancelAll();
        if(logoutDocumentRef!=null){
            logoutDocumentRef.delete();
            logoutDocumentRef = null;
        }

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void scheduledTripPopup(String fromAddress, String toAddress, Integer initialPrice, String date, String passengerName, String passengerPhone) {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.scheduled_trip_popup, null);
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.MyDialogTheme);

        builder.setView(layout);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (!isFinishing()) {
            alertDialog.show();
        }

        TextView title = layout.findViewById(R.id.title);
        TextView ride_desc = layout.findViewById(R.id.ride_desc);
        TextView confirmbtn = layout.findViewById(R.id.confirmbtn);
        TextView from_address = layout.findViewById(R.id.from_address);
        TextView to_address = layout.findViewById(R.id.to_address);
        TextView estimated_price = layout.findViewById(R.id.estimated_price);
        TextView date_time = layout.findViewById(R.id.date_time);
        RelativeLayout cancle_relative = layout.findViewById(R.id.cancle_relative);
        TextView userPhone = layout.findViewById(R.id.userphone);
        TextView userName = layout.findViewById(R.id.username);

        userName.setText("Passenger Name:- " + passengerName);
        userPhone.setText("Contact Number:- " + passengerPhone);
        from_address.setText(fromAddress);
        to_address.setText(toAddress);
        estimated_price.setText("₹" + String.valueOf(initialPrice));
        date_time.setText("for " + date);

        cancle_relative.setOnClickListener(v -> alertDialog.dismiss());
        confirmbtn.setOnClickListener(v -> alertDialog.dismiss());

    }

    private void showMessagePopup(FirestoreChatModel allMessageList) {

        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.show_message_info, null);
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.MyDialogTheme);

        builder.setView(layout);
        builder.setCancelable(true);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (!this.isFinishing()) {
            alertDialog.show();
        }

        TextView messagetxt = layout.findViewById(R.id.messagetxt);
        LinearLayout playBtnLinear = layout.findViewById(R.id.playBtnLinear);
        TextView playBtn = layout.findViewById(R.id.playBtn);
        TextView okbtn = layout.findViewById(R.id.okbtn);
        RelativeLayout cancle_relative = layout.findViewById(R.id.cancle_relative);

        if (allMessageList.getMessageType().equals(AppConstants.MessageTypeText)) {
            messagetxt.setText(allMessageList.getMessage());
        } else if (allMessageList.getMessageType().equals(AppConstants.MessageTypeImage)) {
            messagetxt.setText(R.string.received_image_from_admin);
            playBtnLinear.setVisibility(View.VISIBLE);
            playBtn.setText(R.string.viewImage);
        } else if (allMessageList.getMessageType().equals(AppConstants.MessageTypeAudio)) {
            messagetxt.setText(R.string.received_audio_from_admin);
            playBtnLinear.setVisibility(View.VISIBLE);
            playBtn.setText(R.string.playAudio);
        } else if (allMessageList.getMessageType().equals(AppConstants.MessageTypeVideo)) {
            messagetxt.setText(R.string.received_video_from_admin);
            playBtnLinear.setVisibility(View.VISIBLE);
            playBtn.setText(R.string.playVideo);
        }
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allMessageList.getMessageType().equals(AppConstants.MessageTypeImage)) {
                    Intent i = new Intent(BaseActivity.this, PhotoViewerActivity.class);
                    i.putExtra("url", allMessageList.getMessage());
                    startActivity(i);
                } else if (allMessageList.getMessageType().equals(AppConstants.MessageTypeAudio)) {
                    showMediaPlayerDialog(allMessageList.getMessage());
                } else if (allMessageList.getMessageType().equals(AppConstants.MessageTypeVideo)) {
                    Intent intent = new Intent(BaseActivity.this, MediaPlayerActivity.class);
                    intent.putExtra("url", allMessageList.getMessage());
                    startActivity(intent);
                }
            }
        });

        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Map<String, Object> data = new HashMap<>();

                data.put("driverId", SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, ""));
                data.put("time", new Date());
                Map<String, Object> dummyMap = new HashMap<>();

                DocumentReference df = FirebaseFirestore.getInstance().collection("chat_service").document("drv_" + SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, ""))
                        .collection("chat_list").document("admin").collection("msg")
                        .document(allMessageList.getDocId().trim());
                df.collection("messageReaded").add(data)
                        .addOnSuccessListener(documentReference -> {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        })
                        .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
            }
        });
        cancle_relative.setOnClickListener(v -> alertDialog.dismiss());
    }

    private void showInformationMessagePopup(FirestoreBroadcastChatModel allMessageList) {
        if (informationAlertDialogue != null && informationAlertDialogue.isShowing()) {
            informationAlertDialogue.dismiss();
            informationAlertDialogue = null;
        }
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.show_message_info, null);
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.MyDialogTheme);

        builder.setView(layout);
        builder.setCancelable(false);
        informationAlertDialogue = builder.create();
        informationAlertDialogue.setCanceledOnTouchOutside(false);
        informationAlertDialogue.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (!isFinishing()) {
            informationAlertDialogue.show();
        }

        TextView title = layout.findViewById(R.id.title);
        TextView messagetxt = layout.findViewById(R.id.messagetxt);
        LinearLayout playBtnLinear = layout.findViewById(R.id.playBtnLinear);
        TextView playBtn = layout.findViewById(R.id.playBtn);
        TextView okbtn = layout.findViewById(R.id.okbtn);
        RelativeLayout cancle_relative = layout.findViewById(R.id.cancle_relative);

        title.setText(getResources().getString(R.string.broadcast_message));

        if (allMessageList.getMessageType().equals(AppConstants.MessageTypeText)) {
            messagetxt.setText(allMessageList.getMessage());
        } else if (allMessageList.getMessageType().equals(AppConstants.MessageTypeImage)) {
            messagetxt.setText(R.string.received_image_from_admin);
            playBtnLinear.setVisibility(View.VISIBLE);
            playBtn.setText(R.string.viewImage);
        } else if (allMessageList.getMessageType().equals(AppConstants.MessageTypeAudio)) {
            messagetxt.setText(R.string.received_audio_from_admin);
            playBtnLinear.setVisibility(View.VISIBLE);
            playBtn.setText(R.string.playAudio);
        } else if (allMessageList.getMessageType().equals(AppConstants.MessageTypeVideo)) {
            messagetxt.setText(R.string.received_video_from_admin);
            playBtnLinear.setVisibility(View.VISIBLE);
            playBtn.setText(R.string.playVideo);
        }

        okbtn.setText("Ok");

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allMessageList.getMessageType().equals(AppConstants.MessageTypeImage)) {
                    Intent i = new Intent(BaseActivity.this, PhotoViewerActivity.class);
                    i.putExtra("url", allMessageList.getMessage());
                    startActivity(i);
                } else if (allMessageList.getMessageType().equals(AppConstants.MessageTypeAudio)) {
                    showMediaPlayerDialog(allMessageList.getMessage());
                } else if (allMessageList.getMessageType().equals(AppConstants.MessageTypeVideo)) {
                    Intent intent = new Intent(BaseActivity.this, MediaPlayerActivity.class);
                    intent.putExtra("url", allMessageList.getMessage());
                    startActivity(intent);
                }
            }
        });

        okbtn.setOnClickListener(v -> {
            informationAlertDialogue.cancel();
            informationAlertDialogue.dismiss();

            Map<String, Object> data = new HashMap<>();
            data.put("Name", SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_NAME, ""));
            data.put("driverId", SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, ""));
            data.put("serviceType", allMessageList.getServiceType().trim());
            data.put("time", new Date());
            Map<String, Object> dummyMap = new HashMap<>();

            DocumentReference df = FirebaseFirestore.getInstance().collection("broadcast_service")
                    .document("admin").collection("all_driver")
                    .document(allMessageList.getDoc_id().trim());
            df.collection("Information").add(data)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));

        });

        cancle_relative.setOnClickListener(v -> informationAlertDialogue.dismiss());
    }

    private void showAcknowledgeMessagePopup(FirestoreBroadcastChatModel allMessageList) {
        if (acknowledgeAlertDialogue != null && acknowledgeAlertDialogue.isShowing()) {
            acknowledgeAlertDialogue.dismiss();
            acknowledgeAlertDialogue = null;
        }
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.show_message_info, null);
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.MyDialogTheme);

        builder.setView(layout);
        builder.setCancelable(false);
        acknowledgeAlertDialogue = builder.create();
        acknowledgeAlertDialogue.setCanceledOnTouchOutside(false);
        acknowledgeAlertDialogue.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (!isFinishing()) {
            acknowledgeAlertDialogue.show();
        }
        TextView title = layout.findViewById(R.id.title);
        TextView messagetxt = layout.findViewById(R.id.messagetxt);
        LinearLayout playBtnLinear = layout.findViewById(R.id.playBtnLinear);
        TextView playBtn = layout.findViewById(R.id.playBtn);
        TextView okbtn = layout.findViewById(R.id.okbtn);
        RelativeLayout cancle_relative = layout.findViewById(R.id.cancle_relative);

        title.setText(getResources().getString(R.string.broadcast_message));

        if (allMessageList.getMessageType().equals(AppConstants.MessageTypeText)) {
            messagetxt.setText(allMessageList.getMessage());
        } else if (allMessageList.getMessageType().equals(AppConstants.MessageTypeImage)) {
            messagetxt.setText(R.string.received_image_from_admin);
            playBtnLinear.setVisibility(View.VISIBLE);
            playBtn.setText(R.string.viewImage);
        } else if (allMessageList.getMessageType().equals(AppConstants.MessageTypeAudio)) {
            messagetxt.setText(R.string.received_audio_from_admin);
            playBtnLinear.setVisibility(View.VISIBLE);
            playBtn.setText(R.string.playAudio);
        } else if (allMessageList.getMessageType().equals(AppConstants.MessageTypeVideo)) {
            messagetxt.setText(R.string.received_video_from_admin);
            playBtnLinear.setVisibility(View.VISIBLE);
            playBtn.setText(R.string.playVideo);
        }

        okbtn.setText("Acknowledge");

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allMessageList.getMessageType().equals(AppConstants.MessageTypeImage)) {
                    Intent i = new Intent(BaseActivity.this, PhotoViewerActivity.class);
                    i.putExtra("url", allMessageList.getMessage());
                    startActivity(i);
                } else if (allMessageList.getMessageType().equals(AppConstants.MessageTypeAudio)) {
                    showMediaPlayerDialog(allMessageList.getMessage());
                } else if (allMessageList.getMessageType().equals(AppConstants.MessageTypeVideo)) {
                    Intent intent = new Intent(BaseActivity.this, MediaPlayerActivity.class);
                    intent.putExtra("url", allMessageList.getMessage());
                    startActivity(intent);
                }
            }
        });

        okbtn.setOnClickListener(v -> {
            acknowledgeAlertDialogue.cancel();
            acknowledgeAlertDialogue.dismiss();

            Map<String, Object> data = new HashMap<>();
            data.put("Name", SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_NAME, ""));
            data.put("driverId", SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, ""));
            data.put("serviceType", allMessageList.getServiceType().trim());
            data.put("time", new Date());
            Map<String, Object> dummyMap = new HashMap<>();

            DocumentReference df = FirebaseFirestore.getInstance().collection("broadcast_service")
                    .document("admin").collection("all_driver")
                    .document(allMessageList.getDoc_id().trim());
            df.collection("Acknowledgment").add(data)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));

        });

        cancle_relative.setOnClickListener(v -> acknowledgeAlertDialogue.dismiss());
    }

    private void showResponseMessagePopup(FirestoreBroadcastChatModel allMessageList) {
        if (responseAlertDialogue != null && responseAlertDialogue.isShowing()) {
            responseAlertDialogue.dismiss();
            responseAlertDialogue = null;
        }
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.show_message_info, null);
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.MyDialogTheme);

        builder.setView(layout);
        builder.setCancelable(false);
        responseAlertDialogue = builder.create();
        responseAlertDialogue.setCanceledOnTouchOutside(false);
        responseAlertDialogue.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (!isFinishing()) {
            responseAlertDialogue.show();
        }

        TextView title = layout.findViewById(R.id.title);
        TextView messagetxt = layout.findViewById(R.id.messagetxt);
        EditText responseExt = layout.findViewById(R.id.responseExt);
        LinearLayout playBtnLinear = layout.findViewById(R.id.playBtnLinear);
        TextView playBtn = layout.findViewById(R.id.playBtn);
        TextView okbtn = layout.findViewById(R.id.okbtn);
        RelativeLayout cancle_relative = layout.findViewById(R.id.cancle_relative);

        title.setText(getResources().getString(R.string.broadcast_message));

        if (allMessageList.getMessageType().equals(AppConstants.MessageTypeText)) {
            messagetxt.setText(allMessageList.getMessage());
        } else if (allMessageList.getMessageType().equals(AppConstants.MessageTypeImage)) {
            messagetxt.setText(R.string.received_image_from_admin);
            playBtnLinear.setVisibility(View.VISIBLE);
            playBtn.setText(R.string.viewImage);
        } else if (allMessageList.getMessageType().equals(AppConstants.MessageTypeAudio)) {
            messagetxt.setText(R.string.received_audio_from_admin);
            playBtnLinear.setVisibility(View.VISIBLE);
            playBtn.setText(R.string.playAudio);
        } else if (allMessageList.getMessageType().equals(AppConstants.MessageTypeVideo)) {
            messagetxt.setText(R.string.received_video_from_admin);
            playBtnLinear.setVisibility(View.VISIBLE);
            playBtn.setText(R.string.playVideo);
        }

        responseExt.setVisibility(View.VISIBLE);
        okbtn.setText("Sent");

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allMessageList.getMessageType().equals(AppConstants.MessageTypeImage)) {
                    Intent i = new Intent(BaseActivity.this, PhotoViewerActivity.class);
                    i.putExtra("url", allMessageList.getMessage());
                    startActivity(i);
                } else if (allMessageList.getMessageType().equals(AppConstants.MessageTypeAudio)) {
                    showMediaPlayerDialog(allMessageList.getMessage());
                } else if (allMessageList.getMessageType().equals(AppConstants.MessageTypeVideo)) {
                    Intent intent = new Intent(BaseActivity.this, MediaPlayerActivity.class);
                    intent.putExtra("url", allMessageList.getMessage());
                    startActivity(intent);
                }
            }
        });

        okbtn.setOnClickListener(v -> {
            if (responseExt.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please write message firstly!", Toast.LENGTH_SHORT).show();
            } else {
                responseAlertDialogue.cancel();
                responseAlertDialogue.dismiss();
                Map<String, Object> data = new HashMap<>();
                data.put("Name", SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_NAME, ""));
                data.put("driverId", SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, ""));
                data.put("serviceType", allMessageList.getServiceType().trim());
                data.put("ResponseMessage", responseExt.getText().toString().trim());
                data.put("time", new Date());
                Map<String, Object> dummyMap = new HashMap<>();

                DocumentReference df = FirebaseFirestore.getInstance().collection("broadcast_service")
                        .document("admin").collection("all_driver")
                        .document(allMessageList.getDoc_id().trim());
                df.collection("Response").add(data)
                        .addOnSuccessListener(documentReference -> {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        })
                        .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
            }

        });

        cancle_relative.setOnClickListener(v -> responseAlertDialogue.dismiss());
    }

    private void showMediaPlayerDialog(String message) {

        Drawable playButton = ContextCompat.getDrawable(this, R.drawable.ic_baseline_play_arrow_24);
        Drawable pauseButton = ContextCompat.getDrawable(this, R.drawable.ic_baseline_pause_24);

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_audio_play);

        ImageView buttonPlayPause = dialog.findViewById(R.id.playButton);
        ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        ProgressBar progress_loader = dialog.findViewById(R.id.progress_loader);

        progressBar.setProgress(0);

        buttonPlayPause.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                buttonPlayPause.setImageDrawable(playButton);
            } else {
                mediaPlayer.start();
                buttonPlayPause.setImageDrawable(pauseButton);
            }
        });

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(message);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    progress_loader.setVisibility(View.GONE);
                    buttonPlayPause.setImageDrawable(pauseButton);
                    mediaPlayer.start();
                    progressBar.setMax(mediaPlayer.getDuration() / 1000);
                    final Handler mHandler = new Handler();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mediaPlayer != null) {
                                int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                                progressBar.setProgress(mCurrentPosition);
                                Log.e("progress", "" + mCurrentPosition);
                            }
                            if (progressBar.getProgress() == progressBar.getMax()) {
                                mHandler.removeCallbacks(this);
                            }
                            mHandler.postDelayed(this, 1000);
                        }
                    });
                }
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                buttonPlayPause.setImageDrawable(playButton);
                mediaPlayer.stop();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setOnDismissListener(dialog1 -> {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        });

        dialog.show();
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is 'Off'")
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        refresh();
    }

    @Override
    protected void onDestroy() {
        hideKeyboard();
        try {
            trimCache(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (removeDocRef != null) {
            removeDocRef.remove();
            removeDocRef = null;
        }
        stopMediaPlayer();
        removeBroadcastReceivers();
        isActivityFinished = true;
        finish();
        super.onDestroy();

    }

    private void removeBroadcastReceivers() {
        if (InfoBroadcast != null) {
            unregisterReceiver(InfoBroadcast);
        }
        if (AckBroadcast != null) {
            unregisterReceiver(AckBroadcast);
        }
        if (ResBroadcast != null) {
            unregisterReceiver(ResBroadcast);
        }
        if (logoutbroadcastReceiver != null) {
            unregisterReceiver(logoutbroadcastReceiver);
        }
        if (messageBroadcastReceiver != null) {
            unregisterReceiver(messageBroadcastReceiver);
        }
        if(SwVersionConfigBroadcastReceiver!=null){
            unregisterReceiver(SwVersionConfigBroadcastReceiver);
        }

    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
            return dir.delete();
        } else {
            return false;
        }
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        setActionBar();
    }

    protected Toolbar getToolbar() {
        return null;
    }

    private void setActionBar() {
        Toolbar toolbar = getToolbar();
        if (toolbar != null) {

            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setSubtitleTextColor(Color.WHITE);

            setSupportActionBar(toolbar);

            if (mBackButton == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mBackButton = ContextCompat.getDrawable(getApplicationContext(), R.drawable.iosback);
                } else {
                    mBackButton = ContextCompat.getDrawable(getApplicationContext(), R.drawable.iosback);
                }
                mBackButton.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                mBackButton.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            }
            Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(mBackButton);
        }
    }

    protected abstract void refresh();

    @SuppressLint("MissingPermission")
    public boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        assert connectivityManager != null;
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    protected abstract int setLayoutIfneeded();

    protected abstract int getColor();

    protected void showDialog() {
        ShowDialog showDialog = new ShowDialog(this, "Google play service not available ", "Device is not supported") {
            @Override
            public void setAction() {
                finish();
            }
        };
        showDialog.setCanceledOnTouchOutside(false);
        showDialog.setCancelable(false);
        showDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        showDialog.show();
    }

    public boolean isGooglePlayServicesAvailable() {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.d(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        Log.d(TAG, "This device is  supported.");
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startUpatingLocation();
    }

    @SuppressLint("MissingPermission")
    private void startUpatingLocation() {
        boolean granted = PermissionUtils.checkLocationPermission(this);  // runtimePermis is granted
        if (granted) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleClient, locationRequest, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    mLastLocation = location;
                    retrivedLocation(mLastLocation);

                    if (!LocationService.isServiceRunning) {
                        Intent intent = new Intent(getApplicationContext(), LocationService.class);
                        startService(intent);
                    }

                }
            });
        } else {
            Toast.makeText(this, "Enable Location permission", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_CODE);
            count++;
        }
    }

    protected abstract void retrivedLocation(Location location);

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(BaseActivity.this, "onConnectionFailed: \n" + connectionResult.toString(),
                Toast.LENGTH_LONG).show();
        Log.d(TAG, connectionResult.toString());
    }

    private void goToSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(myAppSettings, REQUEST_APP_SETTINGS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (PermissionUtils.verifyPermissions(grantResults)) {
                Log.d(TAG, "onRequestPermissionsResult: true");
            } else {
                if (count < 2) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Please enable the location permmision to use the app");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(BaseActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_CODE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                    count++;
                } else {
                    goToSettings();
                }
            }
        }
    }

    public void disablebuttons(Button button) {
        button.setEnabled(false);
        button.setClickable(false);
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                try {
                    button.setEnabled(true);
                    button.setClickable(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, delay);

    }

}
