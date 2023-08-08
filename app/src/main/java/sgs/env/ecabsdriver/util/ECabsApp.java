package sgs.env.ecabsdriver.util;

import android.app.ActivityManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.multidex.MultiDex;
import org.json.JSONObject;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import sgs.env.ecabsdriver.activity.HomeActivity;
import sgs.env.ecabsdriver.data.network.NetworkConfig;

public class ECabsApp extends Application {

    private static final String TAG = "ECabs";
    private static ECabsApp mInstance;
    private static Retrofit mRetrofit;
    private final static long CACHE_SIZE = 10 * 1024 * 1024; // 10MB Cache size
    public static final String NOTIFICATION_CHANNEL_ID = "TIMER";
    private static boolean newTripScreenVisible;
    private static boolean homeScreenVisible;
    private static boolean broadcastChatScreenVisible;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        MultiDex.install(this);
        initializeRetrofit();
        createNotificationChannel();


    /*    // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationOpenedHandler(new NotificationOpenedHandler())
                .setNotificationReceivedHandler(new NotificationReceivedHandler())
                .init();*/


    }

    public static boolean isNewTripScreenVisible() {
        return newTripScreenVisible;
    }

    public static void NewTripScreenResumed() {
        newTripScreenVisible = true;
    }

    public static void NewTripScreenPaused() {
        newTripScreenVisible = false;
    }

    public static boolean isHomeActivityVisible() {
        return homeScreenVisible;
    }

    public static void HomeActivityResumed() {
        homeScreenVisible = true;
    }

    public static void HomeActivityPaused() {
        homeScreenVisible = false;
    }

    public static boolean isBroadcastChatScreenVisible() {
        return broadcastChatScreenVisible;
    }

    public static void BroadcastChatScreenResumed() {
        broadcastChatScreenVisible = true;
    }
    public static void BroadcastChatScreenPaused() {
        broadcastChatScreenVisible = false;
    }



    public static synchronized ECabsApp getInstance() {
        return mInstance;
    }

    private static Retrofit initializeRetrofit() {
       /* Gson gson = new GsonBuilder()
                .setLenient()
                .create();*/

        final Interceptor cacheControlInterceptor = chain -> {
            Response originalResponse = chain.proceed(chain.request());
            if (isNetworkAvailable()) {
                int maxSec = 60; // read from cache for 1 minute
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxSec)
                        .build();
            }
            else {
                int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        };

        // Create Cache
        Cache cache = new Cache(mInstance.getCacheDir(), CACHE_SIZE);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .callTimeout(5,TimeUnit.MINUTES)
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(NetworkConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(okHttpClient)
                .build();

        return mRetrofit;
    }

    public static Retrofit getRetrofit() {

        if (mRetrofit != null) {
            return mRetrofit;
        } else {
            return initializeRetrofit();
        }
    }

    // logic for checking wheter the app is in background or foreground
    public static boolean isBackgroundRunning(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(context.getPackageName())) {
                        //If your app is the process in foreground, then it's not in running in background
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean isNetworkAvailable(){
        final ConnectivityManager connectivityManager = ((ConnectivityManager)
                mInstance.getSystemService(Context.CONNECTIVITY_SERVICE));
        assert connectivityManager != null;
        return connectivityManager.getActiveNetworkInfo() != null &&
                connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE));
        assert connectivityManager != null;
        return connectivityManager.getActiveNetworkInfo() != null &&
                connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static void noInternet(Context context, String message) {
        Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show();
    }

    public String getAcssToken() {
        return SharedPrefsHelper.getInstance().get(AppConstants.KEY_JWT_TOKEN, AppConstants.NO_TOKEN);
    }

   /* public class NotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
        @Override
        public void notificationReceived(OSNotification notification) {
            try {
                JSONObject data = notification.payload.additionalData;
                String notificationID = notification.payload.notificationID;
                String title = notification.payload.title;
                String body = notification.payload.body;
                String smallIcon = notification.payload.smallIcon;
                String largeIcon = notification.payload.largeIcon;
                String bigPicture = notification.payload.bigPicture;
                String smallIconAccentColor = notification.payload.smallIconAccentColor;
                String sound = notification.payload.sound;
                String ledColor = notification.payload.ledColor;
                int lockScreenVisibility = notification.payload.lockScreenVisibility;
                String groupKey = notification.payload.groupKey;
                String groupMessage = notification.payload.groupMessage;
                String fromProjectNumber = notification.payload.fromProjectNumber;
                String rawPayload = notification.payload.rawPayload;
                Log.d(TAG, "notificationReceived: notificationReiv is " + notification);

                Intent notificationIntent = new Intent(ECabsApp.this, HomeActivity.class);
                PendingIntent pi = PendingIntent.getActivity(ECabsApp.this, 0, notificationIntent, 0);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class NotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {

            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String launchUrl = result.notification.payload.launchURL;

            Intent intent = new Intent(ECabsApp.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
*/
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Cool Down Timer", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
