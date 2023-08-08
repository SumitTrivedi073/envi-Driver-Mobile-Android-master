package sgs.env.ecabsdriver.interfce;

import android.content.Context;


import sgs.env.ecabsdriver.model.DriverStatus;


public interface Notification {
    void notifyUsr(Context context, DriverStatus driverStatus);
}
