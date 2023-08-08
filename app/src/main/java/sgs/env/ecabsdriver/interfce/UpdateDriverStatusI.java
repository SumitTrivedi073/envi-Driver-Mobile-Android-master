package sgs.env.ecabsdriver.interfce;

import android.content.Context;



import sgs.env.ecabsdriver.model.DriverStatus;


public interface UpdateDriverStatusI {
    void updateDriverStatusAPI(Context context, DriverStatus driverStatus);  // updating the driver status Free, alloted(after entering the otp), booked(his cab is booked), break, completed
}
