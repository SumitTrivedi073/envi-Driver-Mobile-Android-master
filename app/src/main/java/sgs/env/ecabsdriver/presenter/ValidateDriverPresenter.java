package sgs.env.ecabsdriver.presenter;

import static sgs.env.ecabsdriver.util.DriverAlgorithm.logoutfromApp;
import static sgs.env.ecabsdriver.util.ECabsApp.isHomeActivityVisible;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sgs.env.ecabsdriver.activity.CollectCashActivity;
import sgs.env.ecabsdriver.activity.HomeActivity;
import sgs.env.ecabsdriver.activity.LoginActivity;
import sgs.env.ecabsdriver.activity.MapActivity;
import sgs.env.ecabsdriver.activity.TripMapActivity;
import sgs.env.ecabsdriver.activity.WaitingForCustomerActivity;
import sgs.env.ecabsdriver.data.local.ECabsDriverDbHelper;
import sgs.env.ecabsdriver.interfce.RegisterService;
import sgs.env.ecabsdriver.interfce.ValidateDriverI;
import sgs.env.ecabsdriver.model.CheckDriverTripStatus;
import sgs.env.ecabsdriver.model.CustomerLocation;
import sgs.env.ecabsdriver.model.CustomerNotfication;
import sgs.env.ecabsdriver.model.DriverDetails;
import sgs.env.ecabsdriver.model.DriverTripStatus;
import sgs.env.ecabsdriver.model.TripDetails;
import sgs.env.ecabsdriver.model.UserDetails;
import sgs.env.ecabsdriver.model.ValidateDriverResp;
import sgs.env.ecabsdriver.service.LocationService;
import sgs.env.ecabsdriver.service.RetrieveFirestoreData;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class ValidateDriverPresenter implements ValidateDriverI {

    private static final String TAG = "ValidateDriverStatus";
    DriverTripStatus driverTripStatus;
    DriverDetails driverDetails;
    String tripStatus, breakStatus;
    HomeActivity homeActivity;

    @Override
    public void getDriverTripStatus(Context context, CheckDriverTripStatus checkTripStatus) {
        RegisterService service = ECabsApp.getRetrofit().create(RegisterService.class);
        SharedPrefsHelper helper = SharedPrefsHelper.getInstance();
        String token = SharedPrefsHelper.getInstance().get(AppConstants.KEY_JWT_TOKEN, "");
        if (!token.equals("")) {
            Log.d(TAG, "token: " + token);
            retrofit2.Call<ValidateDriverResp> call = service.getDriverTripStatus(token);
            Log.d(TAG, "DriverTripStatusAPI: url " + call.request().url());

            call.enqueue(new Callback<ValidateDriverResp>() {

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(@NonNull retrofit2.Call<ValidateDriverResp> call, @NonNull
                        Response<ValidateDriverResp> response) {
                    Log.d(TAG, "onSuccess: " + response.body());
                    if (response.isSuccessful()&& response.code()==200) {
                        ValidateDriverResp resp = response.body();
                        UserDetails userDetails = null;
                        if (resp != null) {
                            userDetails = resp.getUserDetails();
                            driverTripStatus = resp.getTripDetails();
                            driverDetails = resp.getDriverDetails();
                            tripStatus = resp.getTripStatus();
                            breakStatus = resp.getDasStatus();

                            if (driverDetails != null) {
                                Log.d(TAG, "DriverTripStatusAPI: onResponse = " + response.body());
                                helper.save(AppConstants.DRIVER_ID, driverDetails.getId());
                                helper.save(AppConstants.DRIVER_NAME, driverDetails.getName());
                                helper.save(AppConstants.PIC, driverDetails.getPropic());
                                helper.save(AppConstants.MOBILE_NUM, driverDetails.getPhone());
                            }
                            if (driverTripStatus != null) {
                                Log.d(TAG, "onResponse: driverDetails = " + driverTripStatus);
                                if (driverTripStatus.getDriverTripMasterId() != null && !driverTripStatus.getDriverTripMasterId().isEmpty()) {
                                    helper.save(AppConstants.DRV_MASTER_ID, driverTripStatus.getDriverTripMasterId());
                                }

                                if (driverTripStatus.getScheduledAt() != null && !driverTripStatus.getScheduledAt().isEmpty()) {
                                    helper.save(AppConstants.TripScheduledAt, driverTripStatus.getScheduledAt());
                                }
                                helper.save(AppConstants.IsScheduleTrip, driverTripStatus.isScheduledTrip());
                                if (driverTripStatus.getArrivalAtSource() != null &&
                                        driverTripStatus.getArrivalAtSource().getTime() != null
                                        && !driverTripStatus.getArrivalAtSource().getTime().isEmpty()) {

                                    helper.save(AppConstants.SourceArrivalTime, driverTripStatus.getArrivalAtSource().getTime());
                                }

                                if (driverTripStatus.getSpecialRemraks() != null
                                        && !driverTripStatus.getSpecialRemraks().isEmpty()) {

                                    helper.save(AppConstants.SpecialRemraks, driverTripStatus.getSpecialRemraks());
                                }

                            }
                            Intent intent;
                            Log.d(TAG, "onResponse: tripstatus = " + tripStatus);
                            Log.d(TAG, "onResponse: breakStatus = " + breakStatus);
                            helper.save(AppConstants.TripStatus,tripStatus);

                            switch (tripStatus) {
                                case AppConstants.NOT_LOGGED_IN:
                                    SharedPrefsHelper.getInstance().save(AppConstants.ACCEPT, false);
                                    logoutfromApp(context);
                                    break;

                                case AppConstants.TRIP_COMPLETED:
                                case AppConstants.NO_PREV_TRIPS_AVAILABLE:
                                case AppConstants.PAYMENT_COMPLETED:
                                case AppConstants.CANCELLED:
                                case AppConstants.DRIVER_CANCELLED:
                                case AppConstants.TRIP_STATUS_ONLINE_PENDING:
                                    revertHomeScreen(context);
                                    break;

                                case AppConstants.TRIP_REQUEST:
                                    intent = new Intent(context, WaitingForCustomerActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(intent);
                                    break;

                                case AppConstants.TRIP_ALLOTTED:
                                    SharedPrefsHelper.getInstance().get(AppConstants.ACCEPT, true);
                                    if (driverTripStatus != null) {
                                        helper.save(AppConstants.DRV_MASTER_ID, driverTripStatus.getDriverTripMasterId());
                                        helper.save(AppConstants.PASS_MST_ID, driverTripStatus.getPassengerTripMasterId());
                                        helper.save(AppConstants.PAYMENT_MODE, driverTripStatus.getPaymentMode());
                                        helper.save(AppConstants.FROM_ADDRESS, driverTripStatus.getFromAddress());
                                        helper.save(AppConstants.TO_ADDRESS, driverTripStatus.getToAddress());
                                        helper.save(AppConstants.USER_ID, driverTripStatus.getPassengerId());

                                            intent = new Intent(context, MapActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            context.startActivity(intent);
                                            Toast.makeText(context, "acknowleged", Toast.LENGTH_LONG).show();

                                    }
                                    break;

                                case AppConstants.TRIP_ONBOARDING:
                                    SharedPrefsHelper.getInstance().get(AppConstants.ACCEPT, true);
                                    helper.save(AppConstants.DRV_MASTER_ID, driverTripStatus.getDriverTripMasterId());
                                    helper.save(AppConstants.PASS_MST_ID, driverTripStatus.getPassengerTripMasterId());
                                    helper.save(AppConstants.PAYMENT_MODE, driverTripStatus.getPaymentMode());
                                    helper.save(AppConstants.FROM_ADDRESS, driverTripStatus.getFromAddress());
                                    helper.save(AppConstants.TO_ADDRESS, driverTripStatus.getToAddress());
                                    helper.save(AppConstants.USER_ID, driverTripStatus.getPassengerId());

                                    intent = new Intent(context, TripMapActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(intent);
                                    break;

                                case AppConstants.ARRIVED:
                                    SharedPrefsHelper.getInstance().get(AppConstants.ACCEPT, true);
                                    helper.save(AppConstants.DRV_MASTER_ID, driverTripStatus.getDriverTripMasterId());
                                    helper.save(AppConstants.PASS_MST_ID, driverTripStatus.getPassengerTripMasterId());
                                    helper.save(AppConstants.PAYMENT_MODE, driverTripStatus.getPaymentMode());
                                    helper.save(AppConstants.FROM_ADDRESS, driverTripStatus.getFromAddress());
                                    helper.save(AppConstants.TO_ADDRESS, driverTripStatus.getToAddress());
                                    helper.save(AppConstants.USER_ID, driverTripStatus.getPassengerId());
                                    helper.save(AppConstants.ARRIVED, AppConstants.ARRIVED);

                                    intent = new Intent(context, TripMapActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(intent);
                                    break;

                                case AppConstants.NOT_PAID:
                                case AppConstants.TRIP_STATUS_TOKEN_GENERATED:
                                case AppConstants.TRIP_STATUS_ONLINE_FAILURE:
                                case AppConstants.PAYMENT_INITIATED:
                                    TripDetails details = driverTripStatus.getArrivalAtDestination();
                                    helper.save(AppConstants.PAYMENT_MODE, driverTripStatus.getPaymentMode());
                                    helper.save(AppConstants.DRIVER_ID, driverTripStatus.getDriverId());
                                    helper.save(AppConstants.PASS_MST_ID, driverTripStatus.getPassengerTripMasterId());
                                    helper.save(AppConstants.USER_ID, driverTripStatus.getPassengerId());
                                    helper.save(AppConstants.TO_ADDRESS, driverTripStatus.getToAddress());
                                    helper.save(AppConstants.FROM_ADDRESS, driverTripStatus.getFromAddress());
                                    SharedPrefsHelper.getInstance().get(AppConstants.ACCEPT, true);
                                    helper.save(AppConstants.DRV_MASTER_ID, driverTripStatus.getDriverTripMasterId());
                                    Log.d(TAG, "onResponse: " + details);
                                    helper.save(AppConstants.CGST, details.getCgst());
                                    helper.save(AppConstants.SGST, details.getSgst());
                                    helper.save(AppConstants.PER_KM, details.getKmFare() + "");
                                    helper.save(AppConstants.PER_KM_PRICE,
                                            details.getPerKMPrice());
                                    helper.save(AppConstants.DISCOUNT, details.getDiscount());
                                    helper.save(AppConstants.DISTANCE, details.getDistanceTravelled());
                                    helper.save(AppConstants.AmountTobeCollected, details.getAmountTobeCollected());
                                    helper.save(AppConstants.ADVANCEPAID, details.getAdvancePaid());
                                    helper.save(AppConstants.TAXABLE_AMOUNT, "0");
                                    helper.save(AppConstants.DRIVER_PHOTO, driverDetails.getPropic());
                                    helper.save(AppConstants.DRIVER_FIRST_NAME, driverDetails.getName());
                                    helper.save(AppConstants.TRIP_START_TIME, driverTripStatus.getTripStartTime());
                                    helper.save(AppConstants.TRIP_END_TIME, driverTripStatus.getTripEndTime());
                                    helper.save(AppConstants.DIFFERENCE_IN_TRIP_TIME, driverTripStatus.getDifferenceInTripTime());
                                    if (details.getTollAmount() != null && !details.getTollAmount().isEmpty()) {
                                        helper.save(AppConstants.TOLL_CHARGES, details.getTollAmount());
                                    } else {
                                        helper.save(AppConstants.TOLL_CHARGES, "");
                                    }
                                    helper.save(AppConstants.CUSTOMER_MAIL_ID, userDetails.getMailid());
                                    Log.d(TAG, "onResponse: inside payment not paid after saving data to local db");
                                    intent = new Intent(context, CollectCashActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(intent);
                                    break;

                                default:
                                    revertHomeScreen(context);
                                    break;

                            }

                            switch (breakStatus) {
                                case AppConstants.STOP_CHARGE:
                                    helper.save(AppConstants.ChargingStatus, "");
                                    SharedPrefsHelper.getInstance().save(AppConstants.IN_BREAK, false);
                                    SharedPrefsHelper.getInstance().save(AppConstants.ACCEPT, false);
                                    intent = new Intent(context, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(intent);
                                    break;
                                case AppConstants.STATION_GO:
                                    helper.save(AppConstants.ChargingStatus, AppConstants.STATION_GO);
                                    break;
                                case AppConstants.START_CHARGE:
                                    helper.save(AppConstants.ChargingStatus, AppConstants.START_CHARGE);
                                    break;
                                case "lunch":
                                    helper.save(AppConstants.BREAK_NAME, "lunch");
                                    helper.save(AppConstants.IN_BREAK, true);
                                    break;

                                case "tea":
                                    helper.save(AppConstants.BREAK_NAME, "tea");
                                    helper.save(AppConstants.IN_BREAK, true);
                                    break;

                                case AppConstants.NOT_LOGGED_IN:
                                    logoutfromApp(context);
                                    break;

                            }
                        } else {
                            getDriverTripStatus(context, checkTripStatus);
                        }
                    }
                    else if(response.code() == 401) {
                        logoutfromApp(context);
                    }else {
                        getDriverTripStatus(context, checkTripStatus);
                        Log.d(TAG, "onResponse: " + response.body());

                    }

                }

                @Override
                public void onFailure(@NonNull Call<ValidateDriverResp> call, @NonNull Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                    getDriverTripStatus(context, checkTripStatus);
                }

            });

        }
    }

    private void revertHomeScreen(Context context) {
        homeActivity = HomeActivity.instance;
        if (!SharedPrefsHelper.getInstance().get(AppConstants.FirstLogin,true)) {
            if (!isHomeActivityVisible()) {
                SharedPrefsHelper.getInstance().save(AppConstants.ACCEPT, false);
                Intent intent = new Intent(context, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
        } else {
            if (homeActivity != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    homeActivity.getDeviceInfo();
                }
            }
        }
    }
}
