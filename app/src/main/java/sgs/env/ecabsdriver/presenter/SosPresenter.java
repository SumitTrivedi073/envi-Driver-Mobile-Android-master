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
import sgs.env.ecabsdriver.interfce.SosInterface;
import sgs.env.ecabsdriver.interfce.UiMethods;
import sgs.env.ecabsdriver.model.GeneralResponse;
import sgs.env.ecabsdriver.model.Sos;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.ECabsApp;

public class SosPresenter implements SosInterface {

    private static final String TAG = "SosPresenter";

    @Override
    public void sosAPI(final Context context, Sos sos) {

        String token = ECabsApp.getInstance().getAcssToken();
        final UiMethods uImethodsI = (UiMethods) context;
        uImethodsI.progresStart();

        if(!token.equals(AppConstants.NO_TOKEN)) {

            final RegisterService service = ECabsApp.getRetrofit().create(RegisterService.class);
            Call<GeneralResponse> call = service.sosFun(token,sos);
            Log.d(TAG, "sosAPI: url " + call.request().url());
            Log.d(TAG, "sosAPI: input payload  " + sos);

            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {

                    if(response.isSuccessful() && response.code()==200) {
                        Toast.makeText(context, ""+ response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    else if(response.code()==400){
                        try {
                            Log.d(TAG, "onResponse: failure " + response.errorBody().string());
                            Toast.makeText(context, "Unable to send sos, Please try later!" +
                                    response.errorBody().string(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Log.d(TAG, "onResponse: exception " + e.getMessage());
                        }
                    }
                    else if(response.code() == 401) {
                        logoutfromApp(context);
                    }
                    uImethodsI.progressEnd();
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Toast.makeText(context, "Unable to send sos, Please try later" +t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "onFailure: " + t.getMessage());
                    uImethodsI.progressEnd();
                }
            });
        }
        else {
            Toast.makeText(context, "Unable to send sos, Please try later", Toast.LENGTH_LONG).show();
        }
    }
}
