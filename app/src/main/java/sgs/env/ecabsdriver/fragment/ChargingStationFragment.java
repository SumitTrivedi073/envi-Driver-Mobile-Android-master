package sgs.env.ecabsdriver.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.activity.OptionsActivity;

public class ChargingStationFragment extends BottomSheetDialogFragment {

    private ImageView directionToChargingStation;
    private TextView textView,typeOfChargingStation, vendorConnectorId_tv, connectorId_tv,  powerLevel_tv;
    private ProgressBar progressBar;
    private OptionsActivity optionsActivity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.charging_station_master, container, true);
        directionToChargingStation = view.findViewById(R.id.direction_to_charging_station);
        textView = view.findViewById(R.id.tv_charging_station);
        progressBar = view.findViewById(R.id.progress_bar_charging_station_dialog);
        typeOfChargingStation = view.findViewById(R.id.charging_station_title);
        vendorConnectorId_tv = view.findViewById(R.id.vendorConnectorId);
        connectorId_tv = view.findViewById(R.id.connectorId);
        powerLevel_tv = view.findViewById(R.id.power_level);
        directionToChargingStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionsActivity optionsActivity = (OptionsActivity)getActivity();
                optionsActivity.nav();
            }
        });

        return view;

    }

    public TextView getChargingStationFragmentErrorMsgTextView(){
        return  textView;
    }

    public ImageView getDirectionImage(){
        return directionToChargingStation;
    }

    public ProgressBar getProgressBar(){
        return progressBar;
    }


    @Override
    public void onResume() {
        super.onResume();
     //   optionsActivity = (OptionsActivity)getActivity();
   //     optionsActivity.chargingAPI();
    }

    public TextView getTypeOfChargingStation(){
        return typeOfChargingStation;
    }

    public TextView getVendorConnectorIdTv(){
        return vendorConnectorId_tv;
    }

    public TextView getConnectorIdTv(){
        return connectorId_tv;
    }

    public TextView getPowerLevelTv(){
        return powerLevel_tv;
    }


}
