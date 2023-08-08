package sgs.env.ecabsdriver.interfce;

import android.content.Context;

import sgs.env.ecabsdriver.data.local.ECabsDriverDbHelper;

public interface PaymentStatusCheck {
	
	void verifyTransaction(Context context, String passMstId);

}
