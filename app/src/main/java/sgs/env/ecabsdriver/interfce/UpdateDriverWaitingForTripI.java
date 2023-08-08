package sgs.env.ecabsdriver.interfce;

import android.content.Context;

import sgs.env.ecabsdriver.model.DriverWaitingForTrip;


public interface UpdateDriverWaitingForTripI {
    void updateDriverWaitingAPI(Context context, DriverWaitingForTrip driverWaitingForTrip);  // updating the driver status Free, alloted(after entering the otp), booked(his cab is booked), break, completed
}
