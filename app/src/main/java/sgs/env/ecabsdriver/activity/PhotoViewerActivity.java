package sgs.env.ecabsdriver.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import sgs.env.ecabsdriver.R;

public class PhotoViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Photo View");
        }



        String url = getIntent().getStringExtra("url");

        ImageView imageView = findViewById(R.id.main_image_view);
        final ProgressBar progressBar = findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.VISIBLE);


            displayImageFromUrl(this, url, imageView, new RequestListener() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                            Target target,
                                            boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Object resource, Object model, Target target,
                                               DataSource dataSource, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }
            });

    }

    public static void displayImageFromUrl(final Context context, final String url,
                                           final ImageView imageView, RequestListener listener) {
        RequestOptions myOptions = new RequestOptions()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        if (listener != null) {
            Glide.with(context)
                    .load(url)
                    .apply(myOptions)
                    .listener(listener)
                    .into(imageView);
        } else {
            Glide.with(context)
                    .load(url)
                    .apply(myOptions)
                    .listener(listener)
                    .into(imageView);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}