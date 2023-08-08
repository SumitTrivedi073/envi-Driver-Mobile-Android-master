package sgs.env.ecabsdriver.interfce;

import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import sgs.env.ecabsdriver.model.UpdatePaymentStatus;
import sgs.env.ecabsdriver.model.PaytmModel;

public interface UpdatePaymentStatuss {
    void updatePaymentSatusApi(Context context, PaytmModel paytmModel, TextView processingText, TextView btnCollect, TextView paymentStatus);
    void updatePaymentSatusApiforcash(Context context, UpdatePaymentStatus paytmModel, TextView processingText, TextView btnCollect, TextView paymentStatus);

    void generateQRcode(Context context, String passengerTripMasterId, ImageView qrImg, TextView generateQR, double totalfare);
}
