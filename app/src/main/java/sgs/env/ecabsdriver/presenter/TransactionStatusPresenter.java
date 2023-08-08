package sgs.env.ecabsdriver.presenter;

import static sgs.env.ecabsdriver.util.DriverAlgorithm.logoutfromApp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.activity.HomeActivity;
import sgs.env.ecabsdriver.activity.LoginActivity;
import sgs.env.ecabsdriver.activity.MapActivity;
import sgs.env.ecabsdriver.data.local.ECabsDriverDbHelper;
import sgs.env.ecabsdriver.interfce.PaymentStatusCheck;
import sgs.env.ecabsdriver.interfce.RegisterService;
import sgs.env.ecabsdriver.interfce.SendInvoiceI;
import sgs.env.ecabsdriver.model.CustomerNotfication;
import sgs.env.ecabsdriver.model.PaymentVerificationResponse;
import sgs.env.ecabsdriver.model.PaytmModel;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class TransactionStatusPresenter implements PaymentStatusCheck {

    private static final String TAG = "PaymentPresenter";

    AlertDialog alertDialog;

    public void verifyTransaction(Context context, String passMstId) {

        String token = ECabsApp.getInstance().getAcssToken();
        RegisterService service = ECabsApp.getRetrofit().create(RegisterService.class);
        PaytmModel paytmModel = new PaytmModel();
        paytmModel.setPassengerTripMasterId(passMstId);
        Call<PaymentVerificationResponse> call = service.verifyTransaction(token, paytmModel);
        call.enqueue(new Callback<PaymentVerificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<PaymentVerificationResponse> call,
                                   @NonNull Response<PaymentVerificationResponse> response) {

                if (response.isSuccessful() && response.code()==200) {
                    try {
                        if (response.body() != null) {
                            switch (response.body().getMessage().getPaymentStatus()) {
                                case PaymentStatusConstants.PAYMENT_COMPLETED:

                                    if (SharedPrefsHelper.getInstance().get(
                                            AppConstants.PAYMENT_MODE).equals(AppConstants.ONLINE) ||
                                            SharedPrefsHelper.getInstance().get(
                                                    AppConstants.PAYMENT_MODE).equals(AppConstants.QR_CODE)) {
                                        ShowAlertResponse(context, response.body().getMessage().getResultMsg() + " of ₹" + response.body().getMessage().getTxnAmount(), "1");
                                    } else {
                                        ShowAlertResponse(context, response.body().getMessage().getResultMsg() + " of ₹" + response.body().getMessage().getTxnAmount(), "2");

                                    }
                                    break;

                                case PaymentStatusConstants.PAYMENT_FAILED:
                                case PaymentStatusConstants.PAYMENT_PENDING:
                                case PaymentStatusConstants.PAYMENT_VERIFICATION_PENDING:
                                    ShowAlertResponse(context, response.body().getMessage().getResultMsg(), "0");

                                    break;
                            }
                        }
                    } catch (Exception e) {
                        Log.d("payment order log", "payment order id" + e.getMessage());
                        Toast.makeText(context,
                                "Encountered error during transactio, Please try again",
                                Toast.LENGTH_LONG).show();

                    }

                } else if(response.code()==400){
                    Toast.makeText(context, "Please proceed for payment", Toast.LENGTH_LONG).show();
                }else if(response.code() == 401) {
                    logoutfromApp(context);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PaymentVerificationResponse> call, @NonNull Throwable t) {
                Log.d("Order details", "payment Order details" + t.getMessage());
            }
        });

    }

    private void ShowAlertResponse(Context context, String message, String value) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.transaction_status_popup, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);

        builder.setView(layout);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();

        TextView OK_txt = layout.findViewById(R.id.OK_txt);
        TextView title_txt = layout.findViewById(R.id.title_txt);

        title_txt.setText(message);

        OK_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (value) {
                    case "1":
                        MapActivity.isFirstTime = false;
                        SharedPrefsHelper.getInstance().save(AppConstants.END_TRIP_BUTTON_CLICKED, false);
                        SharedPrefsHelper.getInstance().delete(AppConstants.USER_ID);
                        SharedPrefsHelper.getInstance().delete(AppConstants.QRCODE_DETAIL);
                        SendInvoiceI sendInvoiceI = new SendInvoicePresenter();
                        sendInvoiceI.sendInvoiceAPI(context, Double.parseDouble(SharedPrefsHelper.getInstance().get(AppConstants.AmountTobeCollected, "0.0")));
                        alertDialog.dismiss();

                        break;
                    case "2":
                        Intent intent = new Intent(context, HomeActivity.class);
                        context.startActivity(intent);
                        alertDialog.dismiss();
                        break;
                    case "0":
                        alertDialog.dismiss();
                        break;
                }
            }
        });

    }

}
