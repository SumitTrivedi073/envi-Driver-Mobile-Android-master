package sgs.env.ecabsdriver.presenter;

import static sgs.env.ecabsdriver.util.DriverAlgorithm.logoutfromApp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;

import retrofit2.Callback;
import retrofit2.Response;
import sgs.env.ecabsdriver.activity.CollectCashActivity;
import sgs.env.ecabsdriver.activity.LoginActivity;
import sgs.env.ecabsdriver.activity.WaitingForCustomerActivity;
import sgs.env.ecabsdriver.interfce.RegisterService;

import sgs.env.ecabsdriver.interfce.SendInvoiceI;
import sgs.env.ecabsdriver.interfce.UImethodsI;
import sgs.env.ecabsdriver.model.GeneralResponse;
import sgs.env.ecabsdriver.model.Price;
import sgs.env.ecabsdriver.model.TripInvoice;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class SendInvoicePresenter  implements SendInvoiceI {


    private static final String TAG = "SendInvoicePresenter";

    @Override
    public void sendInvoiceAPI(Context context,double totalFare) {
        UImethodsI uImethodsI = (UImethodsI)context;
        uImethodsI.startProgessBar();
        Log.d("totalFaree", String.valueOf(totalFare));
        final RegisterService service = ECabsApp.getRetrofit().create(RegisterService.class);
        String token = ECabsApp.getInstance().getAcssToken();
        TripInvoice tripInvoice=new TripInvoice();
        String cgst = SharedPrefsHelper.getInstance().get(AppConstants.CGST,"0");
        String sgst = SharedPrefsHelper.getInstance().get(AppConstants.SGST,"0");
        String KmFare = SharedPrefsHelper.getInstance().get(AppConstants.PER_KM,"0");
        String minutePrice = SharedPrefsHelper.getInstance().get(AppConstants.MINUTE_PRICE,"0");
        String mode = SharedPrefsHelper.getInstance().get(AppConstants.PAYMENT_MODE,"NA");
        String distance = SharedPrefsHelper.getInstance().get(AppConstants.DISTANCE,"0.0");
        String customerMailId = SharedPrefsHelper.getInstance().get(AppConstants.CUSTOMER_MAIL_ID,"NA");
        String AmountTobeCollected = SharedPrefsHelper.getInstance().get(AppConstants.AmountTobeCollected);
        String discount = SharedPrefsHelper.getInstance().get(AppConstants.DISCOUNT);
        String perKmPrice = SharedPrefsHelper.getInstance().get(AppConstants.PER_KM_PRICE);
        String fromAddress = SharedPrefsHelper.getInstance().get(AppConstants.FROM_ADDRESS);
        String toAddress = SharedPrefsHelper.getInstance().get(AppConstants.TO_ADDRESS);
        String taxableAmount = SharedPrefsHelper.getInstance().get(AppConstants.TAXABLE_AMOUNT);
        String driverPhoto = SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_PHOTO);
        String driverName = SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_FIRST_NAME);
        String tripStartTime = SharedPrefsHelper.getInstance().get(AppConstants.TRIP_START_TIME);
        String tripEndTime = SharedPrefsHelper.getInstance().get(AppConstants.TRIP_END_TIME);
        String differenceInTripTime = SharedPrefsHelper.getInstance().get(AppConstants.DIFFERENCE_IN_TRIP_TIME);
        String passengerTripMasterId = SharedPrefsHelper.getInstance().get(AppConstants.PASS_MST_ID);
        String driverTripMasterId = SharedPrefsHelper.getInstance().get(AppConstants.DRV_MASTER_ID);
        String tollAmount = SharedPrefsHelper.getInstance().get(AppConstants.TOLL_CHARGES);
        String advancePaid = SharedPrefsHelper.getInstance().get(AppConstants.ADVANCEPAID);

        Price price =new Price();
        price.setCgst(cgst);
        price.setKmFare(KmFare);
        price.setSgst(sgst);
        price.setTotalFare(AmountTobeCollected);
        price.setMinutePrice(minutePrice);
        price.setAmountTobeCollected(AmountTobeCollected);
        price.setDiscount(discount);
        price.setPerKMPrice(perKmPrice);
        price.setTaxableAmount(taxableAmount);
        price.setTollAmount(tollAmount);
        price.setAdvancePaid(advancePaid);
        tripInvoice.setPrice(price);
        tripInvoice.setPaymentMode(mode);
        tripInvoice.setDistance((distance));
        tripInvoice.setCustomerMailId(customerMailId);
        tripInvoice.setFromAddress(fromAddress);
        tripInvoice.setToAddress(toAddress);
        tripInvoice.setDriverPhoto(driverPhoto);
        tripInvoice.setDriverName(driverName);
        tripInvoice.setTripStartTime(tripStartTime);
        tripInvoice.setTripEndTime(tripEndTime);
        tripInvoice.setDifferenceInTripTime(differenceInTripTime);

        tripInvoice.setPassengerTripMasterId(passengerTripMasterId);
        tripInvoice.setDriverTripMasterId(driverTripMasterId);

        if (!token.isEmpty()) {
       
            retrofit2.Call<GeneralResponse> call = service.sendInvoiceOfTheTrip(token,tripInvoice);
            Log.d(TAG, "sendInvoiceAPI: url " + call.request().url());
            call.enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                    Log.e("Response", String.valueOf(response.body()));
                    GeneralResponse generalResponse = response.body();
                    uImethodsI.endProgressBar();
                    if (response.isSuccessful() && generalResponse != null && response.code()==200) {
                        Intent intent = new Intent(context, WaitingForCustomerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra(AppConstants.FROM_ACTIVITY, AppConstants.COLLECT_CASH_ACTIVITY);
                        context.startActivity(intent);
                    } else if(response.code()==400){
                        Toast.makeText(context, "Please try again!", Toast.LENGTH_SHORT).show();
                        try {
                            if (response.errorBody() != null) {
                                Log.d(TAG, "onResponse: " + response.errorBody().string());
                            }
                        } catch (IOException e) {
                            Log.d(TAG, "onResponse: " + e.getMessage());
                        }
                    }else if(response.code() == 401) {
                        logoutfromApp(context);
                    }
              
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<GeneralResponse> call, @NonNull Throwable t) {
                    Toast.makeText(context, "Please try later!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onFailure: " + t.getMessage());
                    uImethodsI.endProgressBar();
               
                }
            });
        }
        else {
            Toast.makeText(context, "Please try later!", Toast.LENGTH_SHORT).show();
            uImethodsI.endProgressBar();
        }
    }
}

