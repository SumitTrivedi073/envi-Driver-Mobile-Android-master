package sgs.env.ecabsdriver.presenter;

import static sgs.env.ecabsdriver.util.DriverAlgorithm.logoutfromApp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sgs.env.ecabsdriver.activity.LoginActivity;
import sgs.env.ecabsdriver.interfce.RegisterService;
import sgs.env.ecabsdriver.interfce.UpdateTokenI;
import sgs.env.ecabsdriver.model.GeneralResponse;
import sgs.env.ecabsdriver.model.Login;
import sgs.env.ecabsdriver.util.ECabsApp;

public class TokenPresenter implements UpdateTokenI {

    private static final String TAG = "TokenPresenter";

    @Override
    public void updateToken(String token, Context context) {
        Log.d(TAG, "updateToken: " + token);
        String jwtToken = ECabsApp.getInstance().getAcssToken();
            RegisterService registerService = ECabsApp.getRetrofit().create(RegisterService.class);
            Login login = new Login();
            login.setFcmToken(token);
            Call<GeneralResponse> call = registerService.updateToken(jwtToken,login);
            Log.d(TAG, "sendToken: url " + call.request().url());

            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                    if(response.isSuccessful() && response.code()==200) {
                        Log.d(TAG, "onResponse: success ");
                    }
                    else if(response.code()==400) {
                        try {
                            if (response.errorBody() != null) {
                                Log.d(TAG, "onResponse: failure response  " + response.errorBody().string());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if(response.code() == 401) {
                        logoutfromApp(context);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
        }

}
