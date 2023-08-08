package sgs.env.ecabsdriver.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import sgs.env.ecabsdriver.util.AppConstants;

public class FirebaseBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = "MyReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive called");
        Intent i = new Intent(AppConstants.logoutFromFirebase);
        context.sendBroadcast(i);

    }
}
