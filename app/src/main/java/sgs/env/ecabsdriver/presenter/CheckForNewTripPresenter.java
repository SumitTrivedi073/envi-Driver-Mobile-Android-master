package sgs.env.ecabsdriver.presenter;

import static sgs.env.ecabsdriver.util.DriverAlgorithm.logoutfromApp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sgs.env.ecabsdriver.activity.LoginActivity;
import sgs.env.ecabsdriver.interfce.RegisterService;
import sgs.env.ecabsdriver.activity.WaitingForCustomerActivity;
import sgs.env.ecabsdriver.interfce.ICheckForTrip;
import sgs.env.ecabsdriver.interfce.IHttpStatusCodeConstants;
import sgs.env.ecabsdriver.model.CustomerNotfication;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class CheckForNewTripPresenter implements ICheckForTrip {
    @SuppressLint("LongLogTag")
    @Override
    public void checkForTrip(Context context, String driverTripMasterId) {
        RegisterService registerService = ECabsApp.getRetrofit().create(RegisterService.class);
        String token = SharedPrefsHelper.getInstance().get(AppConstants.KEY_JWT_TOKEN);
        Call<CustomerNotfication> call = registerService.checkForNewTrip(token, driverTripMasterId);
        String TAG = "CheckForNewTripPresenter";
        Log.d(TAG, "checkForTrip: url " + call.request().url());
        Log.d(TAG, "checkForTrip: token " + token);

        call.enqueue(new Callback<CustomerNotfication>() {
            @Override
            public void onResponse(@NonNull Call<CustomerNotfication> call, @NonNull Response<CustomerNotfication> response) {
                Log.d("response code", "response code--checkTrip" + response.code());
                    if(response.isSuccessful()&& response.code() == 200){
                        WaitingForCustomerActivity waitingForCustomerActivity = (WaitingForCustomerActivity)context;
                            waitingForCustomerActivity.setRequestDetailsFromFirebaseToLocalDb();

                    }
                    else if(response.code()==400){
                        Toast.makeText(context, "Currently no trips available", Toast.LENGTH_SHORT).show();
                    }else if(response.code() == 401) {
                        logoutfromApp(context);
                    }
            }

            @Override
            public void onFailure(@NonNull Call<CustomerNotfication> call, @NonNull Throwable t) {
                checkForTrip(context,driverTripMasterId);
                Toast.makeText(context, "Could not fetch the trip details. Please try again", Toast.LENGTH_LONG).show();
            }
        });
    }
}
