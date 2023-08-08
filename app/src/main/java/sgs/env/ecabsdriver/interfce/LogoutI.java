package sgs.env.ecabsdriver.interfce;

import android.content.Context;

import sgs.env.ecabsdriver.model.CloseDay;


public interface LogoutI {
    
    void endDriverDayAPI(Context context, CloseDay location);  // ending the driver day, driver shift is over......
}
