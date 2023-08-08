package sgs.env.ecabsdriver.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.data.local.ECabsDriverDbHelper;
import sgs.env.ecabsdriver.interfce.ValidateDriverI;
import sgs.env.ecabsdriver.model.AppConfig;
import sgs.env.ecabsdriver.model.CheckDriverTripStatus;
import sgs.env.ecabsdriver.model.CustomerNotfication;
import sgs.env.ecabsdriver.model.FirebaseDriverStatus;
import sgs.env.ecabsdriver.presenter.ValidateDriverPresenter;
import sgs.env.ecabsdriver.reciver.FirestoreBroadcastReceiver;
import sgs.env.ecabsdriver.service.RetrieveFirestoreData;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.MediaPlayerSingleton;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;
import sgs.env.ecabsdriver.util.ECabsApp;


public class SplashScreen extends AppCompatActivity {
    private final String TAG = "RetrieveFirestoreData";
    public static DocumentReference appConfigRef;
    ListenerRegistration appConfigEventListner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        FirebaseApp.initializeApp(this);
        changeStatusBarColor();

    }

    @Override
    protected void onResume() {
        super.onResume();
        retriveFirestoreData();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (appConfigEventListner != null) {
            appConfigEventListner.remove();
            appConfigEventListner = null;
        }

    }

    private void retriveFirestoreData() {
        if (!RetrieveFirestoreData.isServiceRunning) {
            Intent intent = new Intent(getApplicationContext(), RetrieveFirestoreData.class);
            if (!ECabsApp.isBackgroundRunning(this)) {
                startService(intent);
            }
        }

        appConfigRef = FirebaseFirestore.getInstance().collection("settings").document("AppConfig");
        appConfigEventListner = appConfigRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());

                    AppConfig appConfig = snapshot.toObject(AppConfig.class);
                    if (appConfig != null) {
                        try {
                            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                            if (pInfo != null && appConfig.getSwVersionConfig().getMinDriverAppVersion() != null
                                    && !appConfig.getSwVersionConfig().getMinDriverAppVersion().toString().isEmpty()) {
                                Log.e("versionCode", String.valueOf(pInfo.versionCode));
                                Log.e("MinDriverAppVersion", String.valueOf(appConfig.getSwVersionConfig().getMinDriverAppVersion()));

                                if (pInfo.versionCode < appConfig.getSwVersionConfig().getMinDriverAppVersion()) {
                                    SharedPrefsHelper.getInstance().save(AppConstants.AppURL, appConfig.getSwVersionConfig().getDriverAppUrl());
                                    Intent intent = new Intent(SplashScreen.this,SwVersionCheckActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    validateAndLoginMethod();
                                }
                            }


                            if (appConfig.getSearchConfig() != null) {
                                SharedPrefsHelper.getInstance().save(AppConstants.searchFrequency,
                                        appConfig.getSearchConfig().getSearchFrequency());
                                SharedPrefsHelper.getInstance().save(AppConstants.seacrhMinDistance,
                                        appConfig.getSearchConfig().getSeacrhMinDistance());
                                SharedPrefsHelper.getInstance().save(AppConstants.MinSoc,
                                        String.valueOf(appConfig.getSearchConfig().getMinSoc()));

                            }
                        } catch (PackageManager.NameNotFoundException exception) {
                            exception.printStackTrace();
                        }

                    }

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

    }


    public void validateAndLoginMethod() {
        ValidateDriverI validate = new ValidateDriverPresenter();
        boolean loginStatus = SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_LOGIN,false);
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String phone = SharedPrefsHelper.getInstance().get(AppConstants.MOBILE_NUM);
        CheckDriverTripStatus checkDriverTripStatus = new CheckDriverTripStatus(phone,deviceId);

        if (loginStatus) {
            if (ECabsApp.isNetworkAvailable(SplashScreen.this)) {
               validate.getDriverTripStatus(SplashScreen.this,checkDriverTripStatus);
            } else {
                Log.d("TAG", "validateAndLoginMethod: no network ");
            }
        } else {
            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            finish();

        }
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
        }
    }
}
