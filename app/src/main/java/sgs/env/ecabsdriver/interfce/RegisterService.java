package sgs.env.ecabsdriver.interfce;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import sgs.env.ecabsdriver.data.network.NetworkConfig;
import sgs.env.ecabsdriver.model.BreakObj;
import sgs.env.ecabsdriver.model.BreakResoponse;
import sgs.env.ecabsdriver.model.ChargingRequest;
import sgs.env.ecabsdriver.model.ChargingStationModel;
import sgs.env.ecabsdriver.model.CloseDay;
import sgs.env.ecabsdriver.model.CustomerNotfication;
import sgs.env.ecabsdriver.model.DriverMstIDRes;
import sgs.env.ecabsdriver.model.DriverStatus;
import sgs.env.ecabsdriver.model.DriverWaitingForTrip;
import sgs.env.ecabsdriver.model.EndTrip;
import sgs.env.ecabsdriver.model.EndTripResponse;
import sgs.env.ecabsdriver.model.GeneralResponse;
import sgs.env.ecabsdriver.model.InfoModelB;
import sgs.env.ecabsdriver.model.LocResponse;
import sgs.env.ecabsdriver.model.Log;
import sgs.env.ecabsdriver.model.Login;
import sgs.env.ecabsdriver.model.LoginResponse;
import sgs.env.ecabsdriver.model.PassengerTripMaster;
import sgs.env.ecabsdriver.model.PaymentVerificationResponse;
import sgs.env.ecabsdriver.model.ProfileResponse;
import sgs.env.ecabsdriver.model.QRCodeModel;
import sgs.env.ecabsdriver.model.RateTrip;
import sgs.env.ecabsdriver.model.RequestBodyToStartCharging;
import sgs.env.ecabsdriver.model.SearchBody;
import sgs.env.ecabsdriver.model.Sos;
import sgs.env.ecabsdriver.model.StationResponse;
import sgs.env.ecabsdriver.model.TollAmount;
import sgs.env.ecabsdriver.model.TripEndResponse;
import sgs.env.ecabsdriver.model.TripHistoryResponse;
import sgs.env.ecabsdriver.model.TripInvoice;
import sgs.env.ecabsdriver.model.UpdateLoc;
import sgs.env.ecabsdriver.model.UpdatePaymentStatus;
import sgs.env.ecabsdriver.model.UserDeviceInfoModel;
import sgs.env.ecabsdriver.model.ValidateDriverResp;
import sgs.env.ecabsdriver.model.PaytmModel;

public interface RegisterService {

    @Headers(NetworkConfig.CONTENT_TYPE)
    @POST(NetworkConfig.LOGIN_URL)
    Call<LoginResponse> login(@Body Login login);

    @Headers(NetworkConfig.CONTENT_TYPE)
    @POST(NetworkConfig.UPDATE_DRIVER_LOC)
    Call<LocResponse> updateLocation(@Header(NetworkConfig.AUTH_TOKEN) String token , @Body UpdateLoc location1);

    @Headers(NetworkConfig.CONTENT_TYPE)
    @POST(NetworkConfig.REQ_BREAK)
    Call<BreakResoponse> reqBreak(@Header(NetworkConfig.AUTH_TOKEN) String token, @Body BreakObj breakObj);

    @Headers(NetworkConfig.CONTENT_TYPE)
    @POST(NetworkConfig.END_BREAK)
    Call<GeneralResponse> endBreak(@Header(NetworkConfig.AUTH_TOKEN) String token, @Body BreakObj breakObj);

    @GET(NetworkConfig.LOGOUT)
    Call<GeneralResponse> logout(@Header(NetworkConfig.AUTH_TOKEN) String token );

     @GET(NetworkConfig.GET_PROFILE)
    Call<ProfileResponse> getProfile(@Header(NetworkConfig.AUTH_TOKEN) String token );

    @Headers(NetworkConfig.CONTENT_TYPE)
    @POST(NetworkConfig.SEARCH_STATION)
    Call<StationResponse> searchStation(@Header(NetworkConfig.AUTH_TOKEN) String token, @Body SearchBody location);

    @Headers(NetworkConfig.CONTENT_TYPE)
    @POST(NetworkConfig.UpdateChargingStatus)
    Call<GeneralResponse> UpdateChargingStatus(@Header(NetworkConfig.AUTH_TOKEN) String token, @Body ChargingStationModel chargingStationModel);

    @Headers(NetworkConfig.CONTENT_TYPE)
     @POST(NetworkConfig.START_DAY)
    Call<DriverMstIDRes> sendInfo(@Header(NetworkConfig.AUTH_TOKEN) String token, @Body InfoModelB infoModelB);

    @Headers(NetworkConfig.CONTENT_TYPE)
     @POST(NetworkConfig.CLOSE_DAY)
    Call<GeneralResponse> closeDay(@Header(NetworkConfig.AUTH_TOKEN) String token, @Body CloseDay loc);

    @Headers(NetworkConfig.CONTENT_TYPE)
    @POST(NetworkConfig.UPDATE_DRIVER_STATUS)
    Call<TripEndResponse> update(@Header(NetworkConfig.AUTH_TOKEN) String token, @Body DriverStatus status);

