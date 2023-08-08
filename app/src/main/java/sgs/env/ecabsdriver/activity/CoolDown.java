package sgs.env.ecabsdriver.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.data.local.ECabsDriverDbHelper;
import sgs.env.ecabsdriver.model.CustomerNotfication;
import sgs.env.ecabsdriver.service.CoolDownTimerService;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.Internet;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class CoolDown extends AppCompatActivity {

    private TextView timer,carModel;
    private ProgressBar progressBar;
    private int progressBarPercentage;
    private final SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
    private final String intentAction = "charging.coolDown";
    private Intent serviceIntent;
    private ECabsDriverDbHelper dbHelper;
    private CustomerNotfication data;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cool_down);
        timer = findViewById(R.id.cool_down_timer);
        progressBar = findViewById(R.id.cool_down_progressBar);
        carModel = findViewById(R.id.car_model);
        carModel.setText(sharedPrefsHelper.get(AppConstants.DRV_MODEL));
        if(!Internet.isMyServiceRunning(CoolDownTimerService.class, CoolDown.this)) {
            serviceIntent = new Intent(this, CoolDownTimerService.class);
            startService(serviceIntent);
        }

    }

    private void updateUIComponents(Intent intent){
        int progressBarPercenteage = intent.getIntExtra(AppConstants.COOL_DOWN_PROGRESS_BAR_PERCENTAGE, 0);
        String timeLeft = intent.getExtras().getString(AppConstants.COOL_DOWN_TIMER);
        progressBar.setProgress(progressBarPercenteage);
        timer.setText(timeLeft);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUIComponents(intent);

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(intentAction));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}