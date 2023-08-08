package sgs.env.ecabsdriver.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.animations.AntimationHelper;
import sgs.env.ecabsdriver.animations.EnumHolder;
import sgs.env.ecabsdriver.interfce.RegisterService;
import sgs.env.ecabsdriver.interfce.UImethodsI;
import sgs.env.ecabsdriver.model.BreakObj;
import sgs.env.ecabsdriver.model.GeneralResponse;
import sgs.env.ecabsdriver.model.Location;
import sgs.env.ecabsdriver.service.LocationService;
import sgs.env.ecabsdriver.service.BreakTimerService;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.Internet;
import sgs.env.ecabsdriver.util.ProgressBarLayout;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class BreakActivity extends BaseActivity implements UImethodsI {

    private static final String TAG = "BreakActivity";

    Button buttonRelogin;

    Intent intent;

    private TextView textViewCountDown;

    private MediaPlayer mediaPlayer;

    private boolean boundVar = false;

    private boolean backPress;

    private Button btnStop;

    private String breakNme;

    private long currentTime;

    private TextView tvBreakType;

    private LocalBroadcastManager localBroadcastManager;

    private ProgressBarLayout progressBarLayout;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUIComponents(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break);
        progressBarLayout = new ProgressBarLayout();
        stopService(new Intent(BreakActivity.this, LocationService.class));
        tvBreakType = findViewById(R.id.tvBreakType);
        textViewCountDown = findViewById(R.id.tvtimercountdown);
        buttonRelogin = findViewById(R.id.buttonRelogin);
        btnStop = findViewById(R.id.breakStopBtn);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intent = getIntent();
        if (intent != null) {
            breakNme = SharedPrefsHelper.getInstance().get(AppConstants.BREAK_NAME, "");
            ;
            if (breakNme != null && breakNme.contentEquals("tea")) {
                tvBreakType.setText("Break");
                SharedPrefsHelper.getInstance().save(AppConstants.IN_BREAK, true);
                if (!Internet.isMyServiceRunning(BreakTimerService.class, BreakActivity.this)) {
                    Intent teaIntent = new Intent(this, BreakTimerService.class);
                    teaIntent.setAction(AppConstants.START_FORGROUND_SERVICE);
                    teaIntent.putExtra(AppConstants.BREAK_NAME, breakNme);
                    startService(teaIntent);
                }
            } else if (breakNme != null && breakNme.contentEquals("lunch")) {
                tvBreakType.setText("Break");
                SharedPrefsHelper.getInstance().save(AppConstants.IN_BREAK, true);
                if (!Internet.isMyServiceRunning(BreakTimerService.class, BreakActivity.this)) {

                    Intent lunchIntent = new Intent(this, BreakTimerService.class);
                    lunchIntent.setAction(AppConstants.START_FORGROUND_SERVICE);
                    lunchIntent.putExtra(AppConstants.BREAK_NAME, breakNme);
                    startService(lunchIntent);
                }
            }
        }
        AntimationHelper.with(EnumHolder.Pulse).duration(1500).
                interpolate(new AccelerateDecelerateInterpolator()).repeat(3).
                setView(findViewById(R.id.imaeAnim));
        changeStatusBarColor();
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent stopIntent = new Intent(BreakActivity.this, BreakTimerService.class);
                stopIntent.setAction(AppConstants.STOP_FORGROUND_SERVICE);
                startService(stopIntent);
                endBreakAPI();
            }
        });
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.bg_screen2));
        }
    }

    private void endBreakAPI() {
        RegisterService getCustomerDetails = ECabsApp.getRetrofit().create(RegisterService.class);
        //    String breakId = SharedPrefsHelper.getInstance().get(AppConstants.BREAK_ID,"");
        String token = SharedPrefsHelper.getInstance().get(AppConstants.KEY_JWT_TOKEN, "");
        String breakName = SharedPrefsHelper.getInstance().get(AppConstants.BREAK_NAME, "");
        if (token != null && breakName != null) {
            Log.d(TAG, "endBreakAPI: token " + token);
            BreakObj breakObj = new BreakObj();
            String drvMstId = SharedPrefsHelper.getInstance().get(AppConstants.DRV_MASTER_ID, "");
            breakObj.setDriverTripMasterId(drvMstId);
            Location lc = new Location();
            lc.setLatitude(SharedPrefsHelper.getInstance().get(AppConstants.LATITUDE, ""));
            lc.setLongitude(SharedPrefsHelper.getInstance().get(AppConstants.LONGITUDE, ""));
            breakObj.setDriverLocation(lc);
            breakObj.setBreakType(breakName);
            retrofit2.Call<GeneralResponse> takeBreak =
                    getCustomerDetails.endBreak(token, breakObj);
            Log.d(TAG, "takeBreakAPI: url " + takeBreak.request().url());
            Log.d(TAG, "endBreakAPI: object " + breakObj);
            takeBreak.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call,
                                       Response<GeneralResponse> response) {
                    //                GeneralResponse resoponse = response.body();
                    if (response.isSuccessful()) {
                        moveToHomeActivity();
                    } else {
                        Toast.makeText(BreakActivity.this, "Please try again",
                                Toast.LENGTH_SHORT).show();
                        try {
                            Log.d(TAG, "onResponse: failure " + response.errorBody().string());
                        } catch (IOException e) {
                            Log.d(TAG, "onResponse: ex " + e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    Toast.makeText(BreakActivity.this, "Unable to end Break",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void moveToHomeActivity() {
        Intent home = new Intent(BreakActivity.this, HomeActivity.class);
        Log.d(TAG, "breakeEnded API succes ");
        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(home);
        // update to sharedPreferencs
        SharedPrefsHelper.getInstance().save(AppConstants.IN_BREAK, false);
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (backPress) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Ur on break can't go back ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(broadcastReceiver, new IntentFilter(BreakTimerService.COUNTDOWN_BR));
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
	protected void retrivedLocation(android.location.Location location) {

	}

	@Override
    public void startProgessBar() {
        progressBarLayout.displayDialog(BreakActivity.this);
    }

    @Override
    public void endProgressBar() {
        if (progressBarLayout != null)
            progressBarLayout.hideProgressDialog();
    }

    public void updateUIComponents(Intent intent) {
        if (intent.getExtras() != null) {
            String remainingTime = intent.getStringExtra(AppConstants.TIME_BELOW_10_SEC);
            if (remainingTime != null) {
                if (textViewCountDown != null) {
                    textViewCountDown.setVisibility(View.VISIBLE);
                    textViewCountDown.setText(remainingTime);
                }
            }
            int timeUp = intent.getIntExtra(AppConstants.TIMEUP, 1);
            if (timeUp != 1 && timeUp == 0) {
                Toast.makeText(this, "Time expired", Toast.LENGTH_LONG).show();
            }
        }
    }

}
