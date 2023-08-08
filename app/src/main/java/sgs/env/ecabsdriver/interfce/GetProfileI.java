package sgs.env.ecabsdriver.interfce;

import android.content.Context;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

public interface GetProfileI {
    void getProfileAPI(Context context,RatingBar ratingBar, EditText editTextMailId, ImageView imageView);
}
