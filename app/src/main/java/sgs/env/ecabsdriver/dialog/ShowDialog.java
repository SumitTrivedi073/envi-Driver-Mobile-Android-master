package sgs.env.ecabsdriver.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import sgs.env.ecabsdriver.R;


public abstract class ShowDialog extends Dialog implements View.OnClickListener {
    Button ok;
    Context mContext;
    TextView mtitle, mbody;
    String mbodyStr, mTitleStr;

    public ShowDialog(Context context, String title, String body) {
        super(context);
        mContext = context;
        this.mbodyStr = body;
        this.mTitleStr = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_dialog);
        ok = (Button) findViewById(R.id.okBtn);
        mtitle = (TextView) findViewById(R.id.dialogTitle);
        mbody = (TextView) findViewById(R.id.dialogBody);
        mtitle.setText(mTitleStr);
        mbody.setGravity(Gravity.LEFT);
        mbody.setText(mbodyStr);
        ok.setText("Ok");
        ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.okBtn:
                setAction();
                // dismiss();
                break;
        }
    }

    abstract public void setAction();
}
