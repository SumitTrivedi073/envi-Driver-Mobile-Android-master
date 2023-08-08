package sgs.env.ecabsdriver.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;


import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.activity.MapActivity;
import sgs.env.ecabsdriver.util.PinEntryEditText;


/**
 * Created by Lenovo on 4/6/2018.
 */

public class OtpDialog extends DialogFragment {

    private PinEntryEditText pinEntryEditText;
    private Button buttonVerify;
    private OnRideVerifyListner otpListener;
    private MapActivity mapActivity;
    private String otpNum;
    private static final String TAG = "OtpDialog";
    private TextView textViewError;
    private VerifiedResult verifiedResultInterface;
    private VerifiedResult verifiedResult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.otp_field, null);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        /*frag.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle);*/

            int width = getResources().getDimensionPixelSize(R.dimen.otp_dialog_width);
            int height = getResources().getDimensionPixelSize(R.dimen.otp_dialog_height);
            getDialog().getWindow().setLayout(width, height);

        pinEntryEditText = view.findViewById(R.id.txt_pin_entry);
        buttonVerify = view.findViewById(R.id.verifybtn);
        textViewError = view.findViewById(R.id.textViewError);
        textViewError.setVisibility(View.GONE);

        showKeyBoard();
        otpListener = (OnRideVerifyListner) getActivity();
        checkOTP();

        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buttonVerify.isEnabled()) {
                    buttonVerify.setEnabled(false);
                    checkOTP();
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.dialog_height));
    }

    public interface OnRideVerifyListner{
        void goToGoogleMaps(String otpNum);
    }

    private void checkOTP(){

        pinEntryEditText.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
            @Override
            public void onPinEntered(CharSequence str) {
                otpNum =  str.toString();
                    textViewError.setVisibility(View.GONE);
                    otpListener.goToGoogleMaps(otpNum);

                    if(!buttonVerify.isEnabled()) {
                        buttonVerify.setEnabled(true);
                    }
                    getDialog().dismiss();

               /* else {
                    textViewError.setVisibility(View.GONE);

                    if(!buttonVerify.isEnabled()) {
                        buttonVerify.setEnabled(true);
                    }
                    Toast.makeText(getActivity(), "Wrong Otp", Toast.LENGTH_SHORT).show();
                }*/
            }
        });
    }

    private void hideKeyboard() {
        assert ((InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE)) != null;

        ((InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(pinEntryEditText.getWindowToken(), 0);
    }

    private void showKeyBoard() {
        InputMethodManager imm = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }

    public interface VerifiedResult{
        void verifyOTP(boolean result);
    }
}

