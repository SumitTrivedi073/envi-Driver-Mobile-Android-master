package sgs.env.ecabsdriver.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;



import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.activity.MapActivity;
import sgs.env.ecabsdriver.callback.OnAcceptListener;


/**
 * Created by Lenovo on 4/6/2018.
 */

public class AcceptCustomerDialog extends DialogFragment {

    private ImageButton imageButton;
    private OnAcceptListener onAcceptListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog dialog = new Dialog(getActivity(), R.style.Theme_AppCompat_Dialog_Alert);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        }

        int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
        View titleDivider = dialog.findViewById(titleDividerId);

        if (titleDivider != null) {
            titleDivider.setBackgroundColor(getResources().getColor(R.color.white));
        }
        dialog.setContentView(R.layout.accept_customer_dialog);
        ImageButton okButton = dialog.findViewById(R.id.floatingActionButtonAccept);
        dialog.setTitle(R.string.pick_up);


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(getActivity(), MapActivity.class));
                getActivity().finish();
            }
        });
        dialog.show();

        return dialog;
    }

    public void setOnAcceptListener(OnAcceptListener listener) {
        listener = onAcceptListener;
    }
}
