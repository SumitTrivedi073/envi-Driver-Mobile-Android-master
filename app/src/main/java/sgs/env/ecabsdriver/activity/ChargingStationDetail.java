package sgs.env.ecabsdriver.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.interfce.IChargingRequest;
import sgs.env.ecabsdriver.model.ChargeStation;
import sgs.env.ecabsdriver.model.ChargingRequest;
import sgs.env.ecabsdriver.model.RequestBodyToStartCharging;
import sgs.env.ecabsdriver.presenter.ChargingRequestPresenter;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class ChargingStationDetail extends AppCompatActivity {


    private Button startChargingButton,verifyAndProceed;
    private final IChargingRequest iChargingRequest = new ChargingRequestPresenter();
    private final SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
    private TextView resend, chargingStationAddress,connectorId,connectorPowerLevel,chargerId,connectorIdInConnectorAllocationDetailScreen;
    private ProgressBar progressBar, progressBarOnClickOfVerifyAndProceed;
    private EditText otp_field_one,otp_field_two,otp_field_three,otp_field_four,otp_field_five;
    private final int textSize = 1;
    private String otp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging_station_detail);
         startChargingButton = findViewById(R.id.start_charging);
         resend = findViewById(R.id.resend_otp_for_charging);
         chargingStationAddress = findViewById(R.id.charging_station_address);
         connectorId = findViewById(R.id.connectorId);
         connectorIdInConnectorAllocationDetailScreen = findViewById(R.id.connectorId_in_connector_allocation);
         connectorPowerLevel = findViewById(R.id.connector_power_level);
         chargerId = findViewById(R.id.charger_id);
         progressBar = findViewById(R.id.progressBar_on_start_charging);
         otp_field_one = findViewById(R.id.otp_field_one);
        otp_field_two = findViewById(R.id.otp_field_two);
        otp_field_three = findViewById(R.id.otp_field_three);
        otp_field_four = findViewById(R.id.otp_field_four);
        otp_field_five = findViewById(R.id.otp_field_five);
        verifyAndProceed = findViewById(R.id.verify_otp_and_proceed);
        progressBarOnClickOfVerifyAndProceed = findViewById(R.id.progress_bar_on_otp_verification_for_charging);



        Gson gson = new Gson();
        String chargingStationJson = SharedPrefsHelper.getInstance().get(AppConstants.CHARGING_STATION_JSON);
        ChargeStation chargeStation = gson.fromJson(chargingStationJson, ChargeStation.class);
        chargingStationAddress.setText(chargeStation.getAddress());
        connectorId.setText(chargeStation.getConnectorId());
        connectorPowerLevel.setText(chargeStation.getConnectorPowerLevel());
        chargerId.setText(chargeStation.getChargerId());
        connectorIdInConnectorAllocationDetailScreen.setText(chargeStation.getConnectorId());

         startChargingButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 generateOtpWIthChargingReqeust();
                 startChargingButton.setVisibility(View.GONE);
                 progressBar.setVisibility(View.VISIBLE);

             }
         });

         resend.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                clearOtp();
                 generateOtpWIthChargingReqeust();
             }
         });

         otp_field_one.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(otp_field_one.getText().toString().length() == textSize){
                        otp_field_two.requestFocus();
                    }
             }

             @Override
             public void afterTextChanged(Editable s) {

             }
         });
        otp_field_two.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(otp_field_two.getText().toString().length() == textSize){
                    otp_field_three.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp_field_three.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(otp_field_three.getText().toString().length() == textSize){
                    otp_field_four.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp_field_four.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(otp_field_four.getText().toString().length() == textSize){
                    otp_field_five.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        verifyAndProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyAndProceed.setVisibility(View.GONE);
                progressBarOnClickOfVerifyAndProceed.setVisibility(View.VISIBLE);
                otp = makeOtp();
                IChargingRequest iChargingRequest = new ChargingRequestPresenter();
                RequestBodyToStartCharging requestBodyToStartCharging = new RequestBodyToStartCharging();
                requestBodyToStartCharging.setCarRegistrationNumber(sharedPrefsHelper.get(AppConstants.DRIVER_VEH_NO));
                requestBodyToStartCharging.setConnectorId(sharedPrefsHelper.get(AppConstants.CONNECTOR_ID));
                requestBodyToStartCharging.setDriverMobileNumber(AppConstants.MOBILE_NUM);
                requestBodyToStartCharging.setDriverName(AppConstants.DRIVER_NAME);
                requestBodyToStartCharging.setOtp(otp);
                requestBodyToStartCharging.setSocAtCharging(AppConstants.LATEST_SOC);

                iChargingRequest.startCharging(ChargingStationDetail.this, requestBodyToStartCharging,ChargingStationDetail.this);
            }
        });
    }

    public void generateOtpWIthChargingReqeust(){
        ChargingRequest chargingRequest = new ChargingRequest();
        chargingRequest.setCarRegsitrationNumber(sharedPrefsHelper.get(AppConstants.DRIVER_VEH_NO));
        chargingRequest.setCarId(sharedPrefsHelper.get(AppConstants.DRV_VEH_ID));
        chargingRequest.setDriverPhone(sharedPrefsHelper.get(AppConstants.MOBILE_NUM));
        chargingRequest.setDriverId(sharedPrefsHelper.get(AppConstants.DRIVER_ID));
        chargingRequest.setChargingRequestId(sharedPrefsHelper.get(AppConstants.CHARGING_REQUEST_ID));

        iChargingRequest.generateOtpAndSendToDriver(ChargingStationDetail.this, chargingRequest);

    }

    public String makeOtp(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(otp_field_one.getText().toString());
        stringBuilder.append(otp_field_two.getText().toString());
        stringBuilder.append(otp_field_three.getText().toString());
        stringBuilder.append(otp_field_four.getText().toString());
        stringBuilder.append(otp_field_five.getText().toString());

        otp = stringBuilder.toString();
        return otp;
    }

    public void clearOtp(){
        otp_field_one.getText().clear();
        otp_field_two.getText().clear();
        otp_field_three.getText().clear();
        otp_field_four.getText().clear();
        otp_field_five.getText().clear();
        otp_field_one.requestFocus();
    }








}