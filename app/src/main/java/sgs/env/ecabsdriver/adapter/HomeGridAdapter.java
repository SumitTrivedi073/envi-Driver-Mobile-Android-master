package sgs.env.ecabsdriver.adapter;

import android.content.Context;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;




import java.util.List;

import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.model.Home;


/**
 * Created by Lenovo on 4/17/2018.
 */

public class HomeGridAdapter extends RecyclerView.Adapter<HomeGridAdapter.MyViewHolder> {

    private final Context context;
    private final List<Home> homeList;
    private final boolean nextTrip;
    private OnItemClickListener listener;
    private static final String TAG = "HomeGridAdapter";

    public HomeGridAdapter(Context context, List<Home> homeList,boolean nextTrip,OnItemClickListener clickListener) {
        this.context = context;
        this.homeList = homeList;
        this.nextTrip = nextTrip;

        try {
            listener =  clickListener;
        }
        catch (Exception e){
            Log.d(TAG, "HomeGridAdapter: exception " + e.getMessage());
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_row,parent,false);
        return new HomeGridAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Home home = homeList.get(position);

    /*    if(position == 0 && nextTrip) {
            holder.imageViewNotifcation.setVisibility(View.VISIBLE);
        }
        else {
            holder.imageViewNotifcation.setVisibility(View.GONE);
        }*/

        /*ViewCompat.setTransitionName(holder.imageViewLogo, home.getName());*/

        if(home != null){
            holder.textViewName.setText(home.getName());
            holder.imageViewLogo.setImageResource(home.getImage());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( listener != null){
                    listener.onNextActivity(position,home,holder.imageViewLogo);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return homeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewName;
        private final ImageView imageViewLogo;
        private final ImageView imageViewNotifcation;

        public MyViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.tvDashName);
            imageViewLogo = itemView.findViewById(R.id.ivdashboardImage);
            imageViewNotifcation = itemView.findViewById(R.id.ivNotification);
           }
        }

    public interface OnItemClickListener{
        void onNextActivity(int postion, Home home, ImageView imageViewLogo);
    }
}
