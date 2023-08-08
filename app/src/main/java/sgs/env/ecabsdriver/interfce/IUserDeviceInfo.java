package sgs.env.ecabsdriver.interfce;

import android.content.Context;

import sgs.env.ecabsdriver.activity.HomeActivity;
import sgs.env.ecabsdriver.model.UserDeviceInfoModel;

public interface IUserDeviceInfo {
    
    void saveUserDeviceInfo(UserDeviceInfoModel userDeviceInfoModel, Context context);
}
