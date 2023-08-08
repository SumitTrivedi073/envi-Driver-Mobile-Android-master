package sgs.env.ecabsdriver.util;

/**
 * Created by Lenovo on 4/10/2018.
 */

public class AppConstants {

    public static final String TIME_BELOW_10_SEC = "timeRemaining" ;     // counting the time using count timer
    public static final String TIMEUP = "timeUP";
    public static final String START_FORGROUND_SERVICE = "startFService";
    public static final String STOP_FORGROUND_SERVICE = "stopFService";
    public static final String CUSTOMER_PICKED = "customerPicked";
    public static final String NOTIFICATION_TRIP = "notificationTrip";
    public static final String NO_TOKEN = "no";   // for token validation
    public static final String GEN_MASTER_ID = "masterId";
    public static final String DRV_MASTER_ID = "mstId";
    public static final String PASS_MST_ID = "passMstId";
    public static final String CGST = "tax";
    public static final String SGST = "sgst";
    public static final String PER_KM = "perKm";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "logi";
    public static final String POINTS = "Point";
    public static final String DRV_VEH_ID = "vehicleId";
    public static final String ACCEPT = "accept";  // driver accepted customer request
    public static final String PAYMENT_MODE="paymentMode";
    public static final String ADVANCEPAID = "ADVANCEPAID";
    public static final String CUSTOMER_MAIL_ID="customerMailId";
    public static final String MINUTE_PRICE="minutePrice";
    // break types and break count
    public static final String TEA_BREAK = "tea";
    public static final String LUNCH_BREAK = "lunch";
    public static final String TOTAL_BREAK_COUNT = "breakCount";

