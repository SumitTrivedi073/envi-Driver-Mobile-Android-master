package sgs.env.ecabsdriver.presenter;

import static sgs.env.ecabsdriver.util.DriverAlgorithm.logoutfromApp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.activity.CollectCashActivity;
import sgs.env.ecabsdriver.activity.LoginActivity;
import sgs.env.ecabsdriver.interfce.IUpdateToll;
import sgs.env.ecabsdriver.interfce.RegisterService;
import sgs.env.ecabsdriver.model.TollAmount;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class UpdateTollPresenter implements IUpdateToll {
    private static final String TAG = "UpdateTollPresenter";
    private final SharedPrefsHelper helper = SharedPrefsHelper.getInstance();
    CollectCashActivity collectCashActivity;

    public void updateToll(Context context, TollAmount tollAmount) {
        RegisterService registerService = ECabsApp.getRetrofit().create(RegisterService.class);
        String token = helper.get(AppConstants.KEY_JWT_TOKEN);
        collectCashActivity = CollectCashActivity.instance;
        Call<TollAmount> call = registerService.updateToll(token, tollAmount);
        Log.d(TAG, "updatePaymentStatusApi: url " + call.request().url());
        Log.d(TAG, "updatePaymentStatusApi: tollAmount " + tollAmount);


        call.enqueue(new Callback<TollAmount>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<TollAmount> call, @NonNull Response<TollAmount> response) {
                if (response.isSuccessful() && response.code()==200) {
                    Log.d(TAG, "updatePaymentStatusApi: response " + response.body().toString());
                    TollAmount tollAmount1 = response.body();
                    if (tollAmount1 != null) {
                        helper.save(AppConstants.AmountTobeCollected, tollAmount1.getAmountTobeCollected().toString());
                        helper.save(AppConstants.TOLL_CHARGES, tollAmount1.getTollAmount().toString());
                        helper.save(AppConstants.ADVANCEPAID, tollAmount1.getAdvancePaid().toString());
                        helper.save(AppConstants.DISTANCE, tollAmount1.getDistanceTravelled().toString());
                        helper.save(AppConstants.CGST, tollAmount1.getCgst().toString());
                        helper.save(AppConstants.SGST,tollAmount1.getSgst().toString());
                        helper.delete(AppConstants.QRCODE_DETAIL);
                        if (collectCashActivity != null
                                && tollAmount1.getAmountTobeCollected() != null
                                && !tollAmount1.getAmountTobeCollected().toString().isEmpty()) {
                            collectCashActivity.tollAddSuccess(tollAmount1);
                        }

                    }

                }
                else if (response.code() == 400) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            String userMessage = jsonObject.getString("message");
                            if (collectCashActivity != null) {
                                collectCashActivity.tollAddFailure(userMessage);
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }

                    }
                else if(response.code() == 401) {
                    logoutfromApp(context);
                }


            }

            @Override
            public void onFailure(@NonNull Call<TollAmount> call, @NonNull Throwable t) {
                Log.d("Toll presenter", "Toll update failure" + t.getMessage());
                if (collectCashActivity != null) {
                    collectCashActivity.tollAddFailure(context.getResources().getString(R.string.toll__not_added));
                }
            }
        });

    }
}
