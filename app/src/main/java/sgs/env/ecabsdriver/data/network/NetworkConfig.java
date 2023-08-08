package sgs.env.ecabsdriver.data.network;

import sgs.env.ecabsdriver.BuildConfig;

public class NetworkConfig {
	
	//
	public static final String BASE_URL = BuildConfig.BASE_URL;
	// public static final String BASE_URL = "http://192.168.1.3:3000/";
	
	public static final String DC_FAST_CHARGING_URL = BuildConfig.DC_FAST_CHARGING_URL;
	//public static final String DC_FAST_CHARGING_URL = "http://192.168.1.3:3000/";

	
	public static final String LOGIN_URL = "driverApp/login/driverLogin";

	public static final String AUTH_TOKEN = "x-access-token";

	public static final String CONTENT_TYPE = "Content-Type: application/json";
	
	public static final String REQ_BREAK = "driverApp/trip/takeBreak/v2";
	
	public static final String END_BREAK = "driverApp/trip/endBreak/v2";

	public static final String LOGOUT = "driverApp/driver/driverLogOut";
	
	public static final String TRIP_HISTORY = "driverApp/trip/getDriverTripHistory/{drvId}/{pageNumber}/{numberOfItems}";
	
	public static final String GET_PROFILE = "driverApp/driver/getProfile";
	
	public static final String START_DAY = "driverApp/trip/startTrip/v2";
	
	public static final String CLOSE_DAY = "driverApp/driver/close-day";
	
	public static final String NOTIFY_USER = "driverApp/trip/notifyToUser";

	public static final String RATE_API = "driverApp/trip/rateUserTrip";
	
	public static final String SOS_API = "driverApp/driver/sos";
	
	public static final String SEARCH_STATION = "driverApp/chargingStations/getNearBy";

	public static final String UpdateChargingStatus = "driverApp/driver/vehicleCharging";
	
	public static final String UPDATE_DRIVER_WAITING_FOR_TRIP = "driverApp/driver/waiting-for-trip";
	
	public static final String SEND_INVOICE = "driverApp/trip/sendInvoice";
	
	public static final String LOG = "driverApp/driver/log";
	
	public static final String CHECK_FOR_NEW_TRIP = "driverApp/trip/driverTrips/{driverTripMasterId}";
	
	public static final String CREATE_CHARGING_REQUEST = "driverApp/charging-requests/createChargingFRequest";
	
	public static final String GENERATE_OTP_FOR_CHARGING = "driverApp/sendOtpForDcFastCharging";
	
	public static final String START_CHARGING = "driverApp/remoteStartTransaction";
	
	public static final String UPDATE_PAYMENT_STATUS = "driverApp/trip/updatePaymentStatus";

	public static final String UPDATE_TOLL = "driverApp/trip/updateTollAmount";

	public static final String ADD_USER_DEVICE_INFO = "driverApp/driver/saveUserDeviceInfo";
	
	public static final String VERIFY_TRANSACTION_STATUS = "driverApp/driver/verifyPassengerPaymentStatus";

	public static final String UPDATE_TOKEN = "driverApp/driver/FcmTokenUpdate";

	public static final String GENERATE_QRCODE ="driverApp/trip/createQRCodeRequest";

	public static final String UPDATE_DRIVER_LOC = "drvLocation/updateLocation";

	public static final String GET_DRIVER_TRIP_STATUS = "drvLocation/getDriverTripStatus";

	public static final String UPDATE_DRIVER_STATUS = "driverApp/trip/updateTrip/v2";

}
