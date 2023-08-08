package sgs.env.ecabsdriver.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.interfce.RegisterService;
import sgs.env.ecabsdriver.adapter.ReadPaginationAdapter;
import sgs.env.ecabsdriver.model.HistoryContent;
import sgs.env.ecabsdriver.model.TripDta;
import sgs.env.ecabsdriver.model.TripHistoryResponse;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookedFragment extends Fragment {
    
    private static final String TAG = "BookedFragment";
    
    private RecyclerView rvTripList;
    
    private static final int PAGE_START = 1;
    
    private ReadPaginationAdapter tripsAdapter;
    
    private TextView tvHistory;
    
    private LinearLayoutManager llm;
    
    private ProgressBar mProgressPage;
    
    List<TripDta> triphistorylist = new ArrayList<>();
    
    private int totalPage;
    
    private int currentPage = 1;
    
    private NestedScrollView nestedscrollview;
    
    public BookedFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_booked, container, false);
        
        rvTripList = view.findViewById(R.id.rvTripList);
        mProgressPage = view.findViewById(R.id.progressTrip);
        tvHistory = view.findViewById(R.id.tvNoHistory);
        nestedscrollview = view.findViewById(R.id.nestedscrollview);
        
        nestedscrollview.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX,
                                       int oldScrollY) {
                // on scroll change we are checking when users scroll as bottom.
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    // in this method we are incrementing page number,
                    // making progress bar visible and calling get data method.
                    currentPage++;
                    if (currentPage <= totalPage) {
                        tripHistoryAPI(currentPage);
                    }
                }
            }
        });
        if (ECabsApp.isNetworkAvailable(getActivity())) {
            mProgressPage.setVisibility(View.VISIBLE);
            tripHistoryAPI(currentPage);
        } else {
            Toast.makeText(getActivity(), "No network connectivity!", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void tripHistoryAPI(final int currentPage) {
        mProgressPage.setVisibility(View.VISIBLE);
        RegisterService registerService = ECabsApp.getRetrofit().create(RegisterService.class);
        String token = SharedPrefsHelper.getInstance().get(AppConstants.KEY_JWT_TOKEN, "");
        String userId = SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, "");

        if (token != null) {

            Call<TripHistoryResponse> tripHistoryCall = registerService.getTripHistory(token, userId, currentPage, 10);
            Log.d(TAG, "tripHistoryAPI: url " + tripHistoryCall.request().url());
            Log.d(TAG, "tripHistoryAPI:  currentPage  " + currentPage);
            Log.d(TAG, "tripHistoryAPI:  method  " + tripHistoryCall.request().method());


            tripHistoryCall.enqueue(new Callback<TripHistoryResponse>() {
                @Override
                public void onResponse(@NonNull Call<TripHistoryResponse> call, @NonNull Response<TripHistoryResponse> response) {
                    mProgressPage.setVisibility(View.GONE);
                    TripHistoryResponse historyResponse = response.body();
    
                    if (response.isSuccessful() && historyResponse != null) {
                        HistoryContent historyContent = historyResponse.getContent();
                        if (historyContent != null) {
                            if (historyResponse.getContent().getResult().size() > 0) {
                                tvHistory.setVisibility(View.GONE);
                                totalPage = historyResponse.getContent().getTotal_pages();

                                List<TripDta> ontripDetailsArrayList = new ArrayList<>(historyResponse.getContent().getResult());
                                triphistorylist.addAll(ontripDetailsArrayList);
                
                                tripsAdapter =
                                        new ReadPaginationAdapter(getActivity(), triphistorylist);
                                rvTripList.setHasFixedSize(true);
                                rvTripList.setAdapter(tripsAdapter);
                            }else {
                                tvHistory.setVisibility(View.VISIBLE);
                            }
                        }else {
                            tvHistory.setVisibility(View.VISIBLE);
                        }
        
                    } else {
                        tvHistory.setVisibility(View.VISIBLE);
                        try {
                            Log.d(TAG, "onResponse: " + response.errorBody().string());
                        } catch (IOException e) {
                            Log.d(TAG, "onResponse: " + e.getMessage());
                        }
                    }

                    mProgressPage.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NonNull Call<TripHistoryResponse> call, @NonNull Throwable t) {
                    tvHistory.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onFailure: " + t.getMessage());
                    mProgressPage.setVisibility(View.GONE);
                }
            });
        }
    }

}
