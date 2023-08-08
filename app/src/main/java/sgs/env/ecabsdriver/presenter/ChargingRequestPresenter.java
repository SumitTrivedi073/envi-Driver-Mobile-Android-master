package sgs.env.ecabsdriver.presenter;

import static sgs.env.ecabsdriver.util.DriverAlgorithm.logoutfromApp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.activity.ChargingStationDetail;
import sgs.env.ecabsdriver.activity.LoginActivity;
import sgs.env.ecabsdriver.interfce.RegisterService;
import sgs.env.ecabsdriver.data.network.NetworkConfig;
import sgs.env.ecabsdriver.interfce.IChargingRequest;
import sgs.env.ecabsdriver.model.ChargingRequest;
import sgs.env.ecabsdriver.model.RequestBodyToStartCharging;
import sgs.env.ecabsdriver.util.DriverAlgorithm;
import sgs.env.ecabsdriver.util.ECabsApp;

public class ChargingRequestPresenter implements IChargingRequest {
    String token = ECabsApp.getInstance().getAcssToken();
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(NetworkConfig.DC_FAST_CHARGING_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    RegisterService registerService = retrofit.create(RegisterService.class);

    @Override
    public void createChargingRequest(ChargingRequest chargingRequest) {


        Call<ChargingRequest>  call = registerService.createChargingRequest(token, chargingRequest);
        call.enqueue(new Callback<ChargingRequest>() {
            @Override
            public void onResponse(@NonNull Call<ChargingRequest> call, @NonNull Response<ChargingRequest> response) {
                if(response.isSuccessful()){
                    Log.d("Charging Request", "Charging request created successfully");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChargingRequest> call, @NonNull Throwable t) {

            }
        });


    }

    public void generateOtpAndSendToDriver(Context context, ChargingRequest chargingRequest){
        Call<ChargingRequest> call = registerService.generateOtpForCharging(token, chargingRequest);
        call.enqueue(new Callback<ChargingRequest>() {
            @Override
            public void onResponse(@NonNull Call<ChargingRequest> call, @NonNull Response<ChargingRequest> response) {
                if(response.isSuccessful() && response.code()==200){
                    Toast.makeText(context, R.string.otp_sent_successfully, Toast.LENGTH_LONG).show();
                    ChargingStationDetail chargingStationDetail = (ChargingStationDetail)context;
                    chargingStationDetail.findViewById(R.id.otp_verification_for_dc_charging).setVisibility(View.VISIBLE);
                    chargingStationDetail.findViewById(R.id.charging_station_detail_screen_footer).setVisibility(View.GONE);
                    chargingStationDetail.findViewById(R.id.progressBar_on_start_charging).setVisibility(View.GONE);
                }
                else if(response.code()==400){
                    Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                }else if(response.code() == 401) {
                    logoutfromApp(context);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChargingRequest> call, @NonNull Throwable t) {
                Toast.makeText(context, R.string.try_again, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void startCharging(Context context, RequestBodyToStartCharging requestBodyToStartCharging, ChargingStationDetail chargingStationDetail){
        ChargingStationDetail chargingStationDetail1 = (ChargingStationDetail) context;
        Call<RequestBodyToStartCharging> call = registerService.startCharging(token, requestBodyToStartCharging);

        call.enqueue(new Callback<RequestBodyToStartCharging>() {
            @Override
            public void onResponse(@NonNull Call<RequestBodyToStartCharging> call, @NonNull Response<RequestBodyToStartCharging> response) {
                chargingStationDetail1.findViewById(R.id.progress_bar_on_otp_verification_for_charging).setVisibility(View.INVISIBLE);
                if(response.isSuccessful() && response.code()==200){
//                    Intent intent = new Intent(context, ChargingIn)

                    chargingStationDetail1.findViewById(R.id.progress_bar_on_otp_verification_for_charging).setVisibility(View.INVISIBLE);
                    Toast.makeText(context, requestBodyToStartCharging.getMessage(), Toast.LENGTH_LONG).show();

                }
                else if(response.code()==400){
                    Toast.makeText(context, "Could not start charging. ", Toast.LENGTH_LONG).show();
                    chargingStationDetail1.findViewById(R.id.verify_otp_and_proceed).setVisibility(View.VISIBLE);
                }else if(response.code() == 401) {
                    logoutfromApp(context);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RequestBodyToStartCharging> call, @NonNull Throwable t) {
                chargingStationDetail1.findViewById(R.id.progress_bar_on_otp_verification_for_charging).setVisibility(View.INVISIBLE);
                chargingStationDetail1.findViewById(R.id.verify_otp_and_proceed).setVisibility(View.VISIBLE);
                Toast.makeText(context, "Error occured" + t, Toast.LENGTH_LONG).show();

            }
        });
    }
}
