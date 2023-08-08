package sgs.env.ecabsdriver.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.model.CanceledDetails;
import sgs.env.ecabsdriver.util.DriverAlgorithm;



public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.MyViewHolder> {

    List<CanceledDetails> driverTripList;
    private final Context context;
    private static final String TAG = "TripsAdapter";

    public TripsAdapter(Context context, List<CanceledDetails> driverTripList) {
        this.context = context;
        this.driverTripList = driverTripList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drivers_trip_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        CanceledDetails driverTrip = driverTripList.get(position);

        if(driverTrip != null) {
            String fromLat = driverTrip.getFromLat();
            String toLat = driverTrip.getToLat();
            String fromLong = driverTrip.getFromLong();
            String toLong = driverTrip.getToLong();
            String source = DriverAlgorithm.getAddressFromLatLang(context,Double.parseDouble(fromLat),Double.parseDouble(fromLong));
            String destination = DriverAlgorithm.getAddressFromLatLang(context,Double.parseDouble(toLat),Double.parseDouble(toLong));
            String date = driverTrip.getBookedon();
            String fare = driverTrip.getAmount();
            String rate = driverTrip.getRatingByDriver();

            String[] s = date.split("T");
            date = s[0];

          if(source != null && destination != null && date != null
                  && fare != null ) {

              holder.textViewSource.setText(source);
             holder.textViewDestination.setText(destination);
              holder.textViewTotalFare.setText("₹ " + fare);
              holder.textViewDate.setText(date);

          }
        }
    }

    @Override
    public int getItemCount() {
        return driverTripList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewSource;
        private final TextView textViewDestination;
        private final TextView textViewTotalFare;
        private final TextView textViewDate;
        private final TextView tvDistance;

        public MyViewHolder(View itemView) {
            super(itemView);

            textViewSource = itemView.findViewById(R.id.textViewSource);
            textViewDestination = itemView.findViewById(R.id.textViewDestination);
            textViewTotalFare = itemView.findViewById(R.id.textViewFare);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            tvDistance = itemView.findViewById(R.id.tvDistance);
        }
    }
}
