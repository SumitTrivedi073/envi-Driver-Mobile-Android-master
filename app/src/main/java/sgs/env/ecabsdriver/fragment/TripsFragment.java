package sgs.env.ecabsdriver.fragment;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.adapter.TripsAdapter;
import sgs.env.ecabsdriver.model.DriverTrip;


/**
 * A simple {@link Fragment} subclass.
 */
public class TripsFragment extends Fragment {

    private List<DriverTrip> driverTripList;
    private RecyclerView recyclerView;
    private TripsAdapter tripsAdapter;
    private static final String TAG = "TripsFragment";

    public TripsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trips, container, false);

        recyclerView = view.findViewById(R.id.tripsRecylerView);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);


        Log.d(TAG, "onCreateView: " + driverTripList);

/*        tripsAdapter = new TripsAdapter(getActivity(),driverTripList);
        recyclerView.setAdapter(tripsAdapter);*/
        return view;
    }



}
