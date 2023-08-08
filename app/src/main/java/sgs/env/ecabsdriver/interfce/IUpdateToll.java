package sgs.env.ecabsdriver.interfce;

import android.content.Context;

import sgs.env.ecabsdriver.model.TollAmount;

public interface IUpdateToll {
    void updateToll(Context context, TollAmount tollAmount);
}
