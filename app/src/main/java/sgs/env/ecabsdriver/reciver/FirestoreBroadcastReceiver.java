package sgs.env.ecabsdriver.reciver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

import sgs.env.ecabsdriver.model.AppConfig;
import sgs.env.ecabsdriver.model.FirestoreBroadcastChatModel;
import sgs.env.ecabsdriver.model.FirestoreChatModel;
import sgs.env.ecabsdriver.model.TripDataModel;
import sgs.env.ecabsdriver.model.TripInfo;
import sgs.env.ecabsdriver.util.AppConstants;

public class FirestoreBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = "MyReceiver";

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive called");

        FirestoreChatModel firestoreChatModels = intent.getParcelableExtra(AppConstants.allMessageList);
        FirestoreBroadcastChatModel broadcastAllMessageList = intent.getParcelableExtra(AppConstants.broadcastAllMessageList);
        ArrayList<TripInfo> scheduleTripInfos = intent.getParcelableArrayListExtra(AppConstants.scheduleTripInfo);

        if (firestoreChatModels!=null ) {
            Intent i = new Intent(AppConstants.allMessageList);
            // Data you need to pass to activity
            i.putExtra(AppConstants.allMessageList, firestoreChatModels);
            context.sendBroadcast(i);
        }

        if (broadcastAllMessageList!=null){
            if (intent.getStringExtra(AppConstants.messageType).equals("Information")){
                Intent i = new Intent(AppConstants.InfoBroadcast);
                // Data you need to pass to activity
                i.putExtra(AppConstants.broadcastAllMessageList, broadcastAllMessageList);
                i.putExtra(AppConstants.messageType, intent.getStringExtra(AppConstants.messageType));
                context.sendBroadcast(i);
            }

            if (intent.getStringExtra(AppConstants.messageType).equals("Acknowledgment")){
                Intent i = new Intent(AppConstants.AckBroadcast);
                // Data you need to pass to activity
                i.putExtra(AppConstants.broadcastAllMessageList, broadcastAllMessageList);
                i.putExtra(AppConstants.messageType, intent.getStringExtra(AppConstants.messageType));
                context.sendBroadcast(i);
            }

            if (intent.getStringExtra(AppConstants.messageType).equals("Response")) {
                Intent i = new Intent(AppConstants.ResBroadcast);
                // Data you need to pass to activity
                i.putExtra(AppConstants.broadcastAllMessageList, broadcastAllMessageList);
                i.putExtra(AppConstants.messageType, intent.getStringExtra(AppConstants.messageType));
                context.sendBroadcast(i);
            }

        }

        if (intent.getStringExtra(AppConstants.SwVersionConfig)!=null && !intent.getStringExtra(AppConstants.SwVersionConfig).isEmpty()) {
            Log.e("versionCode2", String.valueOf(intent.getStringExtra(AppConstants.SwVersionConfig)));
            Intent i = new Intent(AppConstants.SwVersionConfig);
            i.putExtra(AppConstants.SwVersionConfig, AppConstants.SwVersionConfig);
            context.sendBroadcast(i);
        }

        if (intent.getStringExtra(AppConstants.TripCancel)!=null && !intent.getStringExtra(AppConstants.TripCancel).isEmpty()) {
            Intent i = new Intent(AppConstants.TripCancel);
            i.putExtra(AppConstants.TripCancel, AppConstants.TripCancel);
            context.sendBroadcast(i);
        }


        if (scheduleTripInfos!=null && scheduleTripInfos.size() > 0) {
            Intent i = new Intent(AppConstants.scheduleTripInfo);
            // Data you need to pass to activity
            i.putParcelableArrayListExtra(AppConstants.scheduleTripInfo, scheduleTripInfos);
            context.sendBroadcast(i);
        }
    }
}
