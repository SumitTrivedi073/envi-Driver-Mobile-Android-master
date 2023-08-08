package sgs.env.ecabsdriver.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import sgs.env.ecabsdriver.R;

public class ChargeCompleteFragment extends DialogFragment {

    private Button button;
    private TextView timer;
    private CountDownTimer countDownTimer;
    private MediaPlayer mediaPlayer;
    private final int TIME_IN_MS = 10000; // 10s
    private final int TIMER_INTERVAL = 1000; // stepper is 1s

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.charging_complete_dialog, container, true);
        button = view.findViewById(R.id.charge_complete_dialog_button);
        timer = view.findViewById(R.id.timer);

        mediaPlayer = MediaPlayer.create(view.getContext(), R.raw.charging_complete_audio);
        mediaPlayer.start();

        countDownTimer = new CountDownTimer(TIME_IN_MS, TIMER_INTERVAL){
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText(millisUntilFinished/1000 + "");

            }

            @Override
            public void onFinish() {
                if(getDialog() != null) {
                    getDialog().dismiss();
                }
            }
        }.start();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                getDialog().dismiss();
            }
        });

        return view;


    }
}
