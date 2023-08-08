package sgs.env.ecabsdriver.fragment;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.adapter.TripsAdapter;
import sgs.env.ecabsdriver.util.ECabsApp;


/**
 * A simple {@link Fragment} subclass.
 */
public class CanceledFragment extends Fragment {

    private static final String TAG = "CanceledFragment" ;
    private RecyclerView rvTripList;
    private TripsAdapter tripsAdapter;
    private TextView tvHistory;

    public CanceledFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_canceled, container, false);

        rvTripList = view.findViewById(R.id.rvTripList);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvTripList.setLayoutManager(llm);
        tvHistory = view.findViewById(R.id.tvNoHistory);

        if(ECabsApp.isNetworkAvailable(getActivity())) {
//            tripHistoryAPI();
        }
        else {
            Toast.makeText(getActivity(), "No network connectivity!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

/*
    private void tripHistoryAPI() {
        final ProgressBarLayout layout = new ProgressBarLayout();
        layout.displayDialog(getActivity());
        RegisterService registerService = ECabsApp.getRetrofit().create(RegisterService.class);
        String token = SharedPrefsHelper.getInstance().get(AppConstants.KEY_JWT_TOKEN, "");

        if (token != null) {

            Call<TripHistoryResponse> tripHistoryCall = registerService.getTripHistory(token);
            Log.d(TAG, "tripHistoryAPI: url " + tripHistoryCall.request().url());
            tripHistoryCall.enqueue(new Callback<TripHistoryResponse>() {
                @Override
                public void onResponse(Call<TripHistoryResponse> call, Response<TripHistoryResponse> response) {
                    TripHistoryResponse historyResponse = response.body();
                    if (response.isSuccessful() && historyResponse != null) {

                        TripHistoryResponse responseBody = response.body();
                        HistoryContent historyContent = responseBody.getContent();

                        if(historyContent != null) {
                            List<CanceledDetails> bookedDetails = historyContent.getCanceledBookingDetails();
                            try {
                                if (!bookedDetails.isEmpty()) {

                                    tripsAdapter = new TripsAdapter(getActivity(), bookedDetails);
                                    rvTripList.setAdapter(tripsAdapter);
                                }
                            }
                            catch (Exception e) {
                                Log.d(TAG, "onResponse: e" +e.getMessage());
                            }
                        }

                    } else {
                        try {
                            Log.d(TAG, "onResponse: " + response.errorBody().string());
                            tvHistory.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), "No booking found", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "onResponse: no data");
                        } catch (IOException e) {
                            Log.d(TAG, "onResponse: " + e.getMessage());
                        }
                    }
                    layout.hideProgressDialog();
                }

                @Override
                public void onFailure(Call<TripHistoryResponse> call, Throwable t) {
                    layout.hideProgressDialog();
                    Toast.makeText(getActivity(), "No booking found", Toast.LENGTH_LONG).show();
                    tvHistory.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onResponse: no data!");
                }
            });
        }

    }
*/

}
