package sgs.env.ecabsdriver.presenter;

import static sgs.env.ecabsdriver.util.DriverAlgorithm.logoutfromApp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sgs.env.ecabsdriver.activity.LoginActivity;
import sgs.env.ecabsdriver.interfce.RegisterService;
import sgs.env.ecabsdriver.data.local.ECabsDriverDbHelper;
import sgs.env.ecabsdriver.interfce.UpdateDriverWaitingForTripI;
import sgs.env.ecabsdriver.model.DriverWaitingForTrip;
import sgs.env.ecabsdriver.model.GeneralResponse;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;



public class UpdateDriverWaitingForTripPresenter implements UpdateDriverWaitingForTripI {

    private static final String TAG = "UpdateStatus";
    private String status;
    private ECabsDriverDbHelper dbHelper;
    private SharedPrefsHelper helper;

    public void updateDriverWaitingAPI(final Context context, final DriverWaitingForTrip driverWaitingForTrip) {
        String token = ECabsApp.getInstance().getAcssToken();
        dbHelper = new ECabsDriverDbHelper(context);

        if (!token.contentEquals(AppConstants.NO_TOKEN)) {
            RegisterService service = ECabsApp.getRetrofit().create(RegisterService.class);
            Call<GeneralResponse> call = service.updateDriverWaitingForTrip(token, driverWaitingForTrip);

            Log.d(TAG, "updateDriverWaitingAPI: body " + driverWaitingForTrip);

           call.enqueue(new Callback<GeneralResponse>() {
               @Override
               public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                   if(response.isSuccessful() && response.code()==200) {

                    Log.d(TAG, "response: body " + response);

                   } else if(response.code() == 401) {
                       logoutfromApp(context);
                   }
               }

               @Override
               public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                   Log.d(TAG, "throw: body " + t);

               }
           });
        }

    }}