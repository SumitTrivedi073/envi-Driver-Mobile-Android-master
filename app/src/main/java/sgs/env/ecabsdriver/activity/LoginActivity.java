package sgs.env.ecabsdriver.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.animations.AntimationHelper;
import sgs.env.ecabsdriver.animations.EnumHolder;
import sgs.env.ecabsdriver.data.local.ECabsDriverDbHelper;
import sgs.env.ecabsdriver.interfce.RegisterService;
import sgs.env.ecabsdriver.model.Address;
import sgs.env.ecabsdriver.model.Login;
import sgs.env.ecabsdriver.model.LoginResponse;
import sgs.env.ecabsdriver.model.SocValues;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.PermissionUtils;
import sgs.env.ecabsdriver.util.ProgressBarLayout;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class LoginActivity extends AppCompatActivity {

    private static final int IMMEDIATE_APP_UPDATE_REQ_CODE = 124;

    private static final int REQUEST_PERMISSION_CODE = 5;

    private static final String TAG = "LoginActivity";

    private final int MY_REQUEST_CODE = 1;

    private int count;

    private ActionBar actionBar;

    private ImageView mBackgroungImage, mPhoneImage, editPhonenumber;

    private TextView mTitleVerify, mTitleEula, mTvsentNumber, mPhoneNumberResend;

    private EditText etCountryName, etCountryCode, etPhoneNumber;

    private Button btnLogin;

    private EditText mCountryCodeResend;

    private ProgressBarLayout layout;

    private static long mLastClickTime = 0;

    private String mSelectedcountrycode, FirebaseToken, userPhoneNumber;

    private SharedPrefsHelper helper;

    private ECabsDriverDbHelper dbHelper;

    private AppUpdateManager appUpdateManager;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                finishAffinity();
            }
        }
        if (requestCode == IMMEDIATE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
                //	Toast.makeText(getApplicationContext(),"Update canceled by user! Result Code:
                //	" + resultCode, Toast.LENGTH_LONG).show();
                Toast.makeText(LoginActivity.this, "please update and enjoy the app",
                        Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_OK) {
                Toast.makeText(LoginActivity.this, "App update success", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(getApplicationContext(),"Update Failed! Result Code: " +
                // resultCode, Toast.LENGTH_LONG).show();
                checkUpdate();
            }
            //In app Update link
            //https://www.section.io/engineering-education/android-application-in-app-update-using
            // -android-studio/
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        callInAppUpdate();
        if (!isLocationEnabled()) {
            showAlert();
        }
        checkLocationRunTimePermssion();
        dbHelper = new ECabsDriverDbHelper(LoginActivity.this);
        layout = new ProgressBarLayout();
        helper = SharedPrefsHelper.getInstance();
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Login Activity");
        }
        AntimationHelper.with(EnumHolder.BounceInUp).duration(1500).interpolate(
                new AccelerateDecelerateInterpolator()).repeat(3).setView(
                findViewById(R.id.phoneicon));
        intialiseControls();
        getRuntimePermission();

        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (resultCode == ConnectionResult.SUCCESS) {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(
                    task -> {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed",
                                    task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        FirebaseToken = task.getResult();
                        Log.e("newToken", FirebaseToken);
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        checkUpdate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (PermissionUtils.verifyPermissions(grantResults)) {
                Log.d(TAG, "onRequestPermissionsResult: true");
            } else {
                if (count < 2) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Location permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes,
                            (dialog, which) -> ActivityCompat.requestPermissions(LoginActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_PERMISSION_CODE));
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                    count++;
                } else {
                    finish();
                }
            }
        }
    }

    private void callInAppUpdate() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
        com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask =
                appUpdateManager.getAppUpdateInfo();
        try {
            appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        // For a flexible update, use AppUpdateType.FLEXIBLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    // Request the update.
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                appUpdateInfo,
                                // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                AppUpdateType.IMMEDIATE,
                                // The current activity making the update request.
                                this,
                                // Include a request code to later monitor this update request.
                                MY_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            });
            appUpdateInfoTask.addOnFailureListener(e -> Toast.makeText(LoginActivity.this,
                    "Auto update failed. Please update your app from the play store",
                    Toast.LENGTH_LONG));
        } catch (Exception e) {
            displayAlertDialog("Exception in force update",
                    e.getMessage());
        }
    }

    public void displayAlertDialog(String title, String message) {
        new android.app.AlertDialog.Builder(LoginActivity.this)
                .setTitle(title)
                .setMessage(message)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        dialog.cancel();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                //.setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(this.getContentResolver(),
                        Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(this.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location").setMessage(
                "Your Locations Settings is 'Off'").setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                dialog.create().dismiss();
            }
        });
        dialog.show();
    }

    private void checkLocationRunTimePermssion() {
        if (PermissionUtils.checkLocationPermission(this)) {
            Log.d(TAG, "onCreate: permissions granted");
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSION_CODE);
            count++;
        }
    }

    private void intialiseControls() {
        //for send otp ui initialisation
        mBackgroungImage = findViewById(R.id.backgroundImage);
        mPhoneImage = findViewById(R.id.phoneicon);
        mTitleVerify = findViewById(R.id.titleverifymobile);
        mTitleEula = findViewById(R.id.tvtitleeula);
        etCountryName = findViewById(R.id.etcountryname);
        etCountryCode = findViewById(R.id.tv_country_code);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        btnLogin = findViewById(R.id.btnsendotp);
        etCountryName.setKeyListener(null);
        etCountryCode.setKeyListener(null);
        mTvsentNumber = findViewById(R.id.tvphonenumber);
        editPhonenumber = findViewById(R.id.editphonenumber);
        mCountryCodeResend = findViewById(R.id.etcountrycode);
        mPhoneNumberResend = findViewById(R.id.etphonenumber);
        editPhonenumber.setOnClickListener(view -> {
            String mCode = mCountryCodeResend.getText().toString().trim();
            String mPhone = mPhoneNumberResend.getText().toString().trim();
            if (mCode.length() == 3) {
                mSelectedcountrycode = mCode;
            }
            if (userPhoneNumber != null && userPhoneNumber.length() == 10) {
                userPhoneNumber = mPhone;
            }
            visiblePhoneVerficationScreen(View.GONE);
        });
        // otp not required for driver
        btnLogin.setOnClickListener(view -> {
            if (validatePhoneNumber()) {
                boolean location = isLocationEnabled();
                if (!location)
                    showAlert();
                else
                    sendToNextActivity();
            } else {
                Toast.makeText(LoginActivity.this, "Enter valid mobileNumber!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRuntimePermission() {
        ActivityCompat.requestPermissions(LoginActivity.this,
                new String[]{Manifest.permission.READ_SMS}, 1);
    }

    private void visiblePhoneVerficationScreen(int gone) {
        etCountryName.setVisibility(View.VISIBLE);
        etCountryCode.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.VISIBLE);
        mTitleVerify.setVisibility(View.VISIBLE);
        etPhoneNumber.setVisibility(View.VISIBLE);
        mTitleEula.setVisibility(View.VISIBLE);
        mBackgroungImage.setVisibility(View.VISIBLE);
        mPhoneImage.setVisibility(View.VISIBLE);
        if (gone == View.GONE) {
            mTvsentNumber.setText("Please enter your Mobile Number ");
            mCountryCodeResend.setText(mSelectedcountrycode);

        }
    }

    private boolean validatePhoneNumber() {
        userPhoneNumber = etPhoneNumber.getText().toString().trim();
        if (TextUtils.isEmpty(userPhoneNumber)) {
            Snackbar.make(findViewById(android.R.id.content), "phone number cannot be empty.",
                    Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (userPhoneNumber.length() != 10) {
            Snackbar.make(findViewById(android.R.id.content), "Enter the Valid phonenumber",
                    Snackbar.LENGTH_SHORT).show();
            return false;
        }
        mPhoneNumberResend.setText(userPhoneNumber);
        return true;
    }

    private void sendToNextActivity() {
        boolean internet = PermissionUtils.isNetworkConnected(this);
        if (internet) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 10000) { // 1000 = 1second
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            loginAPI();
        } else {
            Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    private void loginAPI() {
        if (validatePhoneNumber()) {
            layout.displayDialog(this);
            Login login = new Login();
            RegisterService loginAPI = ECabsApp.getRetrofit().create(RegisterService.class);
            String phoneNum = etPhoneNumber.getText().toString().trim();
            login.setCountrycode("+91");
            login.setPhone(phoneNum);
            String deviceId =
                    Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            login.setDeviceId(deviceId);
            SharedPrefsHelper.getInstance().save(AppConstants.MOBILE_NUM, phoneNum);
            login.setFcmToken(FirebaseToken);
            Log.e("inputLogin=====>", login.toString());
            Call<LoginResponse> loginResponseCall = loginAPI.login(login);
            Log.d(TAG, "loginAPI: " + login);
            Log.d(TAG, "loginAPI: url " + loginResponseCall.request().url());
            loginResponseCall.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(@NonNull Call<LoginResponse> call,
                                       @NonNull Response<LoginResponse> response) {
                    layout.hideProgressDialog();
                    if (response.code() == 400) {
                        try {
                            String message = response.errorBody().string();
                            Log.d(TAG, "failure: " + message);
                            if (message.contentEquals(
                                    "{\"message\":\"you cannot login from other device\"}")) {
                                Toast.makeText(LoginActivity.this,
                                        "you cannot login from other device",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                displayAlertDialog("",
                                        "Unable to login. Please contact Operations team");
                                layout.hideProgressDialog();
                                return;
                            }
                        } catch (IOException e) {
                            Log.d(TAG, "onResponse: ex " + e.getMessage());
                        }
                    }
                    Log.d(TAG, "onResponse: code " + response.body());
                    if (response.isSuccessful()) {
                        LoginResponse loginResponse = response.body();
                        Log.e("login_response", String.valueOf(response.body()));
                        if (loginResponse != null) {
                            sgs.env.ecabsdriver.model.Response serData =
                                    loginResponse.getContent();
                            sgs.env.ecabsdriver.model.ResponseConfig serConfig =
                                    loginResponse.getConfig();
                            String token = serData.getToken();
                            String driverName = serData.getName();
                            String id = serData.getId();
                            Address address = serData.getAddress();
                            if (serData.getVehicle() != null) {
                                String vehicleNum = serData.getVehicle().getNumber();
                                String pic = serData.getPic();
                                helper.save(AppConstants.FirstLogin, true);
                                helper.save(AppConstants.KEY_JWT_TOKEN, token);
                                helper.save(AppConstants.DRIVER_NAME, driverName);
                                helper.save(AppConstants.DRIVER_VEH_NO, vehicleNum);
                                helper.save(AppConstants.DRIVER_ID, id);
                                helper.save(AppConstants.PIC, pic);
                                helper.save(AppConstants.DRIVER_LOGIN, true);
                                helper.save(AppConstants.DRV_VEH_ID, serData.getVehicle().getId());

                                helper.save(AppConstants.DRV_MODEL,
                                        serData.getVehicle().getModel());
                                helper.save(AppConstants.COUNTRY, address.getCountry());
                                helper.save(AppConstants.STATE, address.getState());
                                helper.save(AppConstants.CITY, address.getCity());
                                helper.save(AppConstants.PINCODE, address.getPincode());
                                helper.save(AppConstants.ADDRESS, address.getAddress1());
                                helper.save(AppConstants.ACTIVE, true);
                                SocValues socValues = serData.getSocvalues();

                                if (socValues != null) {
                                    dbHelper.insertSoc(socValues);
                                }

                                helper.save(AppConstants.GEN_MASTER_ID, true);
                                dbHelper.insertDriverBreakTime(AppConstants.TEA_BREAK, 0);
                                dbHelper.insertDriverBreakTime(AppConstants.LUNCH_BREAK, 0);
                                helper.save(AppConstants.TOTAL_BREAK_COUNT, 0);
                            } else {
                                Toast.makeText(LoginActivity.this,
                                        "Unable to get vehicle details contact Malbork!",
                                        Toast.LENGTH_SHORT).show();
                            }
                          /*  if (serConfig.getSearchConfig() != null) {
                                helper.save(AppConstants.searchFrequency,
                                        serConfig.getSearchConfig().getSearchFrequency());
                                helper.save(AppConstants.seacrhMinDistance,
                                        serConfig.getSearchConfig().getSeacrhMinDistance());

                                helper.save(AppConstants.MinSoc,
                                        String.valueOf(serConfig.getSearchConfig().getMinSoc()));

                            }
                          */  SharedPrefsHelper.getInstance().save(AppConstants.ACCEPT, false);
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Login failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        try {
                            Toast.makeText(LoginActivity.this,
                                    "Unable to login !" + response.errorBody().string(),
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onResponse: " + response.errorBody().string());
                        } catch (IOException e) {
                            Log.d(TAG, "onResponse: exc " + e.getMessage());
                        }
                    }

                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<LoginResponse> call, @NonNull Throwable t) {
                    Toast.makeText(LoginActivity.this, "Unable to login",
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onFailure: " + t.getMessage());
                    if (!isFinishing()) {
                        layout.hideProgressDialog();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Not valid number", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUpdate() {
        com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask =
                appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(
                    AppUpdateType.IMMEDIATE)) {
                startUpdateFlow(appUpdateInfo);
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                startUpdateFlow(appUpdateInfo);
            }
        });
    }

    private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this,
                    IMMEDIATE_APP_UPDATE_REQ_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

}
