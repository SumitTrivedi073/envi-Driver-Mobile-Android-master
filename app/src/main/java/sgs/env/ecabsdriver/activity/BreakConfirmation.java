package sgs.env.ecabsdriver.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.animations.AntimationHelper;
import sgs.env.ecabsdriver.animations.EnumHolder;
import sgs.env.ecabsdriver.data.local.ECabsDriverDbHelper;
import sgs.env.ecabsdriver.interfce.RegisterService;
import sgs.env.ecabsdriver.model.BreakObj;
import sgs.env.ecabsdriver.model.BreakResoponse;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class BreakConfirmation extends BaseActivity {

    private ProgressBar progressBarWaiting;
    private Button buttonBack;
    private static final String TAG = "CustomerWaiting";
    private String breakName;
    private boolean isActivity;
    private String latitude;
    private String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_break_confirmation);

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Waiting.......");
        }

        Intent intent = getIntent();
        breakName = intent.getStringExtra(AppConstants.BREAK_NAME);
        latitude = intent.getStringExtra(AppConstants.LATITUDE);
        longitude = intent.getStringExtra(AppConstants.LONGITUDE);

        progressBarWaiting = findViewById(R.id.progressbarWaiting);
        ObjectAnimator animation1 = ObjectAnimator.ofInt(progressBarWaiting, "progress", 0, 1);

        animation1.setDuration(4000);
        animation1.setInterpolator(new DecelerateInterpolator());
        animation1.start();

        AntimationHelper.with(EnumHolder.Pulse).duration(1500).
                interpolate(new AccelerateDecelerateInterpolator())
                .repeat(1500).
                setView(findViewById(R.id.textViewCustomerWaiting));

        if (breakName != null) {
            reqBreakAPI(breakName);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivity = false;
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

    }

    private void reqBreakAPI(final String breakName) {

        final ECabsDriverDbHelper dbHelper = new ECabsDriverDbHelper(BreakConfirmation.this);
        RegisterService getCustomerDetails = ECabsApp.getRetrofit().create(RegisterService.class);

        String token = SharedPrefsHelper.getInstance().get(AppConstants.KEY_JWT_TOKEN, "");
        Log.d(TAG, "reqBreakAPI: token " + token);

        if (!token.equals("")) {
            BreakObj breakObj = new BreakObj();
            breakObj.setBreakType(breakName);
            sgs.env.ecabsdriver.model.Location location = new sgs.env.ecabsdriver.model.Location();
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            breakObj.setDriverLocation(location);
            String drvMstId = SharedPrefsHelper.getInstance().get(AppConstants.DRV_MASTER_ID, "");
            breakObj.setDriverTripMasterId(drvMstId);
            Call<BreakResoponse> takeBreak = getCustomerDetails.reqBreak(token, breakObj);
            Log.d(TAG, "reqBreakAPI: input s" + breakObj);
            Log.d(TAG, "takeBreakAPI: url " + takeBreak.request().url());

            takeBreak.enqueue(new Callback<BreakResoponse>() {
                @Override
                public void onResponse(Call<BreakResoponse> call, Response<BreakResoponse> response) {
                    BreakResoponse breakResoponse = response.body();
                    Log.d(TAG, "onResponse: code " + response.code());

                    if (response.isSuccessful() && breakResoponse != null) {
                        String mess = breakResoponse.getMessage();
                        int totalCnt = dbHelper.getBreakTotalCnt();
                        int breakCount = dbHelper.getBreakTypeCount(breakName);
                        dbHelper.updateBreak(breakName, breakCount++, totalCnt++);
                        Toast.makeText(BreakConfirmation.this, "" + mess, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(BreakConfirmation.this, BreakActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra(AppConstants.BREAK_NAME, breakName);
                        SharedPrefsHelper.getInstance().save(AppConstants.BREAK_NAME, breakName);
                        SharedPrefsHelper.getInstance().save(AppConstants.LATITUDE, latitude);
                        SharedPrefsHelper.getInstance().save(AppConstants.LONGITUDE, longitude);
                        if (breakResoponse.getMessage().contentEquals("you are in break ,please end the break with this id")) {
                            intent.putExtra(AppConstants.IN_BREAK, true);
                            dbHelper.updateBreak(breakName, 0, 0);
                        }
                        startActivity(intent);
                    } else {
                        try {

                            String errorResponse = response.errorBody().string();
                            JSONObject errObject = new JSONObject(errorResponse);
                            String message = "Error";
                            if (errObject.has("message")) {
                                message = String.valueOf(errObject.get("message"));
                            }

                            if (response.code() == 400) {
                                Toast.makeText(BreakConfirmation.this, "You have exceeded ur break limits ", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(BreakConfirmation.this, "Can't take a break , " + message, Toast.LENGTH_LONG).show();
                                finish();
                            }
                            Toast.makeText(BreakConfirmation.this, "Can't take a break , " + message, Toast.LENGTH_LONG).show();
                            Log.d(TAG, "onResponse: " + response.errorBody().string());
                        } catch (IOException e) {
                            Log.d(TAG, "onResponse: exc " + e.getMessage());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<BreakResoponse> call, Throwable t) {
                    Toast.makeText(BreakConfirmation.this, "Can't take a break", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "failure: " + t.getMessage());
                    finish();
                }
            });
        }
    }

}