    @Headers(NetworkConfig.CONTENT_TYPE)
    @POST(NetworkConfig.NOTIFY_USER)
    Call<GeneralResponse> notifyUser(@Header(NetworkConfig.AUTH_TOKEN) String token, @Body DriverStatus loc);

    @Headers(NetworkConfig.CONTENT_TYPE)
    @POST(NetworkConfig.RATE_API)
    Call<GeneralResponse> rateTrip(@Header(NetworkConfig.AUTH_TOKEN) String token, @Body RateTrip rate);

    @Headers(NetworkConfig.CONTENT_TYPE)
    @POST(NetworkConfig.SOS_API)
    Call<GeneralResponse> sosFun(@Header(NetworkConfig.AUTH_TOKEN) String token, @Body Sos sos);

    @GET(NetworkConfig.TRIP_HISTORY)
    Call<TripHistoryResponse> getTripHistory(@Header(NetworkConfig.AUTH_TOKEN) String token, @Path("drvId") String drvId, @Path("pageNumber") int pageNumber, @Path("numberOfItems") int numberOfItems);

    @Headers(NetworkConfig.CONTENT_TYPE)
    @POST(NetworkConfig.UPDATE_DRIVER_WAITING_FOR_TRIP)
    Call<GeneralResponse> updateDriverWaitingForTrip(@Header(NetworkConfig.AUTH_TOKEN) String token, @Body DriverWaitingForTrip driverWaitingForTrip);

    @Headers(NetworkConfig.CONTENT_TYPE)
    @POST(NetworkConfig.SEND_INVOICE)
    Call<GeneralResponse> sendInvoiceOfTheTrip(@Header(NetworkConfig.AUTH_TOKEN) String token, @Body TripInvoice tripInvoice);

    @Headers(NetworkConfig.CONTENT_TYPE)
    @POST(NetworkConfig.LOG)
    Call<Log> log(@Header(NetworkConfig.AUTH_TOKEN)String token, @Body Log logData);

    @GET(NetworkConfig.CHECK_FOR_NEW_TRIP)
    Call<CustomerNotfication> checkForNewTrip(@Header((NetworkConfig.AUTH_TOKEN))String token, @Path("driverTripMasterId")String driverTripMasterId);

    @Headers(NetworkConfig.CONTENT_TYPE)
    @POST(NetworkConfig.CREATE_CHARGING_REQUEST)
    Call<ChargingRequest> createChargingRequest(@Header(NetworkConfig.AUTH_TOKEN)String token, @Body ChargingRequest chargingRequest);

    @Headers(NetworkConfig.CONTENT_TYPE)
    @POST(NetworkConfig.GENERATE_OTP_FOR_CHARGING)
    Call<ChargingRequest> generateOtpForCharging(@Header(NetworkConfig.AUTH_TOKEN)String token, @Body ChargingRequest chargingRequest);

    @Headers(NetworkConfig.CONTENT_TYPE)
    @POST(NetworkConfig.START_CHARGING)
    Call<RequestBodyToStartCharging> startCharging(@Header (NetworkConfig.AUTH_TOKEN) String token,
                                                   @Body RequestBodyToStartCharging requestBodyToStartCharging);

    @Headers(NetworkConfig.CONTENT_TYPE)
    @POST (NetworkConfig.UPDATE_PAYMENT_STATUS)
    Call<GeneralResponse> UpdatePaymentStatus(@Header (NetworkConfig.AUTH_TOKEN) String token,
                                              @Body UpdatePaymentStatus passengerTripMasterId);

    @Headers(NetworkConfig.CONTENT_TYPE)
    @POST (NetworkConfig.UPDATE_TOLL)
    Call<TollAmount> updateToll(@Header (NetworkConfig.AUTH_TOKEN) String token,
                                @Body TollAmount tollAmount);

    @Headers(NetworkConfig.CONTENT_TYPE)
    @POST (NetworkConfig.GET_DRIVER_TRIP_STATUS)
    Call<ValidateDriverResp> getDriverTripStatus(@Header (NetworkConfig.AUTH_TOKEN) String token);

    @Headers(NetworkConfig.CONTENT_TYPE)
    @POST (NetworkConfig.ADD_USER_DEVICE_INFO)
    Call<UserDeviceInfoModel> saveUserDeviceInfo(@Header (NetworkConfig.AUTH_TOKEN) String token,
                                                 @Body UserDeviceInfoModel userDeviceInfoModel);

    @Headers(NetworkConfig.CONTENT_TYPE)
    @POST (NetworkConfig.VERIFY_TRANSACTION_STATUS)
    Call<PaymentVerificationResponse> verifyTransaction(
            @Header (NetworkConfig.AUTH_TOKEN) String token, @Body PaytmModel paytmModel);

    @Headers(NetworkConfig.CONTENT_TYPE)
    @POST(NetworkConfig.UPDATE_TOKEN)
    Call<GeneralResponse> updateToken(@Header(NetworkConfig.AUTH_TOKEN) String token, @Body Login login);

    @Headers(NetworkConfig.CONTENT_TYPE)
    @POST (NetworkConfig.GENERATE_QRCODE)
    Call<QRCodeModel> generateQRCode(
            @Header (NetworkConfig.AUTH_TOKEN) String token, @Body PaytmModel paytmModel);

}
