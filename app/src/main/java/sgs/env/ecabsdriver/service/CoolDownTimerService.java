package sgs.env.ecabsdriver.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.activity.CoolDown;
import sgs.env.ecabsdriver.activity.WaitingForCustomerActivity;
import sgs.env.ecabsdriver.data.local.ECabsDriverDbHelper;
import sgs.env.ecabsdriver.interfce.UpdateDriverStatusI;
import sgs.env.ecabsdriver.model.CustomerNotfication;
import sgs.env.ecabsdriver.model.DriverStatus;
import sgs.env.ecabsdriver.model.Location1;
import sgs.env.ecabsdriver.presenter.UpdateDriverStatusPresenter;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class CoolDownTimerService extends Service {

    private final int TIMER_IN_MILLI_SECONDS = 900000;  //900000ms = 15min
    private final int COUNT_DOWN_INTERVAL = 1000;
    private int progressBarPercentage;
    private final String action = "charging.coolDown";
    private CountDownTimer countDownTimer;
    private String timeLeftFormatted;
    private ECabsDriverDbHelper dbHelper;
    private CustomerNotfication data;
    private final SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, CoolDown.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        new CountDownTimer(TIMER_IN_MILLI_SECONDS, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                int stepper = (int) TIMER_IN_MILLI_SECONDS / 100;

                progressBarPercentage = (int) millisUntilFinished / stepper;

                timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);

                Notification notification = new NotificationCompat.Builder(getApplicationContext(), ECabsApp.NOTIFICATION_CHANNEL_ID)
                        .setContentTitle("Timer")
                        .setContentText("Cool down timer - " + timeLeftFormatted)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentIntent(pendingIntent)
                        .build();

                startForeground(1, notification);

                Intent intent1 = new Intent(action);
                intent1.putExtra(AppConstants.COOL_DOWN_PROGRESS_BAR_PERCENTAGE, progressBarPercentage);
                intent1.putExtra(AppConstants.COOL_DOWN_TIMER, timeLeftFormatted);
                sendBroadcast(intent1);

            }

            @Override
            public void onFinish() {
                MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.ready_for_trips);
                mediaPlayer.start();
                dbHelper = new ECabsDriverDbHelper(getApplicationContext());
                data = dbHelper.getRideStatus();
                data.setRideStatus(AppConstants.FREE);
                dbHelper.updateRideStatus(data);
                DriverStatus driverStatus = new DriverStatus();
                Location1 location1 = new Location1();
                location1.setLatitude(sharedPrefsHelper.get(AppConstants.LAST_KNOW_LATITUDE));
                location1.setLongitide(sharedPrefsHelper.get(AppConstants.LAST_KNOWN_LONGITUDE));
                driverStatus.setDriverLocation(location1);
                driverStatus.setDriverTripMasterId(SharedPrefsHelper.getInstance().get(AppConstants.DRV_MASTER_ID, AppConstants.NO_TOKEN));
                driverStatus.setStatus(AppConstants.FREE);
                driverStatus.setDif(-1);
                UpdateDriverStatusI updateDriverStatusI = new UpdateDriverStatusPresenter();
                updateDriverStatusI.updateDriverStatusAPI(getApplicationContext(), driverStatus);
                Intent intent1 = new Intent(getApplicationContext(), WaitingForCustomerActivity.class);
                startActivity(intent1);
                stopSelf();
            }
        }.start();

        return START_STICKY;

    }
}
