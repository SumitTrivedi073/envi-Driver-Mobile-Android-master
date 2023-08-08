package sgs.env.ecabsdriver.presenter;

import static sgs.env.ecabsdriver.util.DriverAlgorithm.logoutfromApp;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import retrofit2.Callback;
import retrofit2.Response;
import sgs.env.ecabsdriver.activity.LoginActivity;
import sgs.env.ecabsdriver.interfce.RegisterService;
import sgs.env.ecabsdriver.interfce.DrvMasterIDI;
import sgs.env.ecabsdriver.interfce.UImethodsI;
import sgs.env.ecabsdriver.interfce.ValidateDriverI;
import sgs.env.ecabsdriver.model.CheckDriverTripStatus;
import sgs.env.ecabsdriver.model.DrivMstIDContent;
import sgs.env.ecabsdriver.model.DriverMstIDRes;
import sgs.env.ecabsdriver.model.InfoModelB;
import sgs.env.ecabsdriver.service.LocationService;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;


// business logic when driver day starts for picking up the customers
// this api needs to be called after driver login immediatly driverMaster ID will be provided that is used in updateTrips for driver
public class DriverMasterIdPresenter implements DrvMasterIDI {

    private UImethodsI uImethodsI;
    private static final String TAG = "MasterId";
    private UImethodsI.RetryMstId retryMstId;

    @Override
    public void startRideAPI(final Context context, final InfoModelB modelB) {
        uImethodsI = (UImethodsI) context;
        retryMstId = (UImethodsI.RetryMstId) context;
        final UImethodsI.ApiFailed apiFailed = (UImethodsI.ApiFailed) context;
        final SharedPrefsHelper helper = SharedPrefsHelper.getInstance();

        String token = ECabsApp.getInstance().getAcssToken();
        if (!token.contentEquals(AppConstants.NO_TOKEN)) {
            uImethodsI.startProgessBar();

            final RegisterService service = ECabsApp.getRetrofit().create(RegisterService.class);
            retrofit2.Call<DriverMstIDRes> call = service.sendInfo(token, modelB);
            Log.d(TAG, "startRideAPI: URL " + call.request().url());
            Log.d(TAG, "startRideAPI: method " + call.request().method());
            Log.d(TAG, "startRideAPI: payLoad " + modelB);

            call.enqueue(new Callback<DriverMstIDRes>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<DriverMstIDRes> call, @NonNull
                        Response<DriverMstIDRes> response) {
                DriverMstIDRes driverMstIDRes = response.body();

                    if (response.isSuccessful() && response.code()==200) {
                        if(response.body().getMessage().contentEquals("Driver Trip Master created successfully")) {
                            responseSuccess(helper,driverMstIDRes, context.getApplicationContext());
                        }
                        else if(response.body().getMessage().contentEquals("Driver Trip Master already exist")) {
                        responseSuccess(helper,driverMstIDRes,context.getApplicationContext());
                        }
                        if (helper.get(AppConstants.FirstLogin,true)) {

                            ValidateDriverI validate = new ValidateDriverPresenter();
                            CheckDriverTripStatus checkDriverTripStatus =
                                    new CheckDriverTripStatus(SharedPrefsHelper.getInstance().get(AppConstants.MOBILE_NUM, ""),
                                            Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));
                            validate.getDriverTripStatus(context.getApplicationContext(),
                                    checkDriverTripStatus);
                            SharedPrefsHelper.getInstance().save(AppConstants.FirstLogin, false);
                        }
                    }
                    else if(response.code()==400){
                        helper.save(AppConstants.GEN_MASTER_ID,true);
                        uImethodsI.endProgressBar();
                    retryMstId.retry();
                    }else if(response.code() == 401) {
                        logoutfromApp(context);
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<DriverMstIDRes> call, Throwable t) {
                    helper.save(AppConstants.GEN_MASTER_ID,true);
                    uImethodsI.endProgressBar();
                    retryMstId.retry();
                    startRideAPI(context,modelB);
                }
            });
        }
        else {
            Toast.makeText(context, "Please Logout and login again !", Toast.LENGTH_LONG).show();
            uImethodsI.endProgressBar();
        }
    }

    private void responseSuccess(SharedPrefsHelper helper, DriverMstIDRes driverMstIDRes,
                                 Context context) {
        
        helper.save(AppConstants.GEN_MASTER_ID, false);
        uImethodsI.endProgressBar();
        DrivMstIDContent content = driverMstIDRes.getContent();
        String drvMsterId = content.getDriverTripmasterId();
        helper.save(AppConstants.DRV_MASTER_ID, drvMsterId);

    }
}
