package sgs.env.ecabsdriver.presenter;

import static sgs.env.ecabsdriver.util.AppConstants.TRIP_ALLOTTED;
import static sgs.env.ecabsdriver.util.DriverAlgorithm.logoutfromApp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sgs.env.ecabsdriver.activity.HomeActivity;
import sgs.env.ecabsdriver.activity.LoginActivity;
import sgs.env.ecabsdriver.activity.MapActivity;
import sgs.env.ecabsdriver.interfce.RegisterService;
import sgs.env.ecabsdriver.activity.TripMapActivity;
import sgs.env.ecabsdriver.data.local.ECabsDriverDbHelper;
import sgs.env.ecabsdriver.interfce.UpdateDriverStatusI;
import sgs.env.ecabsdriver.model.Coupons;
import sgs.env.ecabsdriver.model.CustomerNotfication;
import sgs.env.ecabsdriver.model.DriverStatus;
import sgs.env.ecabsdriver.model.PromotionRegister;
import sgs.env.ecabsdriver.model.TripEndResponse;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.DateUtil;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.ProgressBarLayout;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class UpdateDriverStatusPresenter implements UpdateDriverStatusI {

    private static final String TAG = "UpdateStatus";

    private String status;

    private TripMapActivity activity;

    private SharedPrefsHelper helper;

    private ProgressBarLayout progressBarLayout;

    @Override
    public void updateDriverStatusAPI( Context context,
                                       DriverStatus driverStatus) {  // based on the driver
        progressBarLayout = new ProgressBarLayout();
        String token = ECabsApp.getInstance().getAcssToken();
        helper = SharedPrefsHelper.getInstance();
        if (progressBarLayout!=null) {
            progressBarLayout.displayDialog(context);
        }
        status = driverStatus.getStatus();
        driverStatus.setPassengerId(SharedPrefsHelper.getInstance().get(AppConstants.USER_ID));
        //Code for coupon creation for a user who is on trip if its his first ride and he is
        // refered by someone.
        Coupons coupons = new Coupons();
        coupons.setPromotionId(3);
        PromotionRegister promotionRegister = new PromotionRegister();
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        int lastDate = calendar.getActualMaximum(Calendar.DATE);
        int lastDateYear = DateUtil.getYear(today);
        int lastDateMonth = DateUtil.getMonth(today);
        Calendar myCalendar = new GregorianCalendar(lastDateYear, lastDateMonth, lastDate);
        Date validTillDate = myCalendar.getTime();
        promotionRegister.setPromotionEventReferenceId(
                SharedPrefsHelper.getInstance().get(AppConstants.PASS_MST_ID));
        promotionRegister.setExpired(false);
        promotionRegister.setCreatedDate(today);
        promotionRegister.setValidTill(validTillDate);
        promotionRegister.setApplied(false);
        promotionRegister.setValidFrom(today);
        coupons.setPromotionRegister(promotionRegister);
        coupons.setUserId(SharedPrefsHelper.getInstance().get(AppConstants.USER_ID));
        driverStatus.setCoupons(coupons);
        //End
        if (!token.contentEquals(AppConstants.NO_TOKEN)) {
            RegisterService service = ECabsApp.getRetrofit().create(RegisterService.class);
            Call<TripEndResponse> call = service.update(token, driverStatus);
            Log.d(TAG, "updateDriverStatusAPI: driverStatus " + driverStatus);
            Log.d(TAG, "updateDriverStatusAPI: token " + token);
            call.enqueue(new Callback<TripEndResponse>() {
                @Override
                public void onResponse(@NonNull Call<TripEndResponse> call,
                                       @NonNull Response<TripEndResponse> response) {
                    if (progressBarLayout != null) {
                        progressBarLayout.hideProgressDialog();
                    }
                    if (response.isSuccessful() && response.code()==200) {
                        Log.d(TAG, "updateDriverStatusAPI: onResponse = " + response.body());

                        if (response.body() != null && response.body().getTripDetails() != null) {
                            status = response.body().getTripDetails().getStatus();
                            if (response.body().getTripDetails().getScheduledAt() != null && !response.body().getTripDetails().getScheduledAt().isEmpty()) {
                                helper.save(AppConstants.TripScheduledAt, response.body().getTripDetails().getScheduledAt());
                            }

                            if (String.valueOf(response.body().getTripDetails().getScheduledTrip()) != null &&
                                    !String.valueOf(response.body().getTripDetails().getScheduledTrip()).isEmpty()) {

                                helper.save(AppConstants.IsScheduleTrip, response.body().getTripDetails().getScheduledTrip());
                            }
                            if (response.body().getTripDetails().getSpecialRemarks() != null
                                    && !response.body().getTripDetails().getSpecialRemarks().isEmpty()) {

                                helper.save(AppConstants.SpecialRemraks, response.body().getTripDetails().getSpecialRemarks());
                            }
                            }

                        Log.e("status====>",status.toString());
                        Log.e("response====>",response.body() .toString());
                        helper.save(AppConstants.TripStatus,status);

                        switch (status) {
                            case AppConstants.TRIP_ONBOARDING:
                                SharedPrefsHelper.getInstance().delete(AppConstants.ARRIVED);
                                SharedPrefsHelper.getInstance().delete(AppConstants.IsWait);
                                activity = (TripMapActivity) context;
                                activity.moveToGoogleMapsNavigation();
                                break;
                            case AppConstants.TRIP_COMPLETED:
                                TripEndResponse tripEndResponse =
										response.body();
                                Log.d("tripEndResponse", String.valueOf(tripEndResponse));
                                int check = driverStatus.getDif();
                                if (tripEndResponse != null && check != -1 &&tripEndResponse.getPrice()!=null) {
                                    helper.save(AppConstants.DISTANCE, tripEndResponse.getPrice().getDistanceTravelled());
                                    helper.save(AppConstants.CGST,
                                            tripEndResponse.getPrice().getCgst());
                                    helper.save(AppConstants.SGST,
                                            tripEndResponse.getPrice().getSgst());
                                    helper.save(AppConstants.PER_KM,
                                            tripEndResponse.getPrice().getKmFare());
                                    helper.save(AppConstants.TRIP_END, true);
                                    helper.save(AppConstants.ACCEPT, false);
                                    helper.save(AppConstants.PAYMENT_MODE, tripEndResponse.getPaymentMode());
                                    helper.save(AppConstants.CUSTOMER_MAIL_ID,
                                            tripEndResponse.getCustomerMailId());
                                    helper.save(AppConstants.MINUTE_PRICE,
                                            tripEndResponse.getPrice().getMinutePrice());
                                    /*helper.save(AppConstants.TOTAL_FARE,
                                            tripEndResponse.getPrice().getTotalPrice());*/
                                    helper.save(AppConstants.DISCOUNTED_PRICE,
                                            tripEndResponse.getPrice().getDiscountedPrice());
                                    helper.save(AppConstants.AmountTobeCollected,
                                            tripEndResponse.getPrice().getAmountTobeCollected());
                                    helper.save(AppConstants.ADVANCEPAID,
                                            tripEndResponse.getPrice().getAdvancePaid());
                                    helper.save(AppConstants.DISCOUNT,
                                            tripEndResponse.getPrice().getDiscount());
                                    helper.save(AppConstants.PER_KM_PRICE,
                                            tripEndResponse.getPrice().getPerKMPrice());
                                    helper.save(AppConstants.FROM_ADDRESS,
                                            tripEndResponse.getFromAddress());
                                    helper.save(AppConstants.TO_ADDRESS,
                                            tripEndResponse.getToAddress());
                                    helper.save(AppConstants.TAXABLE_AMOUNT,
                                            tripEndResponse.getPrice().getTaxableAmount());
                                    helper.save(AppConstants.DRIVER_PHOTO,
                                            tripEndResponse.getDriverPhoto());
                                    helper.save(AppConstants.DRIVER_FIRST_NAME,
                                            tripEndResponse.getDriverName());
                                    helper.save(AppConstants.TRIP_START_TIME,
                                            tripEndResponse.getTripStartTime());
                                    helper.save(AppConstants.TRIP_END_TIME,
                                            tripEndResponse.getTripEndTime());
                                    helper.save(AppConstants.DIFFERENCE_IN_TRIP_TIME,
                                            tripEndResponse.getDifferenceInTripTime());
                                    if (tripEndResponse.getPrice().getTollAmount() != null && !tripEndResponse.getPrice().getTollAmount().isEmpty()) {
                                        helper.save(AppConstants.TOLL_CHARGES, tripEndResponse.getPrice().getTollAmount());
                                    } else {
                                        helper.save(AppConstants.TOLL_CHARGES, "");
                                    }

                                }
                                SharedPrefsHelper.getInstance().save(AppConstants.ACCEPT, false);
                                SharedPrefsHelper.getInstance().save(AppConstants.IsScheduleTrip,false);
                                SharedPrefsHelper.getInstance().save(AppConstants.TripScheduledAt,"");
                                SharedPrefsHelper.getInstance().save(AppConstants.SourceArrivalTime,"");
                                if (check == -1) {
                                    Log.d(TAG, "onResponse: ");
                                } else {
                                    activity = (TripMapActivity) context;
                                    if (activity!=null && !activity.isActivityFinished) {
                                        activity.endTrip();
                                    }
                                }
                                break;
                            default:
                                SharedPrefsHelper.getInstance().save(AppConstants.ACCEPT, false);
                                Intent intent = new Intent(context, HomeActivity.class);
                                context.startActivity(intent);
                                ((TripMapActivity) context).finish();
                                break;
                            case "arrivalAtSource":
                            case "arrivalAtDestination":
                                Log.d(TAG, "do nothing here for backend purpose");
                                break;
                            case AppConstants.TRIP_ALLOTTED:
                                SharedPrefsHelper.getInstance().save(AppConstants.ACCEPT, true);
                                SharedPrefsHelper.getInstance().save(AppConstants.SourceArrivalTime,"");
                                Intent intent2 = new Intent(context, MapActivity.class);
                                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                context.startActivity(intent2);
                                Toast.makeText(context, "acknowledge", Toast.LENGTH_LONG).show();
                                break;
                            case AppConstants.STATION_GO:
                                Toast.makeText(context, "Charging status updated",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case AppConstants.START_CHARGE:
                                Toast.makeText(context, "Charging status updated",
                                        Toast.LENGTH_SHORT).show();
                                CustomerNotfication notfication = new CustomerNotfication();
                                notfication.setRideStatus(AppConstants.START_CHARGE);
                                break;
                            case AppConstants.CANCELLED:
                            case AppConstants.DRIVER_CANCELLED:
                                SharedPrefsHelper.getInstance().save(AppConstants.ACCEPT, false);
                                TripMapActivity activity = (TripMapActivity) context;
                                if (activity!=null ) {
                                    activity.cancelSuccess();
                                }
                                break;
                            case AppConstants.ARRIVED:
                                activity = (TripMapActivity) context;
                                if (activity!=null && !activity.isActivityFinished) {
                                    activity.arrivedSuccess();
                                }
                                Toast.makeText(context, "Notification sent to Customer",
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                        Log.d(TAG, "onResponse: sucess " + response);
                    } else if(response.code() == 400)  {
                        if (progressBarLayout != null) {
                            progressBarLayout.hideProgressDialog();
                        }
                        Log.e("on400_status", status);
                        switch (status) {
                            case "booked":

                              		if (progressBarLayout != null) {
										progressBarLayout.hideProgressDialog();
                                    Toast.makeText(context, "Wrong OTP", Toast.LENGTH_SHORT).show();
                                } else {
									if (progressBarLayout != null) {
										progressBarLayout.hideProgressDialog();
									}
                                    Toast.makeText(context, "Unable to validate the otp!",
                                            Toast.LENGTH_SHORT).show();
                                }
                                activity = (TripMapActivity) context;
                                if (activity!=null && !activity.isActivityFinished) {
                                    activity.apiFailed();
                                }
                                break;
                            case "free":
                                Toast.makeText(context, "unable to end the trip!",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            case "break":
                                Toast.makeText(context, "break request failed please try later!",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case "arrivalAtSource":
                            case "arrivalAtDestination":
                            case AppConstants.STATION_GO:
                            case AppConstants.START_CHARGE:
                            case AppConstants.TRIP_ALLOTTED:
                                Toast.makeText(context, "sorry something went wrong,Please try later again !",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case "cancel":
                            case "cancelled":
                            case "driverCancelled":
                                try {
                                    Log.d(TAG, "cancel status " + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                TripMapActivity activity = (TripMapActivity) context;
                                if (activity!=null && !activity.isActivityFinished) {
                                    activity.cancelFailure();
                                }
                                try {
                                    Log.d(TAG, "onResponse: " + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case AppConstants.ARRIVED:
                                try {
                                    Log.d(TAG, "onResponse: " + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                        try {
                            Log.d(TAG, "onResponse: failure " + response.errorBody().string());
                        } catch (IOException e) {
                            Log.d(TAG, "onResponse: exceo" + e.getMessage());
                        }
                    } else if(response.code() == 401) {
                        logoutfromApp(context);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<TripEndResponse> call, @NonNull Throwable t) {
                    if (progressBarLayout != null) {
                        progressBarLayout.hideProgressDialog();
                    }
                    Log.e("onFailure_status", status);
                    switch (status) {
                        case "booked":
                            Toast.makeText(context, "Unable to validate the otp!",
                                    Toast.LENGTH_SHORT).show();
                            activity = (TripMapActivity) context;
                            if (activity!=null && !activity.isActivityFinished) {
                                activity.apiFailed();
                            }
                            break;
                        case "free":
                            Toast.makeText(context, "unable to end the trip!",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        case "break":
                            Toast.makeText(context, "break request failed please try later!",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case "arrivalAtSource":
                        case "arrivalAtDestination":
                            Toast.makeText(context, "sorry something went wrong,Please try later again",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case AppConstants.TRIP_ALLOTTED:
                        case AppConstants.STATION_GO:
                        case AppConstants.START_CHARGE:
                            Toast.makeText(context, "sorry something went wrong,Please try later again !", Toast.LENGTH_SHORT).show();
                            break;
                        case "cancel":
                        case "driverCancelled":
                            Log.d(TAG, "onFailure: " + t.getMessage());
                            TripMapActivity activity = (TripMapActivity) context;
                            if (activity!=null && !activity.isActivityFinished) {
                                activity.cancelFailure();
                            }
                            Log.d(TAG, "onFailure: " + t.getMessage());
                            break;
                        case AppConstants.ARRIVED:
                            activity = (TripMapActivity) context;
                            if (activity!=null && !activity.isActivityFinished) {
                                activity.arrivedFailure();
                            }
                            Log.d(TAG, "onFailure: " + t.getMessage());
                    }
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
        } else {
            Toast.makeText(context, "Please Logout and login again!", Toast.LENGTH_LONG).show();
        }
    }

}

