package sgs.env.ecabsdriver.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ProgressBar;

import sgs.env.ecabsdriver.R;


public class ProgressBarLayout extends AppCompatActivity{

    private ProgressDialog mProgressDialog;
    Context mContext;

    public void displayDialog(Context mContext) {
        if (mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mContext);
            if(!((Activity) mContext).isFinishing()) {
                mProgressDialog.show();
            }
            mProgressDialog.setContentView(R.layout.progress_layout);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        }
    }

    public void hideProgressDialog() {

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
            mProgressDialog.dismiss();
            mProgressDialog=null;

        }
    }

    @Override
    protected void onDestroy() {
        hideProgressDialog();
        super.onDestroy();
    }
}
