package sgs.env.ecabsdriver.presenter;

import static sgs.env.ecabsdriver.activity.WaitingForCustomerActivity.listenerForCancellationOfTrips;
import static sgs.env.ecabsdriver.activity.WaitingForCustomerActivity.mListener;
import static sgs.env.ecabsdriver.util.DriverAlgorithm.logoutfromApp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;

import retrofit2.Callback;
import retrofit2.Response;
import sgs.env.ecabsdriver.activity.LoginActivity;
import sgs.env.ecabsdriver.interfce.RegisterService;
import sgs.env.ecabsdriver.data.local.ECabsDriverDbHelper;
import sgs.env.ecabsdriver.interfce.LogoutI;
import sgs.env.ecabsdriver.model.CloseDay;
import sgs.env.ecabsdriver.model.GeneralResponse;
import sgs.env.ecabsdriver.service.LocationService;
import sgs.env.ecabsdriver.service.RetrieveFirestoreData;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.ProgressBarLayout;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;


public class LogoutPresenter implements LogoutI {
	
	private static final String TAG = "LogoutPresenter";
	
	ProgressBarLayout uImethodsI;
	
	private SharedPrefsHelper helper;
	
	// logout api for driver
	
	// driver end day api
	@Override
	public void endDriverDayAPI(final Context context, CloseDay location) {
		uImethodsI = new ProgressBarLayout();
		String token = ECabsApp.getInstance().getAcssToken();
		helper = SharedPrefsHelper.getInstance();
		if (!token.contentEquals(AppConstants.NO_TOKEN)) {
			uImethodsI.displayDialog(context);
			RegisterService service = ECabsApp.getRetrofit().create(RegisterService.class);
			retrofit2.Call<GeneralResponse> call = service.closeDay(token, location);
			Log.d(TAG, "closeRideAPI: URL " + call.request().url());

			call.enqueue(new Callback<GeneralResponse>() {
				@Override
				public void onResponse(@NonNull retrofit2.Call<GeneralResponse> call,
									   @NonNull Response<GeneralResponse> response) {
					if (response.isSuccessful() && response.code()==200) {
						uImethodsI.hideProgressDialog();
						helper.clearAllData();
						helper.save(AppConstants.ACTIVE, false);
						helper.save(AppConstants.DRIVER_LOGIN, false);

						helper.save(AppConstants.GEN_MASTER_ID, true);
						Intent intent = new Intent(context, LoginActivity.class);
						intent.addFlags(
								Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
						context.startActivity(intent);
						new ECabsDriverDbHelper(context).delBreak();
						new ECabsDriverDbHelper(context).delSoc();
						context.stopService(new Intent(context, RetrieveFirestoreData.class));
						context.stopService(new Intent(context, LocationService.class));
						NotificationManagerCompat.from(context).cancelAll();
					} else if(response.code()==400){
						uImethodsI.hideProgressDialog();
						try {
							if (response.code() == 400) {
								Toast.makeText(context,
										"Contact Admin can't logout during shift hours ",
										Toast.LENGTH_SHORT).show();
							} else {
								Log.d(TAG, "onResponse: failure " + response.errorBody().string());
								Toast.makeText(context, "unable to logout !",
										Toast.LENGTH_LONG).show();
							}
						} catch (IOException e) {
							Log.d(TAG, "onResponse: exception " + e.getMessage());
						}
					}else if(response.code() == 401) {
						logoutfromApp(context);
					}
					uImethodsI.hideProgressDialog();
				}
				
				@Override
				public void onFailure(@NonNull retrofit2.Call<GeneralResponse> call, @NonNull Throwable t) {
					Log.d(TAG, "onFailure: " + t.getMessage());
					uImethodsI.hideProgressDialog();
					Toast.makeText(context, "unable to logout !", Toast.LENGTH_LONG).show();
				}
			});
		} else {
			Toast.makeText(context, "Please Logout and login again!", Toast.LENGTH_LONG).show();
		}
	}
	
}

