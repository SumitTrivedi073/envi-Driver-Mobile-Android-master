package sgs.env.ecabsdriver.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class SwVersionCheckActivity extends AppCompatActivity {

    TextView okbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sw_version_check);

    okbtn = findViewById(R.id.okbtn);

    okbtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(SharedPrefsHelper.getInstance().get(AppConstants.AppURL))));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(SharedPrefsHelper.getInstance().get(AppConstants.AppURL))));
            }

        }
    });
    }
}