package sgs.env.ecabsdriver.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;

import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.data.local.ECabsDriverDbHelper;
import sgs.env.ecabsdriver.interfce.IUpdateToll;
import sgs.env.ecabsdriver.interfce.PaymentStatusCheck;
import sgs.env.ecabsdriver.interfce.QRGenerateCheck;
import sgs.env.ecabsdriver.interfce.SendInvoiceI;
import sgs.env.ecabsdriver.interfce.UImethodsI;
import sgs.env.ecabsdriver.model.CustomerNotfication;
import sgs.env.ecabsdriver.model.TollAmount;
import sgs.env.ecabsdriver.model.TripDataModel;
import sgs.env.ecabsdriver.presenter.QRCode_Detail;
import sgs.env.ecabsdriver.presenter.QRGeneratePresenter;
import sgs.env.ecabsdriver.presenter.SendInvoicePresenter;
import sgs.env.ecabsdriver.presenter.TransactionStatusPresenter;
import sgs.env.ecabsdriver.presenter.UpdateTollPresenter;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.MediaPlayerSingleton;
import sgs.env.ecabsdriver.util.ProgressBarLayout;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class CollectCashActivity extends BaseActivity implements UImethodsI {

    private static final String TAG = "CollectCashActivity";
    private final boolean goBack = true;

    String cgst, sgst, kmFare, drvMstId, mode, passengerMastId;

    private LinearLayout tollInput, toll_layout, tollCharges_view,advancePaid_view,DiscountView;

    private Button tollSubmit;

    private EditText tollAmount;

    private ImageButton buttonBack;

    private Button buttonCollect;

    private TextView textViewDistance, checkTansactionStatus,
            tvTollAmount,advancePaid_txt,tvperKmPrice,GstPriceTxt,DiscountpriceTxt;

    private TextView textViewFare,qrGenMessage;

    private TextView textViewTotalFre;

    private TextView paymentModeMsg;

    private double totalFare = 0.0;

    private ImageView paymentModeImage, paymentModeIcon, qr_image;

    private MediaPlayer mediaPlayer;

    private TextView collectPaymentInfo, refreshQR;

    private TextView paymentMode;

    private ProgressBarLayout progressBarLayout;

    CheckBox toll_checkBox;

    TripDataModel tripDataModel;

    Handler handler = new Handler();

    Runnable runnable;

    public boolean isQRGenerate = false;

    public static CollectCashActivity instance;

    private ListenerRegistration tripDataEventListner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_cash);
        inIt();
        listenr();
    }

    private void inIt() {
        instance = CollectCashActivity.this;
        progressBarLayout = new ProgressBarLayout();
        textViewDistance = findViewById(R.id.tvDistance);
        textViewTotalFre = findViewById(R.id.tvTotalFare);
        textViewFare = findViewById(R.id.tvFare);
        collectPaymentInfo = findViewById(R.id.collect_payment_info);
        paymentModeMsg = findViewById(R.id.paymentModeMsg);
        paymentModeImage = findViewById(R.id.paymentMode_image);
        tollInput = findViewById(R.id.toll_input);
        tollSubmit = findViewById(R.id.tollSubmit);
        tollAmount = findViewById(R.id.toll_amount);
        buttonBack = findViewById(R.id.buttonBack);
        paymentMode = findViewById(R.id.paymentMode_value);
        paymentModeIcon = findViewById(R.id.paymentMode_icon);
        buttonCollect = findViewById(R.id.buttonCollect);
        checkTansactionStatus = findViewById(R.id.checkTansactionStatus);
        toll_layout = findViewById(R.id.toll_layout);
        qr_image = findViewById(R.id.qr_image);
        refreshQR = findViewById(R.id.refreshQR);
        toll_checkBox = findViewById(R.id.toll_checkBox);
        tvTollAmount = findViewById(R.id.tvTollAmount);
        tollCharges_view = findViewById(R.id.tollCharges_view);
        qrGenMessage = findViewById(R.id.qrGenMessage);
        advancePaid_view = findViewById(R.id.advancePaid_view);
        advancePaid_txt = findViewById(R.id.advancePaid_txt);
        tvperKmPrice = findViewById(R.id.tvperKmPrice);
        GstPriceTxt = findViewById(R.id.GstPriceTxt);
        DiscountView = findViewById(R.id.DiscountView);
        DiscountpriceTxt = findViewById(R.id.DiscountpriceTxt);

        cgst = SharedPrefsHelper.getInstance().get(AppConstants.CGST, "0");
        sgst = SharedPrefsHelper.getInstance().get(AppConstants.SGST, "0");
        kmFare = SharedPrefsHelper.getInstance().get(AppConstants.PER_KM, "0");
        drvMstId = SharedPrefsHelper.getInstance().get(AppConstants.DRV_MASTER_ID, "");
        mode = SharedPrefsHelper.getInstance().get(AppConstants.PAYMENT_MODE, "NA");

        if (SharedPrefsHelper.getInstance().get(AppConstants.TOLL_CHARGES, "") != null
                && !SharedPrefsHelper.getInstance().get(AppConstants.TOLL_CHARGES, "").isEmpty()
        && !SharedPrefsHelper.getInstance().get(AppConstants.TOLL_CHARGES, "").equals("0")) {
            tvTollAmount.setText(SharedPrefsHelper.getInstance().get(AppConstants.TOLL_CHARGES));
            tollAmount.setText(SharedPrefsHelper.getInstance().get(AppConstants.TOLL_CHARGES));
            tollInput.setVisibility(View.VISIBLE);
            toll_checkBox.setVisibility(View.GONE);
            tollCharges_view.setVisibility(View.VISIBLE);
        }else {
            tollInput.setVisibility(View.GONE);
            tollCharges_view.setVisibility(View.GONE);
            toll_checkBox.setVisibility(View.VISIBLE);
        }

        if (SharedPrefsHelper.getInstance().get(AppConstants.AmountTobeCollected, "") != null &&
                !SharedPrefsHelper.getInstance().get(AppConstants.AmountTobeCollected, "").isEmpty()) {
            totalFare = Double.parseDouble(SharedPrefsHelper.getInstance().get(AppConstants.AmountTobeCollected));
        }
        passengerMastId = SharedPrefsHelper.getInstance().get(AppConstants.PASS_MST_ID, "");
        textViewDistance.setText(SharedPrefsHelper.getInstance().get(AppConstants.DISTANCE, "0.0"));
        tvperKmPrice.setText(SharedPrefsHelper.getInstance().get(AppConstants.PER_KM_PRICE, "0"));
        textViewFare.setText(kmFare);

        if (totalFare != 0) {
            textViewTotalFre.setText(String.valueOf(totalFare));
        } else {
            textViewTotalFre.setText(String.valueOf(totalFare));
            buttonCollect.setVisibility(View.VISIBLE);
            buttonCollect.setText(R.string.confirm);
            checkTansactionStatus.setVisibility(View.GONE);
        }

        double x=   Double.parseDouble(cgst);
        double y=  Double.parseDouble(sgst);

        //sum these two numbers
        double Gst=x+y;
        GstPriceTxt.setText(String.valueOf(Gst));
        if (SharedPrefsHelper.getInstance().get(AppConstants.DISCOUNT, "0")!=null &&
                !SharedPrefsHelper.getInstance().get(AppConstants.DISCOUNT, "0").equals("0")) {
            DiscountView.setVisibility(View.VISIBLE);
            DiscountpriceTxt.setText(SharedPrefsHelper.getInstance().get(AppConstants.DISCOUNT));
        }else {
            DiscountView.setVisibility(View.GONE);
        }
        if (SharedPrefsHelper.getInstance().get(AppConstants.ADVANCEPAID, "0")!=null &&
                !SharedPrefsHelper.getInstance().get(AppConstants.ADVANCEPAID, "0").equals("0")) {
            advancePaid_view.setVisibility(View.VISIBLE);
            advancePaid_txt.setText(SharedPrefsHelper.getInstance().get(AppConstants.ADVANCEPAID, "0"));
        }else {
            advancePaid_view.setVisibility(View.GONE);
        }
    }

    private void listenr() {
        buttonBack.setOnClickListener(
                view -> Toast.makeText(CollectCashActivity.this, "Can't go back without rating",
                        Toast.LENGTH_SHORT).show());

        tollSubmit.setOnClickListener(v -> {
            String toll = tollAmount.getText().toString();
            if (toll.isEmpty()) {
                Toast.makeText(getApplicationContext(),
                        "Please enter a valid amount and then click on OK",
                        Toast.LENGTH_LONG).show();
            } else {
                if (!String.valueOf(Double.parseDouble(toll)).equals(SharedPrefsHelper.getInstance().get(AppConstants.TOLL_CHARGES, "0.0"))) {

                    updateTollAmount(toll);
                }
            }
        });
        buttonCollect.setOnClickListener(view -> {
            MapActivity.isFirstTime = false;
            SharedPrefsHelper.getInstance().save(AppConstants.END_TRIP_BUTTON_CLICKED, false);
            SendInvoiceI sendInvoiceI = new SendInvoicePresenter();
            sendInvoiceI.sendInvoiceAPI(CollectCashActivity.this, totalFare);
        });
        checkTansactionStatus.setOnClickListener(v -> {
            PaymentStatusCheck paymentCreateOrder = new TransactionStatusPresenter();
            if (passengerMastId != null && !passengerMastId.isEmpty()) {
                paymentCreateOrder.verifyTransaction(CollectCashActivity.this, passengerMastId);
            }
        });

        refreshQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpopupRegenerate(getString(R.string.qr_regenerate_txt));
            }
        });

        toll_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tollInput.setVisibility(View.VISIBLE);
                    toll_checkBox.setVisibility(View.GONE);
                }
            }
        });

    }

    public void updateTollAmount(String tollAmount) {

        TollAmount tollAmt = new TollAmount();
        String passengerMastId = SharedPrefsHelper.getInstance().get(AppConstants.PASS_MST_ID, "");
        String drvMstId = SharedPrefsHelper.getInstance().get(AppConstants.DRV_MASTER_ID, "");
        tollAmt.setTollAmount(Double.parseDouble(tollAmount));
        tollAmt.setDriverTripMasterId(drvMstId);
        tollAmt.setPassengerTripMasterId(passengerMastId);
        tollAmt.setTotalFare(Double.parseDouble(textViewTotalFre.getText().toString()));
        IUpdateToll iUpdateToll = new UpdateTollPresenter();
        iUpdateToll.updateToll(CollectCashActivity.this, tollAmt);

    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieveTripData();
    }

    public void retrieveTripData(){

        DocumentReference tripDataRef =  FirebaseFirestore.getInstance().collection("driver").document(SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, ""))
                .collection("running-trip").document("tripInfo");

        tripDataEventListner = tripDataRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }

                String PassangerTripMasterID;
                if(value!=null){
                    PassangerTripMasterID = value.getString("passengerTripMasterId");
                    if(PassangerTripMasterID!=null && !PassangerTripMasterID.isEmpty()){
                        FirebaseFirestore.getInstance().collection("trips")
                                .document("passengerTripMasterId:"+PassangerTripMasterID)
                                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                        @Nullable FirebaseFirestoreException e) {
                                        if (e != null) {
                                            Log.w(TAG, "Listen failed.", e);
                                            return;
                                        }

                                        if (snapshot != null && snapshot.exists()) {
                                            Log.d(TAG, "Trip data: " + snapshot.getData());
                                            tripDataModel = snapshot.toObject(TripDataModel.class);
                                            fireStoreConnecting(tripDataModel);

                                        }
                                    }
                                });

                    }
                }
            }
        });

    }



    private void fireStoreConnecting(TripDataModel tripDataModel) {
       MediaPlayerSingleton mediaPlayerSingleton = MediaPlayerSingleton.getInstance();
        mediaPlayerSingleton.init(CollectCashActivity.this);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }

        if(tripDataModel.getTripInfo()!=null && tripDataModel.getTripInfo().getPriceClass()!=null &&
                tripDataModel.getTripInfo().getPriceClass().getTotalFare()!=null) {
            totalFare = tripDataModel.getTripInfo().getPriceClass().getTotalFare();
            textViewTotalFre.setText(String.valueOf(totalFare));
        }
        if (tripDataModel.getTripInfo().getPaymentMode() != null) {
            if(!SharedPrefsHelper.getInstance().get(AppConstants.PAYMENT_MODE,AppConstants.QR_CODE).equals(tripDataModel.getTripInfo().getPaymentMode())) {
                SharedPrefsHelper.getInstance().save(AppConstants.PAYMENT_MODE,
                        tripDataModel.getTripInfo().getPaymentMode());
                setPaymentOptions(tripDataModel.getTripInfo().getPaymentMode());
            }else if (SharedPrefsHelper.getInstance().get(AppConstants.PAYMENT_MODE) != null) {
                paymentMode.setVisibility(View.VISIBLE);
                paymentMode.setText(SharedPrefsHelper.getInstance().get(AppConstants.PAYMENT_MODE));
                setPaymentOptions(SharedPrefsHelper.getInstance().get(AppConstants.PAYMENT_MODE));
            }
        }
        }

    private void setPaymentOptions(String paymentMode) {
        switch (paymentMode) {
            case AppConstants.CASH:
                setPaymentCash();
                break;
            case AppConstants.ONLINE:
                setPaymentOnline(tripDataModel);
                break;
            case AppConstants.QR_CODE:
            default:
                setPaymentQR(tripDataModel);
        }
    }

    private void setPaymentCash() {
        toggleFieldsBasedOnPaymentMode(true);
        mediaPlayer = MediaPlayerSingleton.getSingletonMedia().create(
                getApplicationContext(), R.raw.cash_payment_mode);
        mediaPlayer.start();

        if (totalFare!=0){
        paymentModeImage.setImageResource(R.drawable.ic_cash_icon_large);
        paymentModeIcon.setImageResource(R.drawable.ic_paymentmode_cash);
        collectPaymentInfo.setText(R.string.collect_cash);
        buttonCollect.setVisibility(View.VISIBLE);
        checkTansactionStatus.setVisibility(View.GONE);
        toll_layout.setVisibility(View.VISIBLE);
        qr_image.setVisibility(View.GONE);
        refreshQR.setVisibility(View.GONE);
        qrGenMessage.setVisibility(View.GONE);
        buttonCollect.setText(R.string.confirm);
        paymentModeMsg.setText(getResources().getString(
                R.string.payment_mode_msg) + AppConstants.CASH);
        paymentMode.setText(AppConstants.CASH);

    }else {
        checkTansactionStatus.setVisibility(View.GONE);
        buttonCollect.setVisibility(View.VISIBLE);
        buttonCollect.setText(R.string.confirm);
    }
    }

    private void setPaymentQR(TripDataModel tripDataModel) {
        if (tripDataModel!=null && tripDataModel.getTripInfo().getPaymentStatus() != null
                && tripDataModel.getTripInfo().getPaymentStatus().equals(AppConstants.PAYMENT_COMPLETED)) {

           if (totalFare!=0) {
               collectPaymentInfo.setVisibility(View.VISIBLE);
               checkTansactionStatus.setVisibility(View.VISIBLE);
               toll_layout.setVisibility(View.GONE);
               paymentModeMsg.setVisibility(View.GONE);
               collectPaymentInfo.setText(R.string.successful_qr_payment);
               buttonCollect.setVisibility(View.GONE);

           }else {
                checkTansactionStatus.setVisibility(View.GONE);
                buttonCollect.setVisibility(View.VISIBLE);
                buttonCollect.setText(R.string.confirm);
            }


        } else {
            if (!isQRGenerate) {
                showQRCode();
            }else {
                buttonDisable(refreshQR);
            }
            mediaPlayer = MediaPlayer.create(
                    CollectCashActivity.this, R.raw.qr_code_payment_mode);
            mediaPlayer.start();
            toggleFieldsBasedOnPaymentMode(true);

            if (totalFare!=0) {
            paymentModeMsg.setText(getResources().getString(
                    R.string.payment_mode_msg) + AppConstants.QR_CODE);
            paymentMode.setText(AppConstants.QR_CODE);
            paymentModeImage.setImageResource(
                    R.drawable.ic_qr_code_payment_image);
            paymentModeImage.setVisibility(View.GONE);
            paymentModeIcon.setImageResource(R.drawable.ic_qr_code_icon);
            qr_image.setVisibility(View.VISIBLE);
            collectPaymentInfo.setText(R.string.confirm_if_payment_is_done);
            checkTansactionStatus.setVisibility(View.VISIBLE);
            collectPaymentInfo.setVisibility(View.GONE);
            buttonCollect.setVisibility(View.GONE);
            qrGenMessage.setVisibility(View.VISIBLE);

            }else {
                checkTansactionStatus.setVisibility(View.GONE);
                buttonCollect.setVisibility(View.VISIBLE);
                buttonCollect.setText(R.string.confirm);
            }
        }

    }

    private void showQRCode() {
        String json = SharedPrefsHelper.getInstance().get(AppConstants.QRCODE_DETAIL, "");
        if (json.isEmpty()) {
            CallQrCodeApi();
        } else {
            QRCode_Detail qrCode_detail = new Gson().fromJson(json, QRCode_Detail.class);
            if (qrCode_detail.getPassangerTripMasterID().equals(SharedPrefsHelper.getInstance().get(AppConstants.PASS_MST_ID, ""))
                    && qrCode_detail.getTotalfare() == Double.parseDouble(SharedPrefsHelper.getInstance().get(AppConstants.AmountTobeCollected, "0.0"))) {
                byte[] decodedString = Base64.decode(qrCode_detail.getImage().getBytes(StandardCharsets.UTF_8)
                        , Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                Glide.with(this).load(bitmap).placeholder(ContextCompat.getDrawable(this, R.drawable.reload_qr)).into(qr_image);
                buttonDisable(refreshQR);
            } else {
                CallQrCodeApi();
            }
        }
    }


    private void CallQrCodeApi() {
        SharedPrefsHelper.getInstance().delete(AppConstants.QRCODE_DETAIL);
        qrGenMessage.setText(R.string.qr_generating_message);
        qrGenMessage.setTextColor(getResources().getColor(R.color.colorPrimary));
         QRGenerateCheck qrGeneratePresenter = new QRGeneratePresenter();
        if (passengerMastId != null && !passengerMastId.isEmpty()) {
            qrGeneratePresenter.generateQRcode(CollectCashActivity.this, passengerMastId,
                    qr_image, refreshQR, Double.parseDouble(textViewTotalFre.getText().toString()));
        }
    }

    private void setPaymentOnline(TripDataModel tripDataModel) {
        if (tripDataModel!=null && tripDataModel.getTripInfo().getPaymentStatus() != null && tripDataModel.getTripInfo().getPaymentStatus().equals(
                AppConstants.PAYMENT_COMPLETED)) {

            collectPaymentInfo.setVisibility(View.VISIBLE);

            toll_layout.setVisibility(View.GONE);
            paymentModeMsg.setVisibility(View.GONE);
            collectPaymentInfo.setText(R.string.successful_online_payment);

            if (totalFare != 0) {
                collectPaymentInfo.setVisibility(View.VISIBLE);
                checkTansactionStatus.setVisibility(View.VISIBLE);
                toll_layout.setVisibility(View.GONE);
                paymentModeMsg.setVisibility(View.GONE);
                collectPaymentInfo.setText(R.string.successful_online_payment);
                buttonCollect.setVisibility(View.GONE);
            }else {
                checkTansactionStatus.setVisibility(View.GONE);
                buttonCollect.setVisibility(View.VISIBLE);
                buttonCollect.setText(R.string.confirm);
            }



        }else {
            toggleFieldsBasedOnPaymentMode(true);
            mediaPlayer = MediaPlayerSingleton.getSingletonMedia().create(
                    getApplicationContext(), R.raw.online_payment_mode);
            mediaPlayer.start();
            paymentModeMsg.setText(getResources().getString(
                    R.string.payment_mode_msg) + AppConstants.ONLINE);
            paymentModeIcon.setImageResource(R.drawable.ic_online_icon);
            paymentModeMsg.setText(R.string.online_payment_confirmation_wait);
            paymentMode.setText(AppConstants.ONLINE);

            collectPaymentInfo.setVisibility(View.GONE);
            qr_image.setVisibility(View.GONE);
            refreshQR.setVisibility(View.GONE);
            qrGenMessage.setVisibility(View.GONE);

            if (totalFare != 0) {
                checkTansactionStatus.setVisibility(View.VISIBLE);
                buttonCollect.setVisibility(View.GONE);
            }else {
                checkTansactionStatus.setVisibility(View.GONE);
                buttonCollect.setVisibility(View.VISIBLE);
                buttonCollect.setText(R.string.confirm);

            }

        }
    }

    public void toggleFieldsBasedOnPaymentMode(boolean isDisplay) {
        if (isDisplay) {
            if (paymentMode.toString().equals(AppConstants.CASH)) {
                buttonCollect.setVisibility(View.VISIBLE);
            }
            paymentModeMsg.setVisibility(View.VISIBLE);
            collectPaymentInfo.setVisibility(View.VISIBLE);
            paymentMode.setVisibility(View.VISIBLE);
        } else {
            buttonCollect.setVisibility(View.GONE);
            paymentModeMsg.setVisibility(View.GONE);
            collectPaymentInfo.setVisibility(View.GONE);
            paymentMode.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (!goBack) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "collect amount to go back", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void startProgessBar() {
        progressBarLayout.displayDialog(CollectCashActivity.this);
    }

    @Override
    public void endProgressBar() {
        if (progressBarLayout != null) {
            progressBarLayout.hideProgressDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tripDataEventListner!=null){
            tripDataEventListner.remove();
            tripDataEventListner = null;
        }
        stopMediaplayer();
    }
    private void stopMediaplayer() {
        MediaPlayerSingleton mediaPlayerSingleton = MediaPlayerSingleton.getInstance();
        mediaPlayerSingleton.init(getApplicationContext());
        MediaPlayer mediaPlayer = MediaPlayerSingleton.getSingletonMedia();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.stop();
        }

    }

    public void buttonDisable(TextView refreshQR) {
        isQRGenerate  = true;
        refreshQR.setVisibility(View.GONE);
        qrGenMessage.setText(getResources().getString(R.string.regenrate_qr_message));
        qrGenMessage.setTextColor(getResources().getColor(R.color.red));
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                if (paymentMode.getText().toString().equals(AppConstants.QR_CODE)) {
                    refreshQR.setVisibility(View.VISIBLE);
                    qrGenMessage.setVisibility(View.GONE);
                }
            }
        }, 60000);

    }

    public void generateQRFailure(String userMessage) {
        isQRGenerate  = false;
        refreshQR.setVisibility(View.VISIBLE);
        qrGenMessage.setVisibility(View.GONE);
        showpopupMessage(userMessage);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();
        stopMediaplayer();
    }

    @Override
    protected void refresh() {

    }

    @Override
    protected int setLayoutIfneeded() {
        return 0;
    }

    @Override
    protected int getColor() {
        return 0;
    }

    @Override
    protected void retrivedLocation(Location location) {

    }

    public void tollAddSuccess(TollAmount tollAmount) {
        tollCharges_view.setVisibility(View.VISIBLE);
        totalFare = tollAmount.getAmountTobeCollected();
        textViewTotalFre.setText(String.valueOf(tollAmount.getAmountTobeCollected()));
        if (tollAmount.getAmountTobeCollected()>0) {
            checkTansactionStatus.setVisibility(View.VISIBLE);
            buttonCollect.setVisibility(View.GONE);
        }else  if (tollAmount.getAmountTobeCollected() == 0) {
            checkTansactionStatus.setVisibility(View.GONE);
            buttonCollect.setVisibility(View.VISIBLE);
            buttonCollect.setText(R.string.confirm);
        }
        double x=   Double.parseDouble(String.valueOf(tollAmount.getSgst()));
        double y=  Double.parseDouble(String.valueOf(tollAmount.getCgst()));

        //sum these two numbers
        double Gst=x+y;
        GstPriceTxt.setText(String.valueOf(Gst));
        advancePaid_txt.setText(String.valueOf(tollAmount.getAdvancePaid()));
        tvTollAmount.setText(String.valueOf(tollAmount.getTollAmount()));
        if (paymentMode.getText().toString().trim().equals(AppConstants.QR_CODE)) {
            refreshQR.setVisibility(View.VISIBLE);
            qr_image.setImageResource(R.drawable.reload_qr);
            qrGenMessage.setVisibility(View.GONE);
            handler.removeCallbacks(runnable);
            showpopupMessage(getResources().getString(R.string.click_Refresh_QR));
        }
    }

    public void tollAddFailure(String userMessage) {
        showpopupMessage(userMessage);
    }

    private void showpopupMessage(String message) {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.transaction_status_popup, null);
        AlertDialog.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        }

        builder.setView(layout);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (!isFinishing()) {
            alertDialog.show();
        }

        TextView OK_txt = layout.findViewById(R.id.OK_txt);
        TextView title_txt = layout.findViewById(R.id.title_txt);

        title_txt.setText(message);

        OK_txt.setOnClickListener(v -> alertDialog.dismiss());

    }

    private void showpopupRegenerate(String message) {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup_qr_regenerate, null);
        AlertDialog.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        }

        builder.setView(layout);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (!isFinishing()) {
            alertDialog.show();
        }

        TextView okTxt = layout.findViewById(R.id.OK_txt);
        TextView cancelTxt = layout.findViewById(R.id.cancel_txt);
        TextView title_txt = layout.findViewById(R.id.title_txt);

        title_txt.setText(message);

        okTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallQrCodeApi();
                alertDialog.dismiss();
            }
        });

        cancelTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }



}
