package sgs.env.ecabsdriver.interfce;

import android.content.Context;

import sgs.env.ecabsdriver.activity.ChargingStationDetail;
import sgs.env.ecabsdriver.model.ChargingRequest;
import sgs.env.ecabsdriver.model.RequestBodyToStartCharging;

public interface IChargingRequest {
    void createChargingRequest(ChargingRequest chargingRequest);
    void generateOtpAndSendToDriver(Context context, ChargingRequest chargingRequest);
    void startCharging(Context context, RequestBodyToStartCharging requestBodyToStartCharging, ChargingStationDetail chargingStationDetail);
}
