package sgs.env.ecabsdriver.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;

import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.interfce.RateI;
import sgs.env.ecabsdriver.interfce.UImethodsI;
import sgs.env.ecabsdriver.model.CustomerData;
import sgs.env.ecabsdriver.presenter.RatePresenter;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class RatingActivity extends AppCompatActivity implements UImethodsI {

    private RatingBar ratingBar;
    private EditText etMsg;
    private Button submitBtn;
    private ProgressBar progressBar;
    private RateI rateI;
    private CustomerData customerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        ratingBar = findViewById(R.id.ratingBarUser);
        etMsg = findViewById(R.id.feedbckMailBtn);
        progressBar = findViewById(R.id.progressBar);
        submitBtn = findViewById(R.id.submitBtn);

        /*        CustomerNotfication data = new ECabsDriverDbHelper(RatingActivity.this).getRideStatus();
         */
        final String msg = SharedPrefsHelper.getInstance().get(AppConstants.PASS_MST_ID, "");

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final float rate = ratingBar.getRating();
                rateI = new RatePresenter();
                rateI.rateCustomer(RatingActivity.this, rate, msg);
            }
        });

    }

    @Override
    public void startProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void endProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

}
