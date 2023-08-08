package sgs.env.ecabsdriver.presenter;

import static sgs.env.ecabsdriver.util.DriverAlgorithm.logoutfromApp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sgs.env.ecabsdriver.activity.LoginActivity;
import sgs.env.ecabsdriver.interfce.IUserDeviceInfo;
import sgs.env.ecabsdriver.interfce.RegisterService;
import sgs.env.ecabsdriver.model.UserDeviceInfoModel;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class UserDeviceInfoPresenter implements IUserDeviceInfo {
    private static final String TAG = "UserDeviceInfoPresenter";

    @Override
    public void saveUserDeviceInfo(UserDeviceInfoModel userDeviceInfoModel, Context context) {
        TokenPresenter tokenPresenter = new TokenPresenter();
        RegisterService registerService = ECabsApp.getRetrofit().create(RegisterService.class);
        Call<UserDeviceInfoModel> call = registerService.saveUserDeviceInfo(
                SharedPrefsHelper.getInstance().get(AppConstants.KEY_JWT_TOKEN),
                userDeviceInfoModel);
        Log.d(TAG, "UserDeviceInfoPresenter: url " + call.request().url());
        call.enqueue(new Callback<UserDeviceInfoModel>() {
            @Override
            public void onResponse(@NonNull Call<UserDeviceInfoModel> call,
                                   @NonNull Response<UserDeviceInfoModel> response) {
                if (response.isSuccessful() && response.code() ==200) {
                    int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
                    if (resultCode == ConnectionResult.SUCCESS) {

                            FirebaseMessaging.getInstance().setAutoInitEnabled(true);
                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(
                                    new OnCompleteListener<String>() {
                                        @Override
                                        public void onComplete(
                                                @NonNull com.google.android.gms.tasks.Task<String> task) {
                                            if (!task.isSuccessful()) {
                                                Log.w("TAG", "Fetching FCM registration token failed",
                                                        task.getException());
                                                return;
                                            }
                                            // Get new FCM registration token
                                            String FirebaseToken = task.getResult();
                                            Log.e("newToken", FirebaseToken);
                                            tokenPresenter.updateToken(FirebaseToken,context);
                                        }
                                    });

                    }
                    Log.d("USER DEVICE INFO", "user device info saved successfully");
                }  else if(response.code() == 401) {
                    logoutfromApp(context);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserDeviceInfoModel> call, @NonNull Throwable t) {
                Log.d("USER DEVICE INFO", "user device info saving failed");
            }
        });
    }
}
