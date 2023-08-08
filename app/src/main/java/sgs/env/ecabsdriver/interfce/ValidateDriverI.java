package sgs.env.ecabsdriver.interfce;

import android.content.Context;

import sgs.env.ecabsdriver.model.CheckDriverTripStatus;

public interface ValidateDriverI {

    void getDriverTripStatus(Context context, CheckDriverTripStatus checkTripStatus);
}
