package sgs.env.ecabsdriver.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;

import android.util.Log;
import android.widget.Toast;



import java.util.Timer;
import java.util.TimerTask;

import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.activity.BreakActivity;
import sgs.env.ecabsdriver.util.AppConstants;


public class BreakTimerService extends Service {

    private final static String TAG = "TimerService";
    private static final int NOTIFICATION_ID = 101;

    public static final String COUNTDOWN_BR = "com.android.ServiceStopped";
    private final Intent intentBroadCast = new Intent(COUNTDOWN_BR);
    private CountDownTimer downTimer = null;
    private long remainingTime;
    private boolean clockFinished = false;
    private MediaPlayer mediaPlayer;
    private Timer timer;
    private TimerTask timerTask;
    private int counter;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getStringExtra(AppConstants.BREAK_NAME)!=null &&
        !intent.getStringExtra(AppConstants.BREAK_NAME).isEmpty()) {
            String breakNme = intent.getStringExtra(AppConstants.BREAK_NAME);

            switch (breakNme) {
                    case "tea":
                        startTimer(intent, 900);
                        break;

                    case "lunch":
                        startTimer(intent, 2700);
                        break;

                    case "wait1":
                        startTimer(intent, 360);
                        break;

                    case "wait2":
                        startTimer(intent, 120);
                        break;

                    case "0":
                        startTimer(intent, 0);
                        break;

                    case "5":
                        startTimer(intent, 300);
                        break;

                    case "10":
                        startTimer(intent, 600);
                        break;

                }
            } else {
                startTimer(intent, 900);
            }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void startBreakTime(long brkTime) {
        remainingTime = brkTime * 1000;

        downTimer = new CountDownTimer(remainingTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                long remainedSecs = millisUntilFinished / 1000;
                long secondsTick = remainedSecs % 60;

                String below10Sec;
                if (secondsTick < 10) {
                    //send this to activity... either way....
                    below10Sec = (remainedSecs / 60) + ":0" + (remainedSecs % 60);
                    intentBroadCast.putExtra(AppConstants.TIME_BELOW_10_SEC, below10Sec);
                }
                else {
                    below10Sec = " " + (remainedSecs / 60) + ":" + (remainedSecs % 60);
                    intentBroadCast.putExtra(AppConstants.TIME_BELOW_10_SEC, below10Sec);
                }

        /*        Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                intentBroadCast.putExtra("countdown", millisUntilFinished);*/
                // after finishing the time send it to activity using broadCastReciver and send the remaining time .....
                sendBroadcast(intentBroadCast);
            }

            @Override
            public void onFinish() {
                clockFinished = true;
                int timeUp = 0;
                intentBroadCast.putExtra(AppConstants.TIMEUP, timeUp);
                sendBroadcast(intentBroadCast);
                triggerAlarm();
                //set a new Timer
                timer = new Timer();
                initializeTimerTask();
                //schedule the timer, to wake up every 1 second
                timer.schedule(timerTask, 1000, 15 * 1000);
                Log.i(TAG, "Timer finished");
            }
        };
        downTimer.start();
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.d(TAG, "counter " + counter);
                Log.i(TAG, "in timer ++++  " + (counter++));
            }
        };
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void startTimer(Intent intent,long brkTime) {
        Log.d(TAG, "intentAction " + intent.getAction());
        String intentActin = intent.getAction();

        if (intentActin != null && intent.getAction().equals(AppConstants.START_FORGROUND_SERVICE)) {
            if(!clockFinished && brkTime != 0) {
                    startBreakTime(brkTime);
            }

            NotificationManager mNotificationManager;
            NotificationCompat.Builder mBuilder = null;
            Intent resultIntent;
            PendingIntent resultPendingIntent;

            mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            resultIntent = new Intent(this , BreakActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            resultPendingIntent = PendingIntent.getActivity(this,
                    0 , resultIntent,
                    /*PendingIntent.FLAG_UPDATE_CURRENT*/0);
            mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.drawable.dot1);
            mBuilder.setContentTitle("Break Notification")
                    .setContentText("Ecabs")
                    .setAutoCancel(false)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setContentIntent(resultPendingIntent);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            {
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
       /*     mNotificationManager.notify(0 *//* Request Code *//*, mBuilder.build());*/
            startForeground(NOTIFICATION_ID,
                    mBuilder.build());

      /*      startForeground(NOTIFICATION_ID,
                    notification);*/
        }

        else if (intentActin != null && intent.getAction().equals(
                AppConstants.STOP_FORGROUND_SERVICE)) {
            stoptimertask();
            stopAlarmManager();
            Log.i(TAG, "Received Stop Foreground Intent");
            stopForeground(true);
            stopSelf();

            if(downTimer != null) {
                downTimer.cancel();
            }
        }
    }

    public void triggerAlarm() {
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound);
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }
        catch (Exception e) {
            Log.d(TAG, "triggerAlarm: exception " + e.getMessage());
        }
    }

    public void stopAlarmManager() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release(); }
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(TAG, "onTaskRemoved: " + rootIntent);
    }
}