    public static final String TRIP_END = "tripEnd";
    public static final String KEY_JWT_TOKEN = "" ;
    public static final String TRIP_ID ="tripID" ;
    public static final String DISTANCE = "distance";
    public static final String AMOUNT = "amount";
    public static final String DRIVER_LOGIN = "driverLogin";
    public static final String BREAK_NAME = "breakName";
    public static final String DRIVER_NAME = "driverNme";
    public static final String DRIVER_VEH_NO = "vehcileNO";
    public static final String DRIVER_ID = "id" ;
    public static final String MOBILE_NUM = "mobileNum" ;
    public static final String BREAK_ID = "breakID";
    public static final String USER_ID = "userId";
    public static final String CANCEL_TRIP = "cancelTrip";
    public static final String BOOKIN_ID = "bookingId";
    public static final String IN_BREAK = "inBreak";
    public static final String PIC = "pic";
    public static final String ACTIVE = "active";  // true : loged_in  , false : loged_out.
    public static final String SOC = "soc";
    public static final String YET_START = "Yet to start";
    public static final String TRIP_STARTED = "trip started";
    public static final String TRIP_ENDED = "trip Ended";
    public static final String STATION_GO = "waytochargingstation";
    public static final String START_CHARGE = "charging";
    //for stop charge when driver is stop charge or stop
    // break we are getting status as free
    public static final String STOP_CHARGE = "free";
    public static final String STATUS = "statusType";
    public static final String FREE = "free";
    public static final String CANCEL_TIMER = "cancelTimer";
    public static final String ARRIVED = "arrived";
    public static final String OFFWAIT = "offWait";
    public static final String DRV_MODEL = "veh_model";
    public static final String COUNTRY = "country";
    public static final String STATE = "STATE";
    public static final String CITY = "city";
    public static final String PINCODE = "pincode";
    public static final String ADDRESS = "address";
    public static final String SENT_TO_CHARGE = "sentToCharge";
    public static final String LATEST_SOC = "latestSOC";
    public static final String MINIMUM_CHARGE = "minimumCharge";
    public static final String TOTAL_FARE = "totalFare";
    public static final String DISCOUNTED_PRICE = "discountedPrice";
    public static final String END_TRIP_BUTTON_CLICKED = "endTripButtonClicked";
    public static final  String REQUEST = "request";
    public static final  String AmountTobeCollected = "AmountTobeCollected";
    public static final  String DISCOUNT = "discount";
    public static final String PER_KM_PRICE = "perKMPrice";
    public static final String FROM_ADDRESS = "fromAddress";
    public static final String TO_ADDRESS = "toAddress";
    public static final String TAXABLE_AMOUNT = "taxableAmount";
    public static final String DRIVER_PHOTO = "driverPhoto";
    public static final String DRIVER_FIRST_NAME = "driverName";
    public static final String TRIP_START_TIME = "tripStartTime";
    public static final String TRIP_END_TIME = "tripEndTime";
    public static final String DIFFERENCE_IN_TRIP_TIME = "differenceInTripTime";
    public static final String OPTIONS_ACTIVITY = "station.Navigation";
    public static final String START_CHARGING_ACTIVITY = "station.Start_Charging";
    public static final String CHARGING_REQUEST_ID = "chargingRequestId";
    public static final String CHARGING_STATION_JSON = "chargingStationJson";
    public static final String CONNECTOR_ID = "connectorId";
    public static final String CHARGING_COMPLETED = "CHARGING_COMPLETED";
    public static final String LAST_KNOW_LATITUDE = "latestLatitude";
    public static final String LAST_KNOWN_LONGITUDE = "latestLongitude";
    public static final String COOL_DOWN_PROGRESS_BAR_PERCENTAGE="coolDownProgressBarPercentage";
    public static final String COOL_DOWN_TIMER = "coolDownTimer";
    public static final String COOL_DOWN_TIMER_FINISHED="coolDownTimerFinished";
    public static final String CHARGING_IN_PROGRESS = "CHARGING_IN_PROGRESS";
    public static final String FROM_ACTIVITY="fromActivity";
    public static final String COLLECT_CASH_ACTIVITY="collectCashActivity";
    public static final String CASH="cash";
    public static final String ONLINE = "online";
    public static final String QR_CODE = "qr_code";
    public static final String PAYMENT_COMPLETED = "PAYMENT_COMPLETED";
    public static final String TRIP_RECEIVED_SOUND_PLAYING = "tripReceivedSoundPlaying";
    public static final String TOLL_CHARGES="tollCharges";
    public static final String TRIP_COMPLETED="completed";
    public static final String TRIP_ONBOARDING="onboarding";
    public static final String TRIP_ALLOTTED="allotted";
    public static final String TRIP_REQUEST="request";
    public static final String NO_PREV_TRIPS_AVAILABLE = "noPrevTripsAvailable";
    public static final String TRIP_STATUS_ONLINE_FAILURE = "PAYMENT_FAILED";
    public static final String TRIP_STATUS_ONLINE_PENDING = "PAYMENT_PENDING";
    public static final String PAYMENT_INITIATED = "PAYMENT_INITIATED";
    public static final String NOT_PAID  = "NOT_PAID";
    public static final String TRIP_STATUS_TOKEN_GENERATED = "TOKEN_GENERATED";
    public static final String NOT_LOGGED_IN = "notLoggedIn";
    public static final String CANCELLED = "cancelled" ;
    public static final String DRIVER_CANCELLED = "driverCancelled" ;
    public static final String searchFrequency = "searchFrequency" ;
    public static final String seacrhMinDistance = "seacrhMinDistance" ;
    public static final String MinSoc = "MinSoc";
    public static final String ChargingStatus = "ChargingStatus";
    public static final String FirstLogin = "FirstLogin";
    public static final String QRCODE_DETAIL = "QRCode_Detail";
    public static String logoutFromFirebase = "logoutFromFirebase";
    public static  String messageType = "messageType";
    public static  String allMessageList = "allMessageList";
    public static  String broadcastAllMessageList = "broadcastAllMessageList";
    public static  String InfoBroadcast = "InfoBroadcast";
    public static  String AckBroadcast = "AckBroadcast";
    public static  String ResBroadcast = "ResBroadcast";
    public static  String scheduleTripInfo = "scheduleTripInfo";
    public static String MessageTypeText  ="Text";
    public static String MessageTypeImage  ="Image";
    public static String MessageTypeAudio  ="Audio";
    public static String MessageTypeVideo  ="Video";
    public static String TripScheduledAt = "TripScheduledAt";
    public static String IsScheduleTrip = "IsScheduleTrip";
    public static String SourceArrivalTime = "SourceArrivalTime";
    public static String SpecialRemraks = "SpecialRemraks";
    public static String IsWait = "IsWait";
    public static String IsWaitTimerComplete = "IsWaitTimerComplete";
    public static String TripStatus = "TripStatus";
    public static String SwVersionConfig = "SwVersionConfig";
    public static String AppURL = "AppURL";
    public static String TripData = "TripData";
    public static String TripCancel = "TripCancel";


}
