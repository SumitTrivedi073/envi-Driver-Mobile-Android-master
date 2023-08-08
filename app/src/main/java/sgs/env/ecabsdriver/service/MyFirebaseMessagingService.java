
package sgs.env.ecabsdriver.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.activity.BreakActivity;
import sgs.env.ecabsdriver.activity.HomeActivity;
import sgs.env.ecabsdriver.activity.MapActivity;
import sgs.env.ecabsdriver.activity.WaitingForCustomerActivity;
import sgs.env.ecabsdriver.data.local.ECabsDriverDbHelper;
import sgs.env.ecabsdriver.model.CustomerNotfication;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.MediaPlayerSingleton;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

import static androidx.lifecycle.ProcessLifecycleOwner.*;
import static sgs.env.ecabsdriver.util.ECabsApp.isNewTripScreenVisible;

public class MyFirebaseMessagingService extends FirebaseMessagingService implements LifecycleObserver {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String TRIP_STATUS = "trip_status";
    private static final String BODY = "body";
    public static final String INTENT_NEXT_TRIP = "nextTrip";
    public static final String INTENT_CANCEL_TRIP = "cancelTrip";
    private String type;
    private String bodyValue;
    private LocalBroadcastManager localBroadcastManager;
    private ECabsDriverDbHelper dbHelper;
    private Activity mCurrentActivity;
    private String title;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    public static boolean wasInBackground = false;

    
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("onNewToken",s);
        
    }
    
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        dbHelper = new ECabsDriverDbHelper(this);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        Log.d(TAG, "From: " + remoteMessage.getFrom());
    
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        
            Map<String, String> data = remoteMessage.getData();
            type = data.get("type");
            title = data.get("title");
            Log.d(TAG, "onMessageReceived: Type " + type);
            bodyValue = data.get(BODY);
            if (type != null) {
                if (type.contentEquals(TRIP_STATUS)) {
                    // cancel the status
                    sendNotification(remoteMessage);
                    String id = data.get("id");
                    CustomerNotfication dataNot = new CustomerNotfication();
                    dataNot.setUserId(id);
                    dataNot.setRideStatus("cancelled");
                    dataNot.setDriverPage("home page");
                    dbHelper.updateRideStatus(dataNot);
                
                    boolean inBackground = ECabsApp.isBackgroundRunning(this);
                    if (inBackground) {
                        /*   sendNotificationCancel("User canceled ur trip", "");*/
                        oreoNotification("User canceled ur trip", "next trip will be asssigned shortly", -1);
                    }
                    else {
                        Intent intentBroadCast = new Intent(INTENT_CANCEL_TRIP);
                        intentBroadCast.putExtra(AppConstants.CANCEL_TRIP, intentBroadCast);
                        localBroadcastManager.sendBroadcast(intentBroadCast);
                    }
                }
                else if (type.contentEquals("break_status")) {
                    if (title.contentEquals("your break is approved")) {
                        sendNotification(remoteMessage);
                        String breakId = data.get("breakId");
                        SharedPrefsHelper.getInstance().save(AppConstants.BREAK_ID, breakId);
                        /*sendNotification1("Ur Break confirmed");*/
                        oreoNotification("Break status", "Ur Break confirmed", 2);
                    }
                    else {
                        /*   sendNotificationRejected("Break rejected");*/
                        oreoNotification("Break status", "Ur Break rejected", -2);
                    }
                }
                else if (type.contentEquals("request")) {
                    if (SharedPrefsHelper.getInstance().get(AppConstants.ACCEPT,false)!=null
                            ||String.valueOf(SharedPrefsHelper.getInstance().get(AppConstants.ACCEPT,false)).equals("false")) {
                          if (!isNewTripScreenVisible()) {
                              MediaPlayerSingleton mediaPlayerSingleton = MediaPlayerSingleton.getInstance();
                              mediaPlayerSingleton.init(getApplicationContext());
                              MediaPlayer mediaPlayer = MediaPlayerSingleton.getSingletonMedia();
                              mediaPlayer.setLooping(true);
                              mediaPlayer.start();
                              //  sendNotification(remoteMessage);
                              bodyValue = data.get(BODY);
                              SharedPrefsHelper.getInstance().save(AppConstants.NOTIFICATION_TRIP, true);
                              Log.d(TAG, "trip booked");
                              Intent intent = new Intent(this, WaitingForCustomerActivity.class);
                              intent.setAction(Intent.ACTION_MAIN);
                              intent.addCategory(Intent.CATEGORY_LAUNCHER);
                              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                              startActivity(intent);
                              Log.d(TAG, "trip launched");
                          }
                    }
                }
            }
        }
    }
    
    
    public void sendNotification (RemoteMessage remoteMessage){
        Log.d("insideMethod", String.valueOf(wasInBackground));
        Log.d("insideBackGround","insideBackGround");
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            Log.d("insideSDKVersion","insideSDKVersion");
            
            @SuppressLint("WrongConstant")
            final int flag =  Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,"tripNotification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("my notification test");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(R.color.red);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);
            notificationBuilder.setPriority(NotificationManager.IMPORTANCE_HIGH);
            Intent intent = new Intent(this, WaitingForCustomerActivity .class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,1, intent,flag);
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.profile_logd)
                    .setContentTitle(remoteMessage.getData().get("title"))
                    .setContentText(remoteMessage.getData().get("body"))
                    .setContentIntent(pendingIntent);
            notificationManager.notify(1,notificationBuilder.build());
        }
    }
    
    public void oreoNotification(String title, String message, int type) {   // type 1 : book, -1 : cancel, 2 : breakApproved, -2:breakRejected.
        NotificationManager mNotificationManager;
        NotificationCompat.Builder mBuilder = null;
        Intent resultIntent;
        PendingIntent resultPendingIntent;
        /**Creates an explicit intent for an Activity in your app**/
        switch (type) {
            case 1 :
                resultIntent = new Intent(this , MapActivity.class);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    resultPendingIntent = PendingIntent.getActivity(this,
                            0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                }else {
                    resultPendingIntent = PendingIntent.getActivity(this,
                            0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                }
                mBuilder = new NotificationCompat.Builder(this);
                mBuilder.setSmallIcon( R.drawable.dot1);
                mBuilder.setContentTitle(title +"Booked your cab")
                        .setContentText(message)
                        .setAutoCancel(false)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(resultPendingIntent);
                break;
            
            case 2 :
                resultIntent = new Intent(this , BreakActivity.class);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    resultPendingIntent = PendingIntent.getActivity(this,
                            0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                }else {
                    resultPendingIntent = PendingIntent.getActivity(this,
                            0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                }
                mBuilder = new NotificationCompat.Builder(this);
                mBuilder.setSmallIcon(R.drawable.dot1);
                mBuilder.setContentTitle(title )
                        .setContentText(message)
                        .setAutoCancel(false)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(resultPendingIntent);
                break;
            
            case -2 :
            case -1 :
                resultIntent = new Intent(this , HomeActivity.class);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    resultPendingIntent = PendingIntent.getActivity(this,
                            0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                }else {
                    resultPendingIntent = PendingIntent.getActivity(this,
                            0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                }
                mBuilder = new NotificationCompat.Builder(this);
                mBuilder.setSmallIcon(R.drawable.dot1);
                mBuilder.setContentTitle(title )
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(resultPendingIntent);
                break;
        }
        
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            if(mBuilder != null) {
                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            }
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
    }

    
}
