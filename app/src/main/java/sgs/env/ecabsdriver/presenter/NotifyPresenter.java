package sgs.env.ecabsdriver.presenter;

import static sgs.env.ecabsdriver.util.DriverAlgorithm.logoutfromApp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sgs.env.ecabsdriver.activity.LoginActivity;
import sgs.env.ecabsdriver.interfce.RegisterService;
import sgs.env.ecabsdriver.interfce.Notification;
import sgs.env.ecabsdriver.interfce.UImethodsI;
import sgs.env.ecabsdriver.model.DriverStatus;
import sgs.env.ecabsdriver.model.GeneralResponse;
import sgs.env.ecabsdriver.util.ECabsApp;


public class NotifyPresenter implements Notification {
    private final String TAG = "NotifyPresenter";

    @Override
    public void notifyUsr(final Context context, final DriverStatus driverStatus) {
        String token = ECabsApp.getInstance().getAcssToken();

        if (!token.isEmpty()) {
            final UImethodsI imethodsI = (UImethodsI) context;
            imethodsI.startProgessBar();
            final RegisterService notifyData = ECabsApp.getRetrofit().create(RegisterService.class);
            retrofit2.Call<GeneralResponse> call = notifyData.notifyUser(token,driverStatus);

            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {

                    if(response.isSuccessful()&&response.code()==200) {
                    switch (driverStatus.getPassengerTripStageId()) {
                        case "arrivalAtSource" :
                            Log.d(TAG, "onResponse:source do nothing");
                            break;

                        case "arrivalAtDestiantion" :
                            Log.d(TAG, "onResponse:destiantion do nothing");
                            break;
                    }
                    } else if(response.code()==400){
                        try {
                            Log.d(TAG, "onResponse: failure " + response.errorBody().string());
                        }
                        catch (IOException e) {
                            Log.d(TAG, "onResponse: exception " + e.getMessage());
                        }
                    }else if(response.code() == 401) {
                        logoutfromApp(context);
                    }
                    imethodsI.endProgressBar();
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Toast.makeText(context, "Please try later!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onFailure: " + t.getMessage());
                    imethodsI.endProgressBar();
                }
            });
        }
        else {
            Toast.makeText(context, "Please try later!", Toast.LENGTH_SHORT).show();
        }
    }
}
