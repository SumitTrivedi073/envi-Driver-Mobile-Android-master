package sgs.env.ecabsdriver.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.interfce.RegisterService;
import sgs.env.ecabsdriver.interfce.UpdateDriverStatusI;
import sgs.env.ecabsdriver.model.ChargeStation;
import sgs.env.ecabsdriver.model.ChargingStationModel;
import sgs.env.ecabsdriver.model.GeneralResponse;
import sgs.env.ecabsdriver.model.LocationPoints;
import sgs.env.ecabsdriver.model.SearchBody;
import sgs.env.ecabsdriver.model.StationResponse;
import sgs.env.ecabsdriver.presenter.UpdateDriverStatusPresenter;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.ProgressBarLayout;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class OptionsActivity extends BaseActivity implements View.OnClickListener{
	
	private static final String TAG = "OptionsAct";

	private Button btnGo,btnStationLocator,btnStart,btnStop;
	
	private TextView tvAddress;

	private Location lstLocation;

	private UpdateDriverStatusI statusI;

	private Double latitude,longitude;

	private String mapUri;

	private String actionName;

	private ProgressBarLayout progressBarLayout;

	boolean isCharging = false;
	List<ChargeStation> chargeStation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		progressBarLayout = new ProgressBarLayout();
		statusI = new UpdateDriverStatusPresenter();
		btnStart = findViewById(R.id.btnStartCharge);
		btnStop = findViewById(R.id.btnStopCharge);
		btnGo = findViewById(R.id.btnGo);
		tvAddress = findViewById(R.id.tvAddress);
		btnStationLocator = findViewById(R.id.btnStationLocator);

		btnStart.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		btnGo.setOnClickListener(this);
		btnStationLocator.setOnClickListener(this);
		tvAddress.setText("Please wait searching near by charging station");

	}

	@Override
	protected void refresh() {
	}

	@Override
	protected int setLayoutIfneeded() {
		return R.layout.no_internet_layout;
	}

	@Override
	protected int getColor() {
		return R.color.bg_screen2;
	}


	@Override
	protected void retrivedLocation(Location location) {
		lstLocation = location;
		if (ECabsApp.isNetworkAvailable(this)) {
			if (!isCharging) {
				chargingAPI();
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1234) {
			if (resultCode == Activity.RESULT_OK) {
				if (mapUri != null) {
					callGoogleMap(mapUri);
				}
			}
		}
	}
	
	private void callGoogleMap(String newUri) {
		Uri googleIntentURI = Uri.parse(newUri);
		Intent mapIntent = new Intent(Intent.ACTION_VIEW, googleIntentURI);
		mapIntent.setPackage("com.google.android.apps.maps");
		startActivity(mapIntent);

	}
	

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnStartCharge:
				setStatus(AppConstants.START_CHARGE);
				break;
			case R.id.btnStopCharge:
				setStatus(AppConstants.STOP_CHARGE);
				break;
			case R.id.direction_to_charging_station:
			case R.id.btnGo:
				if (SharedPrefsHelper.getInstance().get(AppConstants.ChargingStatus)!=null &&
						!SharedPrefsHelper.getInstance().get(AppConstants.ChargingStatus).toString().isEmpty()) {
					if (!SharedPrefsHelper.getInstance().get(AppConstants.ChargingStatus).equals(AppConstants.STATION_GO)) {

						setStatus(AppConstants.STATION_GO);
					}
				}else {
					setStatus(AppConstants.STATION_GO);
				}
				break;

			case R.id.btnStationLocator:
				if (chargeStation.size()>0) {
					nav();
				}else {
					tvAddress.setText("No near by charging station found");
					btnStart.setEnabled(false);
					btnStart.setBackgroundResource(R.color.lightergray);
					btnGo.setBackgroundResource(R.color.lightergray);
					btnGo.setEnabled(false);
					btnStop.setText("Exit");
					btnStop.setEnabled(true);
					btnStop.setBackgroundResource(R.color.red);

				}
				break;
		}
	}

	@Override
	public void onBackPressed() {
		if (SharedPrefsHelper.getInstance().get(AppConstants.ChargingStatus) != null &&
				!SharedPrefsHelper.getInstance().get(AppConstants.ChargingStatus).toString().isEmpty()) {
			if (SharedPrefsHelper.getInstance().get(AppConstants.ChargingStatus).equals(AppConstants.STATION_GO)
					|| SharedPrefsHelper.getInstance().get(AppConstants.ChargingStatus).equals(AppConstants.START_CHARGE)) {
				Toast.makeText(getApplicationContext(), "Can't go back,finish charging firstly!",Toast.LENGTH_LONG).show();
			}else {
				finish();
				super.onBackPressed();
			}
		}else {

			finish();
			super.onBackPressed();
		}
	}


	public void nav() {
		if (mLastLocation != null) {
			String newUri =
					"https://www.google.com/maps/dir/?api=1&origin=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "&destination=" + latitude + "," + longitude;
			Log.d(TAG, "Uri: " + newUri);
			mapUri = newUri;
			if (Build.VERSION.SDK_INT >= 23) {
				if (!Settings.canDrawOverlays(this)) {
					Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
							Uri.parse("package:" + getPackageName()));
					startActivityForResult(intent, 1234);
				} else {
					callGoogleMap(newUri);
				}
			} else {
				callGoogleMap(newUri);
			}
		} else {
			Toast.makeText(this, "Please try again location not captured",
					Toast.LENGTH_LONG).show();
		}
	}

	public void chargingAPI() {
		progressBarLayout.displayDialog(this);
		String token = ECabsApp.getInstance().getAcssToken();
		if (!token.equals(AppConstants.NO_TOKEN) && lstLocation != null) {
			final RegisterService service = ECabsApp.getRetrofit().create(RegisterService.class);
			sgs.env.ecabsdriver.model.Location location = new sgs.env.ecabsdriver.model.Location();
			location.setLatitude(String.valueOf(lstLocation.getLatitude()));
			location.setLongitude(String.valueOf(lstLocation.getLongitude()));
			final SearchBody searchBody = new SearchBody();
			searchBody.setLocation(location);
			Call<StationResponse> call = service.searchStation(token, searchBody);
			Log.d(TAG, "chargingAPI: url " + call.request().url());
			Log.d(TAG, "chargingAPI: payload " + searchBody);
			call.enqueue(new Callback<StationResponse>() {
				@Override
				public void onResponse(@NonNull Call<StationResponse> call,
									   @NonNull Response<StationResponse> response) {
					progressBarLayout.hideProgressDialog();
					if (response.isSuccessful()) {
						isCharging = true;
						 chargeStation = response.body().getStation();
						Log.d(TAG, "onResponse: is " + response.body());
						if (chargeStation.size()>0) {
							for (int i = 0; i < chargeStation.size(); i++) {
								if (chargeStation.get(i).getMalborkOwned() != null && chargeStation.get(i).getMalborkOwned() == true) {
									Gson gson = new Gson();
									String chargingStationJson = gson.toJson(chargeStation.get(i));
									SharedPrefsHelper.getInstance().save(
											AppConstants.CHARGING_STATION_JSON,
											chargingStationJson);
									SharedPrefsHelper.getInstance().save(AppConstants.CONNECTOR_ID,
											chargeStation.get(i).getConnectorId());

									actionName = AppConstants.START_CHARGING_ACTIVITY;
								} else {
									actionName = AppConstants.OPTIONS_ACTIVITY;
								}
								tvAddress.setText(chargeStation.get(i).getAddress());
							}
								LocationPoints points = chargeStation.get(0).getLocation();
								latitude = points.getCoordinates().get(1);
								longitude = points.getCoordinates().get(0);

								if (SharedPrefsHelper.getInstance().get(AppConstants.ChargingStatus)!=null &&
										!SharedPrefsHelper.getInstance().get(AppConstants.ChargingStatus).toString().isEmpty()){
									if (SharedPrefsHelper.getInstance().get(AppConstants.ChargingStatus).equals(AppConstants.STATION_GO)){
										btnGo.setEnabled(false);
										btnGo.setBackgroundResource(R.color.lightergray);
										btnStart.setEnabled(true);
										btnStart.setBackgroundResource(R.color.actioncolor);
										btnStop.setEnabled(false);
										btnStop.setBackgroundResource(R.color.lightergray);
									}
									if (SharedPrefsHelper.getInstance().get(AppConstants.ChargingStatus).equals(AppConstants.START_CHARGE)){
										btnGo.setEnabled(false);
										btnGo.setBackgroundResource(R.color.lightergray);
										btnStart.setEnabled(false);
										btnStart.setBackgroundResource(R.color.lightergray);
										btnStop.setEnabled(true);
										btnStop.setBackgroundResource(R.color.actioncolor);
									}
								}else {
									btnGo.setEnabled(true);
									btnGo.setBackgroundResource(R.color.actioncolor);
									btnStart.setEnabled(false);
									btnStart.setBackgroundResource(R.color.lightergray);
									btnStop.setEnabled(false);
									btnStop.setBackgroundResource(R.color.lightergray);
								}

						} else {
							tvAddress.setText("No near by charging station found");
							btnStart.setEnabled(false);
							btnStart.setBackgroundResource(R.color.lightergray);
							btnGo.setBackgroundResource(R.color.lightergray);
							btnGo.setEnabled(false);
							btnStop.setText("Exit");
							btnStop.setEnabled(true);
							btnStop.setBackgroundResource(R.color.red);
						}
					} else {
						try {
							Log.d(TAG, "onResponse: failure " + response.errorBody().string());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				}
				
				@Override
				public void onFailure(@NonNull Call<StationResponse> call, @NonNull Throwable t) {
					Log.d(TAG, "onFailure: " + t.getMessage());
					progressBarLayout.hideProgressDialog();
				}
			});
		} else {
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					chargingAPI();
				}
			}, 20000);
		}
	}
	
	private void setStatus(String chargeStatus) {
		progressBarLayout.displayDialog(this);
		String token = ECabsApp.getInstance().getAcssToken();
		if (!token.equals(AppConstants.NO_TOKEN) && lstLocation != null) {
			final RegisterService service = ECabsApp.getRetrofit().create(RegisterService.class);
			ChargingStationModel chargingStationModel = new ChargingStationModel();
			chargingStationModel.setCharging(chargeStatus);
			chargingStationModel.setDriverId(SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, ""));
			Call<GeneralResponse> call = service.UpdateChargingStatus(token, chargingStationModel);
			Log.d(TAG, "chargingStationAPI: url " + call.request().url());
			Log.d(TAG, "chargingStationAPI: payload " + chargingStationModel);
			call.enqueue(new Callback<GeneralResponse>() {
				@Override
				public void onResponse(@NonNull Call<GeneralResponse> call,
									   @NonNull Response<GeneralResponse> response) {
					progressBarLayout.hideProgressDialog();
					if (response.isSuccessful()) {
						if (chargeStatus.equals(AppConstants.STATION_GO)){

							btnGo.setEnabled(false);
							btnGo.setBackgroundResource(R.color.lightergray);
							btnStart.setEnabled(true);
							btnStart.setBackgroundResource(R.color.actioncolor);
							btnStop.setEnabled(false);
							btnStop.setBackgroundResource(R.color.lightergray);
							SharedPrefsHelper.getInstance().save(AppConstants.ChargingStatus, AppConstants.STATION_GO);
							nav();
						}  if (chargeStatus.equals(AppConstants.START_CHARGE)){
							btnGo.setEnabled(false);
							btnGo.setBackgroundResource(R.color.lightergray);
							btnStart.setEnabled(false);
							btnStart.setBackgroundResource(R.color.lightergray);
							btnStop.setEnabled(true);
							btnStop.setBackgroundResource(R.color.actioncolor);
							SharedPrefsHelper.getInstance().save(AppConstants.ChargingStatus, AppConstants.START_CHARGE);
						}
						if (chargeStatus.equals(AppConstants.STOP_CHARGE)){
							SharedPrefsHelper.getInstance().save(AppConstants.ChargingStatus, "");
							nxtActivity();
						}
					} else {
						progressBarLayout.hideProgressDialog();
						try {
							Log.d(TAG, "onResponse: failure " + response.errorBody().string());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				}

				@Override
				public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
					Log.d(TAG, "onFailure: " + t.getMessage());
					progressBarLayout.hideProgressDialog();
				}
			});

	}
	}
	
	private void nxtActivity() {
		Intent intent = new Intent(OptionsActivity.this, HomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	

}
