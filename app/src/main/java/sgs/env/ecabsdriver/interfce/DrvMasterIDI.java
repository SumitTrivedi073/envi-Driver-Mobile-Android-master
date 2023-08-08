package sgs.env.ecabsdriver.interfce;

import android.content.Context;



import sgs.env.ecabsdriver.model.InfoModelB;


public interface DrvMasterIDI {
    void startRideAPI(Context context, InfoModelB modelB);  // driverDayStarts by calling, driver will reicve the customers notification
}
