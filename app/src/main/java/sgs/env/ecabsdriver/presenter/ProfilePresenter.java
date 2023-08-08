package sgs.env.ecabsdriver.presenter;

import static sgs.env.ecabsdriver.util.DriverAlgorithm.logoutfromApp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;


import java.io.IOException;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;
import sgs.env.ecabsdriver.activity.LoginActivity;
import sgs.env.ecabsdriver.interfce.RegisterService;
import sgs.env.ecabsdriver.interfce.GetProfileI;
import sgs.env.ecabsdriver.interfce.UImethodsI;
import sgs.env.ecabsdriver.model.ProfileContent;
import sgs.env.ecabsdriver.model.ProfileResponse;
import sgs.env.ecabsdriver.util.ECabsApp;


public class ProfilePresenter implements GetProfileI {

    private static final String TAG = "ProfilePresenter";

    @Override
    public void getProfileAPI(final Context context, final RatingBar ratingBar, final EditText editTextMailId, final ImageView imageView) {
        final UImethodsI imethodsI = (UImethodsI) context;
        final RegisterService profileData = ECabsApp.getRetrofit().create(RegisterService.class);
        String token = ECabsApp.getInstance().getAcssToken();

        if (!token.equals("")) {
            imethodsI.startProgessBar();
            retrofit2.Call<ProfileResponse> call = profileData.getProfile(token);
            Log.d(TAG, "getProfileAPI: url " + call.request().url());
            call.enqueue(new Callback<ProfileResponse>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                    ProfileResponse profileResponse = response.body();

                    if (response.isSuccessful()  && response.code()==200 && profileResponse!=null) {
                        List<ProfileContent> contentList = profileResponse.getContent();
                        if (!contentList.isEmpty()) {
                            int size = contentList.size();
                            String proPic = contentList.get(size - 1).getPropic();
                            String mailID = contentList.get(size - 1).getMailid();
                            float rating = contentList.get(size - 1).getRating();
                            if (rating != 0) {
                                ratingBar.setRating(rating);
                            }
                            if (!proPic.equals("")) {
                                Glide.with(context)
                                        .load(proPic)
                                        .into(imageView);
                            }
                            editTextMailId.setText(mailID);
                        }
                    } else if(response.code()==400){
                        Toast.makeText(context, "Please try later!", Toast.LENGTH_SHORT).show();
                        try {
                            Log.d(TAG, "onResponse: " + response.errorBody().string());
                        } catch (IOException e) {
                            Log.d(TAG, "onResponse: " + e.getMessage());
                        }
                    }else if(response.code() == 401) {
                        logoutfromApp(context);
                    }
                    imethodsI.endProgressBar();
                }

                @Override
                public void onFailure(retrofit2.Call<ProfileResponse> call, Throwable t) {
                    Toast.makeText(context, "Please try later!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onFailure: " + t.getMessage());
                    imethodsI.endProgressBar();
                }
            });
        }
        else {
            Toast.makeText(context, "Please try later!", Toast.LENGTH_SHORT).show();
        }
    }
    }

