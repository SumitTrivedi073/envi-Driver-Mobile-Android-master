package sgs.env.ecabsdriver.interfce;

import android.content.Context;

import sgs.env.ecabsdriver.model.ChargeStation;

public interface SearchStation {
    void nearByStation(Context context);

    interface Method {
        void success();
        void failure();
    }
}
