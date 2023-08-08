package sgs.env.ecabsdriver.presenter;
import static sgs.env.ecabsdriver.util.DriverAlgorithm.logoutfromApp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.activity.CollectCashActivity;
import sgs.env.ecabsdriver.activity.LoginActivity;
import sgs.env.ecabsdriver.interfce.QRGenerateCheck;
import sgs.env.ecabsdriver.interfce.RegisterService;
import sgs.env.ecabsdriver.model.PaytmModel;
import sgs.env.ecabsdriver.model.QRCodeModel;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class QRGeneratePresenter implements QRGenerateCheck {

    private static final String TAG = "TokenPresenter";
    CollectCashActivity collectCashActivity;

    public void generateQRcode(Context context, String passMstId, ImageView qr_image, TextView refreshQR, double totalfare) {
        Log.d(TAG, "passMstId: " + passMstId);
        String token = ECabsApp.getInstance().getAcssToken();
        RegisterService service = ECabsApp.getRetrofit().create(RegisterService.class);
        collectCashActivity = CollectCashActivity.instance;

        PaytmModel paytmModel = new PaytmModel();
        paytmModel.setPassengerTripMasterId(passMstId);
        Call<QRCodeModel> call = service.generateQRCode(token, paytmModel);
        Log.d(TAG, "sendToken: url " + call.request().url());
        Log.d(TAG, "sendToken: url " + paytmModel);
        call.enqueue(new Callback<QRCodeModel>() {
            @Override
            public void onResponse(@NonNull Call<QRCodeModel> call, @NonNull Response<QRCodeModel> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: success"+response.body().toString());

                    if (response.body() != null && response.code()==200 && response.body().getImage()!=null) {
                        byte[] decodedString = Base64.decode(response.body().getImage().toString().getBytes(StandardCharsets.UTF_8)
                                , Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        Glide.with(context).load(bitmap).placeholder(ContextCompat.getDrawable(context, R.drawable.reload_qr)).into(qr_image);

                        QRCode_Detail qrCode_detail = new QRCode_Detail();
                        qrCode_detail.setImage(response.body().getImage());
                        qrCode_detail.setPassangerTripMasterID(passMstId);
                        qrCode_detail.setTotalfare(totalfare);
                        SharedPrefsHelper.getInstance().saveModelClass(AppConstants.QRCODE_DETAIL,qrCode_detail);

                        if (collectCashActivity != null) {
                            collectCashActivity.buttonDisable(refreshQR);
                        }

                    }else if (response.code() == 400) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            String userMessage = jsonObject.getString("message");
                            if (collectCashActivity != null) {
                                collectCashActivity.generateQRFailure(userMessage);
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }

                    }
                } else if(response.code() == 401) {
                    logoutfromApp(context);
                }
            }

            @Override
            public void onFailure(@NonNull Call<QRCodeModel> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                if (collectCashActivity != null) {
                    collectCashActivity.generateQRFailure(t.getMessage());
                }
            }
        });
    }

}
