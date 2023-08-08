package sgs.env.ecabsdriver.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.Objects;

import sgs.env.ecabsdriver.R;

public class MediaPlayerActivity extends AppCompatActivity {

    SimpleExoPlayer player;
    private SimpleExoPlayerView mExoPlayerView;
    private MediaSource mVideoSource;
    private int mResumeWindow;
    private long mResumePosition;
    String VideoURL;
    ImageView video_pause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Video View");


        mExoPlayerView = findViewById(R.id.player_view);
        video_pause = findViewById(R.id.video_pause);
        if (getIntent().getExtras() != null) {
            VideoURL = Objects.requireNonNull(getIntent().getStringExtra("url"));

        }

        /*mExoPlayerView.getVideoSurfaceView().setOnClickListener(view -> {
            Log.e("GET", "clicked!");
            if (player.getPlayWhenReady()) {
                player.setPlayWhenReady(false);
                video_pause.setVisibility(View.VISIBLE);
            } else {
                player.setPlayWhenReady(true);
                video_pause.setVisibility(View.GONE);
            }
        });*/

        ExoplayerLoad(VideoURL);
    }

    private void ExoplayerLoad(String url) {

        Handler mainHandler = new Handler();
        String streamUrl = url;
        String userAgent = Util.getUserAgent(MediaPlayerActivity.this,
                getApplicationContext().getApplicationInfo().packageName);
        DefaultHttpDataSourceFactory httpDataSourceFactory =
                new DefaultHttpDataSourceFactory(userAgent, null,
                        DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                        DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
        DefaultDataSourceFactory dataSourceFactory =
                new DefaultDataSourceFactory(MediaPlayerActivity.this, null,
                        httpDataSourceFactory);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        Uri daUri = Uri.parse(streamUrl);

        mVideoSource =
                new ExtractorMediaSource(daUri, dataSourceFactory, extractorsFactory, mainHandler,
                        null);
        initExoPlayer();

        mExoPlayerView.setUseController(true);


    }

    private void initExoPlayer() {

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this),
                        trackSelector,
                        loadControl);
        mExoPlayerView.setPlayer(player);

        boolean haveResumePosition = mResumeWindow != C.INDEX_UNSET;

        if (haveResumePosition) {
            mExoPlayerView.getPlayer().seekTo(mResumeWindow, mResumePosition);
        }
        player.prepare(mVideoSource);

        mExoPlayerView.getPlayer().setPlayWhenReady(true);



    }

    @Override
    protected void onPause() {
        super.onPause();
        pausePlayer();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void pausePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }



    @Override
    protected void onDestroy() {
        // Very important !
        super.onDestroy();
        destroyPlayer();
    }
    @Override
    public void onBackPressed() {
        destroyPlayer();
        finish();
        super.onBackPressed();
    }

    private void destroyPlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
            player.stop();
            player = null;
        }
    }

}