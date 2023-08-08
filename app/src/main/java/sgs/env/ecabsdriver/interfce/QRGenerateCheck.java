package sgs.env.ecabsdriver.interfce;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

public interface QRGenerateCheck {

	void generateQRcode(Context context, String passMstId, ImageView qr_image, TextView refreshQR, double totalfare);

}
