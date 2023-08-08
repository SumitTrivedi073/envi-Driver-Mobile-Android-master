package sgs.env.ecabsdriver.activity;

import static sgs.env.ecabsdriver.util.ECabsApp.HomeActivityPaused;
import static sgs.env.ecabsdriver.util.ECabsApp.HomeActivityResumed;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sgs.env.ecabsdriver.BuildConfig;
import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.adapter.HomeGridAdapter;
import sgs.env.ecabsdriver.data.local.ECabsDriverDbHelper;
import sgs.env.ecabsdriver.interfce.DrvMasterIDI;
import sgs.env.ecabsdriver.interfce.IPassengerTripMaster;
import sgs.env.ecabsdriver.interfce.IUserDeviceInfo;
import sgs.env.ecabsdriver.interfce.LogoutI;
import sgs.env.ecabsdriver.interfce.RegisterService;
import sgs.env.ecabsdriver.interfce.UImethodsI;
import sgs.env.ecabsdriver.interfce.UiMethods;
import sgs.env.ecabsdriver.interfce.UpdateDriverStatusI;
import sgs.env.ecabsdriver.model.ChargeStation;
import sgs.env.ecabsdriver.model.ChargingStationModel;
import sgs.env.ecabsdriver.model.CloseDay;
import sgs.env.ecabsdriver.model.DriverStatus;
import sgs.env.ecabsdriver.model.GeneralResponse;
import sgs.env.ecabsdriver.model.Home;
import sgs.env.ecabsdriver.model.InfoModelB;
import sgs.env.ecabsdriver.model.Location;
import sgs.env.ecabsdriver.model.Location1;
import sgs.env.ecabsdriver.model.LocationPoints;
import sgs.env.ecabsdriver.model.SearchBody;
import sgs.env.ecabsdriver.model.Sos;
import sgs.env.ecabsdriver.model.StationResponse;
import sgs.env.ecabsdriver.model.UserDeviceInfoModel;
import sgs.env.ecabsdriver.presenter.DriverMasterIdPresenter;
import sgs.env.ecabsdriver.presenter.LogoutPresenter;
import sgs.env.ecabsdriver.presenter.SosPresenter;
import sgs.env.ecabsdriver.presenter.UpdateDriverStatusPresenter;
import sgs.env.ecabsdriver.presenter.UserDeviceInfoPresenter;
import sgs.env.ecabsdriver.service.RetrieveFirestoreData;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.PermissionUtils;
import sgs.env.ecabsdriver.util.ProgressBarLayout;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class HomeActivity extends BaseActivity implements UiMethods, UImethodsI,
        UImethodsI.ApiFailed,
        UImethodsI.RetryMstId,
        IPassengerTripMaster {

    private static final int IMMEDIATE_APP_UPDATE_REQ_CODE = 124;

    private static final int REQUEST_PERMISSION_CODE = 200;

    private static final String TAG = "HomeActivity";

    LocalBroadcastManager localBroadcastManager;

    ProgressBar progressBar;

    private List<Home> homeList;

    private ProgressBarLayout layout;

    boolean isDriverLogout = false;

    private int count;

    private TextView tvDriverNme, tvVehicleNo, tvVersion;

    private ImageButton brkButton;

    private SharedPrefsHelper helper;

    private android.location.Location mLocation;

    private String name, actionName, driverImage = "";

    private PopupMenu popup;

    private ECabsDriverDbHelper dbHelper;

    private DriverStatus status;

    private ImageView ivDriverImage;

    AppUpdateManager appUpdateManager;

    public static HomeActivity instance;

    android.app.AlertDialog alertDialog;

    private Double latitude, longitude;

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivityResumed();
        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        checkUpdate();
        refresh();

    }


    @Override
    protected void onPause() {
        super.onPause();
        HomeActivityPaused();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_home);

        instance = HomeActivity.this;
        layout = new ProgressBarLayout();
        helper = SharedPrefsHelper.getInstance();
        dbHelper = new ECabsDriverDbHelper(HomeActivity.this);

        if (!SharedPrefsHelper.getInstance().get(AppConstants.FirstLogin,true)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getDeviceInfo();
            }
        }
        if (isNetworkAvailable(this)) {
            internetAvailable();
        } else {
            internetAvailable();
            Toast.makeText(HomeActivity.this, "No network connectivity!",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void internetAvailable() {

        progressBar = findViewById(R.id.homeProgressbar);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        RecyclerView recyclerView = findViewById(R.id.ActivityRecylerView);
        TextView buttonLogout = findViewById(R.id.ButtonLogout);
        brkButton = findViewById(R.id.breakBtn);
        tvDriverNme = findViewById(R.id.tvDriverName);
        tvVehicleNo = findViewById(R.id.textViewVehicleNum);
        tvVersion = findViewById(R.id.tvVersion);
        ivDriverImage = findViewById(R.id.ivNavProfile);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            tvVersion.setText("Version : " + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "internetAvailable: ex " + e.getMessage());
        }
        name = helper.get(AppConstants.DRIVER_NAME, "");
        String vehNo = helper.get(AppConstants.DRIVER_VEH_NO, "");
        driverImage = helper.get(AppConstants.PIC, "");
        if (name != null && vehNo != null) {
            tvDriverNme.setText(name);
            tvVehicleNo.setText(vehNo);
            if (!driverImage.equals("")) {
                Glide.with(HomeActivity.this).load(driverImage).into(ivDriverImage);
            }
        }
        fillHmeRecyleView();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        HomeGridAdapter homeGridAdapter = new HomeGridAdapter(this, homeList,
                SharedPrefsHelper.getInstance().get(AppConstants.NOTIFICATION_TRIP, false),
                new HomeGridAdapter.OnItemClickListener() {
                    @Override
                    public void onNextActivity(int postion, Home home, ImageView imageViewLogo) {
                        switch (postion) {
                            case 0:
                                startActivity(new Intent(HomeActivity.this,
                                        WaitingForCustomerActivity.class));
                                break;
                            case 1:
                                Intent intent =
                                        new Intent(HomeActivity.this, DriverTripsActivity.class);
                                startActivity(intent);
                                break;

                            case 2:
                                Intent intent1 =
                                        new Intent(HomeActivity.this, ProfileActivity.class);
                                startActivity(intent1);
                                break;
                            //  sos is feature
                            case 3:
                                if (mLocation != null) {
                                    if (ECabsApp.isNetworkAvailable(HomeActivity.this)) {
                                        sosAPI();
                                    } else {
                                        Toast.makeText(HomeActivity.this,
                                                "Network connectivity problem !",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(HomeActivity.this,
                                            "Please wait location capturing.....",
                                            Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case 4:
                                Intent intent2 = new Intent(HomeActivity.this, ChatSupportActivity.class);
                                startActivity(intent2);
                                break;
                        }
                    }
                });
        recyclerView.setAdapter(homeGridAdapter);
        popup = new PopupMenu(HomeActivity.this, brkButton);
        popup.getMenuInflater().inflate(R.menu.driver_hme_menu, popup.getMenu());
        // driver taking break
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                breakDialog();
            }
        });
        // logout for driver
        brkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.menu_logout) {
                            driverLogoutAPI();

                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void getDeviceInfo() {
        IUserDeviceInfo iUserDeviceInfo = new UserDeviceInfoPresenter();
        UserDeviceInfoModel userDeviceInfoModel = new UserDeviceInfoModel();
        userDeviceInfoModel.setAndroidVersion(Build.VERSION.RELEASE);
        userDeviceInfoModel.setBrand(Build.BRAND);
        userDeviceInfoModel.setManufacturer(Build.MANUFACTURER);
        userDeviceInfoModel.setProduct(Build.PRODUCT);
        BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
        int batteryPercentage =
                batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        userDeviceInfoModel.setBatteryPercentage(batteryPercentage + "%");
        userDeviceInfoModel.setAppVersion(BuildConfig.VERSION_NAME);
        userDeviceInfoModel.setAppVersionCode(String.valueOf(BuildConfig.VERSION_CODE));
        userDeviceInfoModel.setUserId(SharedPrefsHelper.getInstance().get(AppConstants.USER_ID));
        iUserDeviceInfo.saveUserDeviceInfo(userDeviceInfoModel, this);
    }

    private void driverLogoutAPI() {
        if (!isDriverLogout) {
            CloseDay closeDay = new CloseDay();
            Location location = new Location();
            String mstId = SharedPrefsHelper.getInstance().get(AppConstants.DRV_MASTER_ID);
            String driverId = helper.get(AppConstants.DRIVER_ID);
            if (mLocation != null && mstId != null) {

                location.setLatitude(String.valueOf(mLocation.getLatitude()));
                location.setLongitude(String.valueOf(mLocation.getLongitude()));
                closeDay.setDriverLocation(location);
                closeDay.setDrierId(driverId);
                closeDay.setDriverTripMasterId(mstId);
                isDriverLogout = true;
                LogoutI logoutI = new LogoutPresenter();
                logoutI.endDriverDayAPI(HomeActivity.this, closeDay);
            }
        }

    }

    private void fillHmeRecyleView() {
        homeList = new ArrayList<>();
        homeList.add(new Home("Start Trip", R.drawable.wheel));
        homeList.add(new Home("Trip History", R.drawable.orderhistory));
        homeList.add(new Home("Profile", R.drawable.customer));
        homeList.add(new Home("SOS", R.drawable.sos));
        homeList.add(new Home("Chat with support", R.mipmap.chat_icon));
    }

    private void sosAPI() {
        String driverName = helper.get(AppConstants.DRIVER_NAME);
        String driverId = helper.get(AppConstants.DRIVER_ID);
        String vehicleNo = helper.get(AppConstants.DRIVER_VEH_NO);
        String drvMstId = helper.get(AppConstants.DRV_MASTER_ID);
        String drvPhone = helper.get(AppConstants.MOBILE_NUM);
        LocationPoints locationPoints = new LocationPoints();
        List<Double> list = new ArrayList<>();
        list.add(0, mLocation.getLongitude());
        list.add(1, mLocation.getLatitude());
        locationPoints.setType(AppConstants.POINTS);
        locationPoints.setCoordinates(list);
        Sos sos = new Sos();
        sos.setDriverTripmasterId(drvMstId);
        sos.setId(driverId);
        sos.setLocation(locationPoints);
        sos.setName(driverName);
        sos.setVehicleId(vehicleNo);
        sos.setPhone(drvPhone);
        sos.setUserId("");  // when der is no customer associated to the driver......
        new SosPresenter().sosAPI(HomeActivity.this, sos);
    }

    private void breakDialog() {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.break_layout, null);
        alertBuilder.setView(dialogView);
        Button rbTeaBreak = dialogView.findViewById(R.id.rbtea);
        Button rbLunchBreak = dialogView.findViewById(R.id.rbLunch);
        Button rbExtraBreak = dialogView.findViewById(R.id.rbExtraBreak);
        Button btnGo = dialogView.findViewById(R.id.btnGo);
        Button btnStartCharge = dialogView.findViewById(R.id.btnStartCharge);
        Button btnStopCharge = dialogView.findViewById(R.id.btnStopCharge);
        int lunchBreakCount = dbHelper.getBreakTypeCount(AppConstants.LUNCH_BREAK);
        int teaBreakCount = dbHelper.getBreakTypeCount(AppConstants.TEA_BREAK);
        if (lunchBreakCount > 1) {
            rbLunchBreak.setBackgroundResource(R.color.date_coloo);
            rbLunchBreak.setText("No break available");
            rbLunchBreak.setEnabled(false);
        }
        if (teaBreakCount > 1) {
            rbTeaBreak.setVisibility(View.GONE);
            rbExtraBreak.setVisibility(View.VISIBLE);
        }
        final AlertDialog alert = alertBuilder.create();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setCancelable(true);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();

        if (SharedPrefsHelper.getInstance().get(AppConstants.ChargingStatus) != null &&
                !SharedPrefsHelper.getInstance().get(AppConstants.ChargingStatus).toString().isEmpty()) {
            if (SharedPrefsHelper.getInstance().get(AppConstants.ChargingStatus).equals(AppConstants.STATION_GO)) {
                btnGo.setEnabled(false);
                btnGo.setBackgroundResource(R.color.lightergray);
                btnStartCharge.setEnabled(true);
                btnStartCharge.setBackgroundResource(R.color.actioncolor);
                btnStopCharge.setEnabled(false);
                btnStopCharge.setBackgroundResource(R.color.lightergray);
            }
            if (SharedPrefsHelper.getInstance().get(AppConstants.ChargingStatus).equals(AppConstants.START_CHARGE)) {
                btnGo.setEnabled(false);
                btnGo.setBackgroundResource(R.color.lightergray);
                btnStartCharge.setEnabled(false);
                btnStartCharge.setBackgroundResource(R.color.lightergray);
                btnStopCharge.setEnabled(true);
                btnStopCharge.setBackgroundResource(R.color.actioncolor);
            }
        } else {
            btnGo.setEnabled(true);
            btnGo.setBackgroundResource(R.color.actioncolor);
            btnStartCharge.setEnabled(false);
            btnStartCharge.setBackgroundResource(R.color.lightergray);
            btnStopCharge.setEnabled(false);
            btnStopCharge.setBackgroundResource(R.color.lightergray);
        }

        rbTeaBreak.setOnClickListener(view -> {
            if (mLocation != null) {
                Intent intent = new Intent(HomeActivity.this, BreakConfirmation.class);
                intent.putExtra(AppConstants.BREAK_NAME, "tea");
                intent.putExtra(AppConstants.LATITUDE,
                        String.valueOf(mLocation.getLatitude()));
                intent.putExtra(AppConstants.LONGITUDE,
                        String.valueOf(mLocation.getLongitude()));
                startActivity(intent);
                alert.dismiss();
            } else {
                Toast.makeText(HomeActivity.this, "Please wait untill the lcoation is " +
                                "captured",
                        Toast.LENGTH_SHORT).show();
                alert.dismiss();
            }
        });

        rbLunchBreak.setOnClickListener(view -> {
            if (mLocation != null) {
                Intent intent = new Intent(HomeActivity.this, BreakConfirmation.class);
                intent.putExtra(AppConstants.BREAK_NAME, "lunch");
                intent.putExtra(AppConstants.LATITUDE,
                        String.valueOf(mLocation.getLatitude()));
                intent.putExtra(AppConstants.LONGITUDE,
                        String.valueOf(mLocation.getLongitude()));
                startActivity(intent);
                alert.dismiss();
            } else {
                Toast.makeText(HomeActivity.this, "Please wait untill the lcoation is " +
                                "captured",
                        Toast.LENGTH_SHORT).show();
                alert.dismiss();
            }
        });

        rbExtraBreak.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, BreakConfirmation.class);
            startActivity(intent);
            alert.cancel();
        });

        btnGo.setOnClickListener(v -> {
            setStatus(AppConstants.STATION_GO);
            alert.dismiss();
        });
        btnStartCharge.setOnClickListener(v -> {
            setStatus(AppConstants.START_CHARGE);
            alert.dismiss();
        });

        btnStopCharge.setOnClickListener(v -> {
            setStatus(AppConstants.STOP_CHARGE);
            alert.dismiss();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        endProgressBar();
        finish();
    }

    @Override
    protected void refresh() {

        if (SharedPrefsHelper.getInstance().get(AppConstants.ChargingStatus) != null &&
                !SharedPrefsHelper.getInstance().get(AppConstants.ChargingStatus).toString().isEmpty()) {
            if (SharedPrefsHelper.getInstance().get(AppConstants.ChargingStatus).equals(AppConstants.STATION_GO)
                    || SharedPrefsHelper.getInstance().get(AppConstants.ChargingStatus).equals(AppConstants.START_CHARGE)) {
                Intent intent = new Intent(this, OptionsActivity.class);
                startActivity(intent);

            }
        }
        if (SharedPrefsHelper.getInstance().get(AppConstants.IN_BREAK, false) != null) {
            Boolean inBreak = SharedPrefsHelper.getInstance().get(AppConstants.IN_BREAK, false);
            if (inBreak) {
                Intent intent = new Intent(HomeActivity.this, BreakActivity.class);
                startActivity(intent);
                finish();
            }
        }
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
    protected void retrivedLocation(android.location.Location location) {
        mLocation = location;
        // generating the driverMasterId for the day to the driver,
        SharedPrefsHelper.getInstance().save(AppConstants.LATITUDE, String.valueOf(mLocation.getLatitude()));
        SharedPrefsHelper.getInstance().save(AppConstants.LONGITUDE, String.valueOf(mLocation.getLongitude()));

        genDrvMstId(location);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                if (PermissionUtils.verifyPermissions(grantResults)) {
                    Log.d(TAG, "onRequestPermissionsResult: true");
                } else {
                    if (count < 2) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission necessary");
                        alertBuilder.setMessage("Location permission is necessary");
                        alertBuilder.setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(HomeActivity.this,
                                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                                REQUEST_PERMISSION_CODE);
                                    }
                                });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();
                        count++;
                    } else {
                        finish();
                    }
                }
                break;
        }
    }

    private void genDrvMstId(android.location.Location location) {
        boolean id = helper.get(AppConstants.GEN_MASTER_ID, true);
        DrvMasterIDI idi = new DriverMasterIdPresenter();
        if (id) {
            InfoModelB infoModelB = new InfoModelB();
            infoModelB.setDriverId(helper.get(AppConstants.DRIVER_ID, ""));
            infoModelB.setDriverName(name);
            if (helper.get(AppConstants.DRV_VEH_ID) != null) {
                infoModelB.setVehicleId(helper.get(AppConstants.DRV_VEH_ID));
            }
            if (helper.get(AppConstants.DRIVER_VEH_NO) != null) {
                infoModelB.setVehicleNumber(helper.get(AppConstants.DRIVER_VEH_NO));
            }
            // for driver vehicle is hardcoded
            if (helper.get(AppConstants.MOBILE_NUM) != null) {
                infoModelB.setDriverPhoneNumber(helper.get(AppConstants.MOBILE_NUM, ""));
            }
            if (helper.get(AppConstants.DRIVER_NAME) != null) {
                infoModelB.setDriverName(helper.get(AppConstants.DRIVER_NAME, ""));
            }
            infoModelB.setDriverRating(5);
            if (!String.valueOf(location.getLatitude()).isEmpty() && !String.valueOf(
                    location.getLatitude()).isEmpty() || !String.valueOf(
                    location.getLatitude()).equals("0.0") && !String.valueOf(
                    location.getLatitude()).equals("0.0")
                    || !String.valueOf(
                    location.getLatitude()).equals("0") && !String.valueOf(
                    location.getLatitude()).equals("0")) {
                Location location1 = new Location();
                location1.setLatitude(String.valueOf(location.getLatitude()));
                location1.setLongitude(String.valueOf(location.getLongitude()));
                infoModelB.setLocation(location1);
            }
            infoModelB.setDriverPhoto(helper.get(AppConstants.PIC,
                    "https://www.malbork.in/static/media/Mask%20Group%2015.4bb4df62.png"));
            idi.startRideAPI(HomeActivity.this, infoModelB);
        }
    }

    @Override
    public void progresStart() {
        layout.displayDialog(HomeActivity.this);
    }

    @Override
    public void progressEnd() {
        if (layout != null) {
            layout.hideProgressDialog();
        }
    }

    @Override
    public void startProgessBar() {
        layout.displayDialog(HomeActivity.this);
    }

    @Override
    public void endProgressBar() {
        if (layout != null) {
            layout.hideProgressDialog();
        }

    }

    @Override
    public void apiFailed() {
        Toast.makeText(this,
                "Unable to create driverTrip masterId please wait for some time retrying again!",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void retry() {
        retryTrip();
    }

    private void retryTrip() {
        setContentView(setLayoutIfneeded());
        ImageView imageView = findViewById(R.id.no_interent_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable(HomeActivity.this) && mLocation != null) {
                    genDrvMstId(mLocation);
                } else {
                    Toast.makeText(HomeActivity.this, "No network connectivity!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void getPassegerTripMasterDetails() {
        custmerDialog();
    }

    private void custmerDialog() {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.accept_customer, null);
        final UpdateDriverStatusI statusI = new UpdateDriverStatusPresenter();
        status = new DriverStatus();
        String passTripMstId = SharedPrefsHelper.getInstance().get(AppConstants.PASS_MST_ID, "");
        String drvMstId = SharedPrefsHelper.getInstance().get(AppConstants.DRV_MASTER_ID, "");
        status.setPassengerTripMasterId(passTripMstId);
        status.setDriverTripMasterId(drvMstId);
        Location1 location1 = new Location1();
        location1.setLatitude(String.valueOf(mLocation.getLatitude()));
        location1.setLongitide(String.valueOf(mLocation.getLongitude()));
        status.setStatus("allotted");
        status.setDriverLocation(location1);
        alertBuilder.setView(dialogView);
        TextView tv = dialogView.findViewById(R.id.textViewCustomerNmae);
        Button imageButton = dialogView.findViewById(R.id.floatingActionButtonAccept);
        final AlertDialog alert = alertBuilder.create();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setCancelable(true);
        alert.show();
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                moveToMapActivity();
            }
        });
    }

    private void moveToMapActivity() {
        helper.save(AppConstants.ACCEPT, true);
        Intent intent = new Intent(this, MapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void checkUpdate() {

        com.google.android.play.core.tasks.Task<AppUpdateInfo>
                appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMMEDIATE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(HomeActivity.this, "please update and enjoy the app",
                        Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_OK) {
                Toast.makeText(HomeActivity.this, "App update success",
                        Toast.LENGTH_SHORT).show();
                driverLogoutAPI();
            } else {
                checkUpdate();
            }
            //In app Update link
            //https://www.section.io/engineering-education/android-application-in-app-update-using
            // -android-studio/
        }
    }

    public void showAlertResponse(String message) {
        if (alertDialog == null) {
            LayoutInflater inflater =
                    (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.transaction_status_popup, null);
            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.MyDialogTheme);

            builder.setView(layout);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            if (!isFinishing()) {
                alertDialog.show();
            }

            TextView OK_txt = layout.findViewById(R.id.OK_txt);
            TextView title_txt = layout.findViewById(R.id.title_txt);

            title_txt.setText(message);

            OK_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();

                }
            });
        }
    }

    private void setStatus(String chargeStatus) {
        layout.displayDialog(this);
        String token = ECabsApp.getInstance().getAcssToken();
        if (!token.equals(AppConstants.NO_TOKEN)) {
            final RegisterService service = ECabsApp.getRetrofit().create(RegisterService.class);
            ChargingStationModel chargingStationModel = new ChargingStationModel();
            chargingStationModel.setCharging(chargeStatus);
            chargingStationModel.setDriverId(SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, ""));
            Call<GeneralResponse> call = service.UpdateChargingStatus(token, chargingStationModel);
            Log.d(TAG, "chargingStationAPI: url " + call.request().url());
            Log.d(TAG, "chargingStationAPI: payload " + chargingStationModel);
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(@NonNull Call<GeneralResponse> call,
                                       @NonNull Response<GeneralResponse> response) {

                    if (response.isSuccessful()) {
                        if (chargeStatus.equals(AppConstants.STATION_GO)) {
                            SharedPrefsHelper.getInstance().save(AppConstants.ChargingStatus, AppConstants.STATION_GO);
                            chargingAPI();
                        }

                        if (chargeStatus.equals(AppConstants.START_CHARGE)) {
                            SharedPrefsHelper.getInstance().save(AppConstants.ChargingStatus, AppConstants.START_CHARGE);
                            layout.hideProgressDialog();
                        }
                        if (chargeStatus.equals(AppConstants.STOP_CHARGE)) {
                            SharedPrefsHelper.getInstance().save(AppConstants.ChargingStatus, "");
                            layout.hideProgressDialog();
                        }
                    } else {
                        layout.hideProgressDialog();
                        if (response.code() == 400) {

                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response.errorBody().string());
                                String userMessage = jsonObject.getString("message");
                                Toast.makeText(HomeActivity.this, userMessage, Toast.LENGTH_SHORT).show();
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }

                @Override
                public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                    layout.hideProgressDialog();
                }
            });

        }
    }

    public void chargingAPI() {
        String token = ECabsApp.getInstance().getAcssToken();
        if (!token.equals(AppConstants.NO_TOKEN) && mLocation != null) {
            final RegisterService service = ECabsApp.getRetrofit().create(RegisterService.class);
            sgs.env.ecabsdriver.model.Location location = new sgs.env.ecabsdriver.model.Location();
            location.setLatitude(String.valueOf(mLocation.getLatitude()));
            location.setLongitude(String.valueOf(mLocation.getLongitude()));
            final SearchBody searchBody = new SearchBody();
            searchBody.setLocation(location);
            Call<StationResponse> call = service.searchStation(token, searchBody);
            Log.d(TAG, "chargingAPI: url " + call.request().url());
            Log.d(TAG, "chargingAPI: payload " + searchBody);
            call.enqueue(new Callback<StationResponse>() {
                @Override
                public void onResponse(@NonNull Call<StationResponse> call,
                                       @NonNull Response<StationResponse> response) {
                    layout.hideProgressDialog();
                    if (response.isSuccessful()) {
                        List<ChargeStation> chargeStation = response.body().getStation();
                        Log.d(TAG, "onResponse: is " + response.body());
                        if (chargeStation.size() > 0) {
                            for (int i = 0; i < chargeStation.size(); i++) {
                                if (chargeStation.get(i).getMalborkOwned() != null && chargeStation.get(i).getMalborkOwned() == true) {
                                    Gson gson = new Gson();
                                    String chargingStationJson = gson.toJson(chargeStation.get(i));
                                    SharedPrefsHelper.getInstance().save(
                                            AppConstants.CHARGING_STATION_JSON,
                                            chargingStationJson);
                                    SharedPrefsHelper.getInstance().save(AppConstants.CONNECTOR_ID,
                                            chargeStation.get(i).getConnectorId());

                                    actionName = AppConstants.START_CHARGING_ACTIVITY;
                                } else {
                                    actionName = AppConstants.OPTIONS_ACTIVITY;
                                }
                            }
                            LocationPoints points = chargeStation.get(0).getLocation();
                            latitude = points.getCoordinates().get(1);
                            longitude = points.getCoordinates().get(0);
                            nav();
                        } else {
                            Toast.makeText(getApplicationContext(), "No near by charging station found please try again!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        try {
                            Log.d(TAG, "onResponse: failure " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onFailure(@NonNull Call<StationResponse> call, @NonNull Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                    layout.hideProgressDialog();
                }
            });
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    chargingAPI();
                }
            }, 20000);
        }
    }

    public void nav() {
        if (mLastLocation != null) {
            String newUri =
                    "https://www.google.com/maps/dir/?api=1&origin=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "&destination=" + latitude + "," + longitude;
            Log.d(TAG, "Uri: " + newUri);
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
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
            Toast.makeText(this, "Please try again location not captured",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void callGoogleMap(String newUri) {
        Uri googleIntentURI = Uri.parse(newUri);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, googleIntentURI);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);

    }
}

