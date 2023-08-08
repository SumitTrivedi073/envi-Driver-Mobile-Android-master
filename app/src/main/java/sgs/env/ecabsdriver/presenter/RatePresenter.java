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
import sgs.env.ecabsdriver.activity.HomeActivity;
import sgs.env.ecabsdriver.activity.LoginActivity;
import sgs.env.ecabsdriver.interfce.RegisterService;
import sgs.env.ecabsdriver.interfce.RateI;
import sgs.env.ecabsdriver.interfce.UImethodsI;
import sgs.env.ecabsdriver.model.GeneralResponse;
import sgs.env.ecabsdriver.model.RateTrip;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class RatePresenter implements RateI {

    private UImethodsI uImethodsI;
    private static final String TAG = "RatePresenter";

    // rating api
    @Override
    public void rateCustomer(final Context context, float rate, String message) {
        String token = ECabsApp.getInstance().getAcssToken();
        Log.d(TAG, "rateCustomer: rate " + rate);
        Log.d(TAG, "rateCustomer: mesage " + message);
        if(!token.equals(AppConstants.NO_TOKEN)) {
            uImethodsI = (UImethodsI) context;
            uImethodsI.startProgessBar();

            final RegisterService service = ECabsApp.getRetrofit().create(RegisterService.class);
            RateTrip rateTrip = new RateTrip();
            if(message != null && rate != 0) {
                rateTrip.setMessage(message);
                rateTrip.setRate(rate);
            }
            else {
                rateTrip.setMessage("No input");  // when driver selects empty mesage and sets rating to zero
                rateTrip.setRate(-1);
            }
            String passMstId = SharedPrefsHelper.getInstance().get(AppConstants.PASS_MST_ID,"");
            Log.d(TAG, "rateCustomer: pass");
            rateTrip.setPassengerTripMasterId(passMstId);
            Call<GeneralResponse> call = service.rateTrip(token,rateTrip);

            Log.d(TAG, "rateCustomer: url " + call.request().url());
            Log.d(TAG, "rateCustomer: bodyInput " + rateTrip);

            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {

                    if(response.isSuccessful()&&response.code()==200) {
                        Intent intent = new Intent(context, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        // SharedPrefsHelper.getInstance().delete(AppConstants.PASS_MST_ID);
                        context.startActivity(intent);
                    }
                    else if(response.code()==400){
                        Toast.makeText(context, "Please try later!", Toast.LENGTH_SHORT).show();
                        try {
                            Log.d(TAG, "onResponse: failure " + response.errorBody().string());
                        } catch (IOException e) {
                            Log.d(TAG, "onResponse: ex" + e.getMessage());
                        }
                    }else if(response.code() == 401) {
                        logoutfromApp(context);
                    }
                    uImethodsI.endProgressBar();
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Toast.makeText(context, "Please try later", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onFailure: " + t.getMessage());
                    uImethodsI.endProgressBar();
                }
            });
        }
        else {
            Toast.makeText(context, "Please try later something went wrong !", Toast.LENGTH_SHORT).show();
        }
    }
}
