package com.example.filter.Video;

import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.daasuu.epf.EPlayerView;
import com.example.filter.R;
import com.example.filter.Video.FilterType.FilterTypeEpf;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

public class FullVideoActivity extends AppCompatActivity {

    private EPlayerView ePlayerView;
    private SimpleExoPlayer player;
    private final List<FilterTypeEpf> filterTypeEpfs = FilterTypeEpf.createFilterList();
    private PlayerTimer playerTimer;
    private String path;
    private Integer position;
    private FullScreenVideo fullScreenVideo;
    private SeekBar seekBar;
    private View view;
    private ImageView imageplay;
    private boolean isplay = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_video_activity);

        fullScreenVideo = findViewById(R.id.fullvideo);
        seekBar = findViewById(R.id.seekBarFullvideo);
        view = findViewById(R.id.viewFullVideo);
        imageplay = findViewById(R.id.imageplayfullvideo);

        path = getIntent().getStringExtra("path");
        position = getIntent().getIntExtra("filter", 0);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.getDuration() / 1000 == player.getCurrentPosition() / 1000) {
                    player.seekTo(0);
                } else {
                    if (isplay) {
                        imageplay.setImageResource(R.drawable.ic_pausevideo);
                    } else {
                        imageplay.setImageResource(R.drawable.ic_playvideo);
                    }
                    imageplay.setVisibility(View.VISIBLE);
                    view.setEnabled(false);
                    view.setClickable(false);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            imageplay.setVisibility(View.GONE);
                            view.setEnabled(true);
                            view.setClickable(true);
                        }
                    }, 1000);
                }
            }
        });

        imageplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player == null) return;

                if (isplay) {
                    player.setPlayWhenReady(false);
                    isplay = false;

                } else {
                    player.setPlayWhenReady(true);
                    isplay = true;
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (player == null) return;

                if (!fromUser) {
                    return;
                }

                player.seekTo(progress * 1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // do nothing
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpSimpleExoPlayer();
        setUoGlPlayerView();
        setUpTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer();
        if (playerTimer != null) {
            playerTimer.stop();
            playerTimer.removeMessages(0);
        }
    }

    private void setUpSimpleExoPlayer() {

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "FilterVideo"));

        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
//                .createMediaSource(Uri.parse("http://usuk.keeng.net?ot=video&oid=257134&rt=WP&uid=0"));
                .createMediaSource(Uri.parse(path));

        // SimpleExoPlayer
        player = ExoPlayerFactory.newSimpleInstance(this);
        // Prepare the player with the source.
        player.prepare(videoSource);
        player.setPlayWhenReady(false);

        isplay = false;
    }

    private void setUoGlPlayerView() {
        ePlayerView = new EPlayerView(this);
        ePlayerView.setSimpleExoPlayer(player);
        ePlayerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ePlayerView.setGlFilter(FilterTypeEpf.createGlFilter(filterTypeEpfs.get(position), getApplicationContext()));
        fullScreenVideo.addView(ePlayerView);
        ePlayerView.onResume();
    }

    private void setUpTimer() {
        playerTimer = new PlayerTimer();
        playerTimer.setCallback(new PlayerTimer.Callback() {
            @Override
            public void onTick(long timeMillis) {
                long position = player.getCurrentPosition();
                long duration = player.getDuration();

                if (duration <= 0) return;

                seekBar.setMax((int) duration / 1000);
                seekBar.setProgress((int) position / 1000);
            }
        });
        playerTimer.start();
    }

    private void releasePlayer() {
        ePlayerView.onPause();
        fullScreenVideo.removeAllViews();
        ePlayerView = null;
        player.stop();
        player.release();
        player = null;
    }
}
