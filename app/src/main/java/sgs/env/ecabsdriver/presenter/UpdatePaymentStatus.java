package sgs.env.ecabsdriver.presenter;

import static sgs.env.ecabsdriver.util.DriverAlgorithm.logoutfromApp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import sgs.env.ecabsdriver.activity.LoginActivity;
import sgs.env.ecabsdriver.adapter.ReadPaginationAdapter;
import sgs.env.ecabsdriver.interfce.RegisterService;
import sgs.env.ecabsdriver.interfce.UpdatePaymentStatuss;
import sgs.env.ecabsdriver.model.GeneralResponse;
import sgs.env.ecabsdriver.model.PaymentVerificationResponse;
import sgs.env.ecabsdriver.model.PaytmModel;
import sgs.env.ecabsdriver.model.QRCodeModel;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class UpdatePaymentStatus implements UpdatePaymentStatuss {

    private static final String TAG = "UpdatePaymentStatusApi";

    @Override
    public void updatePaymentSatusApi(Context context, PaytmModel paytmModel, TextView processingText, TextView btnCollect, TextView paymentStatus) {
        String token = ECabsApp.getInstance().getAcssToken();
        RegisterService service = ECabsApp.getRetrofit().create(RegisterService.class);
        Call<PaymentVerificationResponse> call = service.verifyTransaction(token, paytmModel);
        call.enqueue(new Callback<PaymentVerificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<PaymentVerificationResponse> call,
                                   @NonNull Response<PaymentVerificationResponse> response) {

                if (response.isSuccessful() &&  response.code()==200) {
                    try {
                        if (response.body() != null) {
                            switch (response.body().getMessage().getPaymentStatus()) {
                                case PaymentStatusConstants.PAYMENT_COMPLETED:

                                    onSuccessAPI(processingText, paymentStatus);
                                    break;

                                case PaymentStatusConstants.PAYMENT_FAILED:
                                case PaymentStatusConstants.PAYMENT_PENDING:
                                case PaymentStatusConstants.PAYMENT_VERIFICATION_PENDING:
                                    onFailureApi(processingText, btnCollect);
                                    break;
                            }
                        }
                    } catch (Exception e) {
                        Log.d("payment order log", "payment order id" + e.getMessage());
                        Toast.makeText(context,
                                "Encountered error during transactio, Please try again",
                                Toast.LENGTH_LONG).show();

                    }

                }else if(response.code() == 400){
                    onFailureApi(processingText, btnCollect);
                    Toast.makeText(context, "Please proceed for payment", Toast.LENGTH_LONG).show();
                }  else if(response.code() == 401) {
                    logoutfromApp(context);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PaymentVerificationResponse> call, @NonNull Throwable t) {
                Log.d("Order details", "payment Order details" + t.getMessage());
                onFailureApi(processingText, btnCollect);
            }
        });
    }

    @Override
    public void updatePaymentSatusApiforcash(Context context, sgs.env.ecabsdriver.model.UpdatePaymentStatus updatePaymentStatus, TextView processingText, TextView btnCollect, TextView paymentStatus) {
        RegisterService service = ECabsApp.getRetrofit().create(RegisterService.class);
        String token = SharedPrefsHelper.getInstance().get(AppConstants.KEY_JWT_TOKEN, "");
        String userId = SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, "");
        Log.d(TAG, "tripHistoryAPI: driverId " + userId);
        if (!token.isEmpty()) {
            Log.d(TAG, "token: " + token);
            retrofit2.Call<GeneralResponse> call = service.UpdatePaymentStatus(token, updatePaymentStatus);
            Log.d(TAG, "c: url " + call.request().url());

            call.enqueue(new Callback<GeneralResponse>() {

                @Override
                public void onResponse(@NonNull retrofit2.Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                    Log.d(TAG, "onSuccess: " + response);
                    if (response.isSuccessful()) {
                        Log.d(TAG, "onResponse: payment update successful");
                        onSuccessAPI(processingText, paymentStatus);
                    } else {
                        Log.d(TAG, "onResponse: payment update failed");
                        onFailureApi(processingText, btnCollect);
                    }
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<GeneralResponse> call, @NonNull Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                    onFailureApi(processingText, btnCollect);
                }
            });

        }
    }

    @Override
    public void generateQRcode(Context context, String passengerTripMasterId, ImageView qrImg, TextView generateQR, double totalfare) {
        Log.d(TAG, "passMstId: " + passengerTripMasterId);
        String token = ECabsApp.getInstance().getAcssToken();
        RegisterService service = ECabsApp.getRetrofit().create(RegisterService.class);
        PaytmModel paytmModel = new PaytmModel();
        paytmModel.setPassengerTripMasterId(passengerTripMasterId);
        Call<QRCodeModel> call = service.generateQRCode(token, paytmModel);
        Log.d(TAG, "sendToken: url " + call.request().url());

        call.enqueue(new Callback<QRCodeModel>() {
            @Override
            public void onResponse(@NonNull Call<QRCodeModel> call, @NonNull Response<QRCodeModel> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: success ");

                    if (response.body() != null) {
                        byte[] decodedString = Base64.decode(response.body().getImage().toString().getBytes(StandardCharsets.UTF_8)
                                , Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        Glide.with(context).load(bitmap).placeholder(ContextCompat.getDrawable(context, R.drawable.reload_qr)).into(qrImg);

                        QRCode_Detail qrCode_detail = new QRCode_Detail();
                        qrCode_detail.setImage(response.body().getImage());
                        qrCode_detail.setPassangerTripMasterID(passengerTripMasterId);
                        qrCode_detail.setTotalfare(totalfare);
                        SharedPrefsHelper.getInstance().saveModelClass(AppConstants.QRCODE_DETAIL,qrCode_detail);


                    }
                } else {

                    Glide.with(context).load(ContextCompat.getDrawable(context, R.drawable.reload_qr)).into(qrImg);
                    if (response.code() == 400) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            String userMessage = jsonObject.getString("message");
                            Toast.makeText(context, userMessage, Toast.LENGTH_LONG).show();
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<QRCodeModel> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                Glide.with(context).load(ContextCompat.getDrawable(context, R.drawable.reload_qr)).into(qrImg);
            }
        });
    }

    private void onSuccessAPI(TextView processingText, TextView paymentStatus) {
        processingText.setText(R.string.confirmed);
        paymentStatus.setVisibility(View.GONE);
    }

    private void onFailureApi(TextView processingText, TextView btnCollect) {
        processingText.setVisibility(View.GONE);
        btnCollect.setText(R.string.retry);
        btnCollect.setVisibility(View.VISIBLE);
    }



}
