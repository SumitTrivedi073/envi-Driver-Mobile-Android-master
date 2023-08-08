package sgs.env.ecabsdriver.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.List;

import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.interfce.UpdatePaymentStatuss;
import sgs.env.ecabsdriver.model.PaytmModel;
import sgs.env.ecabsdriver.model.TripDta;
import sgs.env.ecabsdriver.model.UpdatePaymentStatus;
import sgs.env.ecabsdriver.presenter.QRCode_Detail;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.DateUtil;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class ReadPaginationAdapter extends RecyclerView.Adapter<ReadPaginationAdapter.CompanionViewHolder> {

    private static CompanionViewHolder viewHolder;

    private final List<TripDta> tripDataList;

    private final Context context;

    // creating a constructor.
    public ReadPaginationAdapter(Context context, List<TripDta> triphistorylist) {
        this.context = context;
        this.tripDataList = triphistorylist;

    }

    @NonNull
    @Override
    public CompanionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflating our layout file on below line.
        View view = LayoutInflater.from(context).inflate(R.layout.drivers_trip_row, parent, false);
        return new CompanionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanionViewHolder holder, int position) {

        viewHolder = holder;
        // getting data from our array list in our modal class.
        holder.bind(tripDataList.get(position));
    }

    @Override
    public int getItemCount() {
        // returning the size of array list.
        return tripDataList.size();
    }

    public static class CompanionViewHolder extends RecyclerView.ViewHolder {

        TextView textViewSource, textViewDestination, tvDistance,
                textViewTotalFare, textViewDate, tvStatus, tvPaymentMode, tvAdvancePaid, amountCollected,
                tvAmountOwnDriver,paymentStatus,tvPaytmMoney, processingText, generateQR, btnCollect;
        ImageView qrImg;
        LinearLayout distanceLinear, amountCollectedLinear, advancePaidLinear, totalFareLinear,paytmMoneyLinear,
                AmountOwnDriverLinear;

        public CompanionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSource = itemView.findViewById(R.id.textViewSource);
            textViewDestination = itemView.findViewById(R.id.textViewDestination);
            textViewTotalFare = itemView.findViewById(R.id.textViewFare);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPaymentMode = itemView.findViewById(R.id.tvPaymentMode);
            btnCollect = itemView.findViewById(R.id.btnCollect);
            tvAdvancePaid = itemView.findViewById(R.id.tvAdvancePaid);
            tvAmountOwnDriver = itemView.findViewById(R.id.tvAmountOwnDriver);
            amountCollected = itemView.findViewById(R.id.amountCollected);
            tvPaytmMoney = itemView.findViewById(R.id.tvPaytmMoney);
            paytmMoneyLinear = itemView.findViewById(R.id.paytmMoneyLinear);
            AmountOwnDriverLinear = itemView.findViewById(R.id.AmountOwnDriverLinear);
            paymentStatus = itemView.findViewById(R.id.paymentStatus);
            processingText = itemView.findViewById(R.id.processing);
            generateQR = itemView.findViewById(R.id.generateQR);
            qrImg = itemView.findViewById(R.id.qrImg);
            distanceLinear = itemView.findViewById(R.id.distanceLinear);
            amountCollectedLinear = itemView.findViewById(R.id.amountCollectedLinear);
            advancePaidLinear = itemView.findViewById(R.id.advancePaidLinear);
            totalFareLinear = itemView.findViewById(R.id.totalFareLinear);
        }

        @SuppressLint("SetTextI18n")
        public void bind(TripDta driverTrip) {
            if (driverTrip != null) {

                if (driverTrip.getFromAddress() != null) {
                    textViewSource.setText(driverTrip.getFromAddress());
                }
                if (driverTrip.getToAddress() != null) {
                    textViewDestination.setText(driverTrip.getToAddress());
                }

                String date = driverTrip.getEnd_time();
                if (date != null) {
                    String m = null;
                    try {
                        m = DateUtil.getFormattedDate(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    textViewDate.setText(m);
                } else {
                    textViewDate.setText("NA");
                }
                if (driverTrip.getDistance() != null &&driverTrip.getDistance()!=null && !driverTrip.getDistance().equals("0")
                        && !driverTrip.getDistance().equals("NA")) {
                    tvDistance.setText("Distance : " + driverTrip.getDistance() + "KM");
                } else {
                    distanceLinear.setVisibility(View.GONE);
                }
                if (driverTrip.getPrice() != null && driverTrip.getPrice().getTotalPrice()!=null
                        && !String.valueOf(driverTrip.getPrice().getTotalPrice()).equals("0") && !String.valueOf(driverTrip.getPrice().getTotalPrice()).equals("NA")) {
                    textViewTotalFare.setText(" " + driverTrip.getPrice().getTotalPrice());
                } else {
                    totalFareLinear.setVisibility(View.GONE);
                }
                if (driverTrip.getPrice() != null && driverTrip.getPrice().getAmountTobeCollected()!=null
                        && !String.valueOf(driverTrip.getPrice().getAmountTobeCollected()).equals("0") &&
                        !String.valueOf(driverTrip.getPrice().getAmountTobeCollected()).equals("NA")) {
                    amountCollected.setText(" " + driverTrip.getPrice().getAmountTobeCollected());
                } else {
                    amountCollectedLinear.setVisibility(View.GONE);
                }
                if (driverTrip.getPrice() != null && driverTrip.getPrice().getAdvancePaid()!=null
                        && !String.valueOf(driverTrip.getPrice().getAdvancePaid()).equals("0")
                        && !String.valueOf(driverTrip.getPrice().getAdvancePaid()).equals("NA")) {
                    tvAdvancePaid.setText(" " + driverTrip.getPrice().getAdvancePaid());
                } else {
                    advancePaidLinear.setVisibility(View.GONE);
                }
                if (driverTrip.getPrice() != null && driverTrip.getPrice().getPayTMMoney()!=null
                        && !String.valueOf(driverTrip.getPrice().getPayTMMoney()).equals("0") &&
                        !String.valueOf(driverTrip.getPrice().getPayTMMoney()).equals("NA")) {
                    tvPaytmMoney.setText(" " + driverTrip.getPrice().getPayTMMoney());
                } else {
                    paytmMoneyLinear.setVisibility(View.GONE);
                }

                if (driverTrip.getPrice() != null && driverTrip.getPrice().getAmountDriverOwes()!=null
                        && !String.valueOf(driverTrip.getPrice().getAmountDriverOwes()).equals("0") &&
                        !String.valueOf(driverTrip.getPrice().getAmountDriverOwes()).equals("NA")) {
                    tvAmountOwnDriver.setText(" " + driverTrip.getPrice().getAmountDriverOwes());
                } else {
                    AmountOwnDriverLinear.setVisibility(View.GONE);
                }

                if (driverTrip.getStatus() != null) {
                    tvStatus.setText(driverTrip.getStatus());
                } else {
                    tvStatus.setText("NA");
                }
                if (driverTrip.getPaymentMode() != null) {
                    tvPaymentMode.setText("Payment Mode: " + driverTrip.getPaymentMode());
                } else {
                    tvPaymentMode.setText("Payment Mode: NA");
                }
                paymentStatus.setVisibility(View.GONE);
                btnCollect.setVisibility(View.GONE);
                processingText.setVisibility(View.GONE);
                if ((driverTrip.getPaymentMode() != null && driverTrip.getPaymentStatus() != null && driverTrip.getStatus().equals(
                        "completed") && !driverTrip.getPaymentStatus().equals(AppConstants.PAYMENT_COMPLETED))) {
                    btnCollect.setVisibility(View.VISIBLE);
                    paymentStatus.setVisibility(View.VISIBLE);

                    qrImg.setVisibility(View.VISIBLE);
                    paymentStatus.setText(driverTrip.getPaymentStatus());

                    if (driverTrip.getPassengerTripMasterId() != null && !driverTrip.getPassengerTripMasterId().isEmpty()) {
                        String json = SharedPrefsHelper.getInstance().get(AppConstants.QRCODE_DETAIL, "");
                        if (!json.isEmpty()) {
                            QRCode_Detail qrCode_detail = new Gson().fromJson(json, QRCode_Detail.class);

                            if (qrCode_detail.getPassangerTripMasterID().equals(driverTrip.getPassengerTripMasterId())) {
                                byte[] decodedString = Base64.decode(qrCode_detail.getImage().getBytes(StandardCharsets.UTF_8)
                                        , Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                Glide.with(itemView.getContext()).load(bitmap).placeholder(ContextCompat.getDrawable(itemView.getContext(), R.drawable.reload_qr)).into(qrImg);
                                generateQR.setVisibility(View.VISIBLE);
                            } else {
                                generateQR.setVisibility(View.VISIBLE);
                            }
                        } else {
                            generateQR.setVisibility(View.VISIBLE);
                        }
                    }

                    btnCollect.setOnClickListener(view -> {
                        if (driverTrip.getPaymentMode().equals(
                                AppConstants.ONLINE) || driverTrip.getPaymentMode().equals(
                                AppConstants.QR_CODE)) {
                            PaytmModel paytmModel = new PaytmModel();
                            paytmModel.setPassengerTripMasterId(driverTrip.getPassengerTripMasterId());
                            UpdatePaymentStatuss update =
                                    new sgs.env.ecabsdriver.presenter.UpdatePaymentStatus();
                            update.updatePaymentSatusApi(itemView.getContext(), paytmModel, processingText, btnCollect, paymentStatus);

                        } else if (driverTrip.getPaymentMode().equals(
                                AppConstants.CASH)) {
                            UpdatePaymentStatus updatePaymentStatus = new UpdatePaymentStatus();
                            updatePaymentStatus.setPassengerTripMasterId(
                                    driverTrip.getPassengerTripMasterId());
                            updatePaymentStatus.setPaymentStatus(AppConstants.PAYMENT_COMPLETED);
                            updatePaymentStatus.setDriverTripMasterId(driverTrip.getDriverTripMasterId());
                            UpdatePaymentStatuss update =
                                    new sgs.env.ecabsdriver.presenter.UpdatePaymentStatus();
                            update.updatePaymentSatusApiforcash(itemView.getContext(), updatePaymentStatus, processingText, btnCollect, paymentStatus);

                        }
                        btnCollect.setVisibility(View.GONE);
                        processingText.setVisibility(View.VISIBLE);
                        processingText.setText("Processing...");
                    });
                    generateQR.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UpdatePaymentStatuss update =
                                    new sgs.env.ecabsdriver.presenter.UpdatePaymentStatus();
                            if (driverTrip.getPassengerTripMasterId() != null && !driverTrip.getPassengerTripMasterId().isEmpty()) {
                                update.generateQRcode(itemView.getContext(), driverTrip.getPassengerTripMasterId(),
                                        qrImg, generateQR, Double.parseDouble(driverTrip.getPrice().getAmountTobeCollected()));
                            }
                        }
                    });
                } else {
                    tvPaymentMode.setText("Payment Mode : " + driverTrip.getPaymentMode());
                    btnCollect.setVisibility(View.GONE);
                    generateQR.setVisibility(View.GONE);
                    qrImg.setVisibility(View.GONE);
                }
            }
        }

    }
}
