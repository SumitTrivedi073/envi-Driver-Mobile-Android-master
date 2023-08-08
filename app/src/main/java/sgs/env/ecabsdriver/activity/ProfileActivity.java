package sgs.env.ecabsdriver.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.interfce.GetProfileI;
import sgs.env.ecabsdriver.interfce.UImethodsI;
import sgs.env.ecabsdriver.presenter.ProfilePresenter;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.ECabsApp;
import sgs.env.ecabsdriver.util.ProgressBarLayout;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;


public class ProfileActivity extends BaseActivity implements UImethodsI {

    private ImageView imageView,imageViewBack;
    private EditText editTextName,editTextMobileNum,editTextMailId,editTextAddress;
    private RatingBar ratingBar;

    private static final String TAG = "ProfileActivity";
    private ProgressBarLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        layout = new ProgressBarLayout();
        imageView = findViewById( R.id.profilepic);
        ratingBar = findViewById( R.id.profileRating);
        Drawable drawable = ratingBar.getProgressDrawable();
        drawable.setColorFilter(Color.parseColor("#FFC107"),PorterDuff.Mode.SRC_ATOP);
        imageViewBack = findViewById( R.id.imageViewBack);

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // rating bar logic along with viewPager

        editTextAddress = findViewById( R.id.enteradressline1);
        editTextName = findViewById( R.id.enterprofilename);
       editTextMailId = findViewById( R.id.etEmail);
        editTextMobileNum = findViewById(R.id.etEmailId);

        String name = SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_NAME);
        String mobNum = SharedPrefsHelper.getInstance().get(AppConstants.MOBILE_NUM);

        String country = SharedPrefsHelper.getInstance().get(AppConstants.COUNTRY);
        String state = SharedPrefsHelper.getInstance().get(AppConstants.STATE);
        String city = SharedPrefsHelper.getInstance().get(AppConstants.CITY);
        String pincode = SharedPrefsHelper.getInstance().get(AppConstants.PINCODE);
        String address1 = SharedPrefsHelper.getInstance().get(AppConstants.ADDRESS);

        String address = address1 + " " + city + " " + state + " " + country + " " + pincode;
        editTextAddress.setText(address);
        editTextName.setText(name);
        editTextMobileNum.setText(mobNum);

       boolean internet = ECabsApp.isNetworkAvailable(this);
       if(internet){
           GetProfileI profileI = new ProfilePresenter();
           profileI.getProfileAPI(ProfileActivity.this,ratingBar,editTextMailId,imageView);
        }
        else {
           Toast.makeText(this, "No Internet connectivity!", Toast.LENGTH_SHORT).show();
       }
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

    @Override
    public void startProgessBar() {
        layout.displayDialog(this);
    }

    @Override
    public void endProgressBar() {
        if(layout != null)
        layout.hideProgressDialog();
    }
}


