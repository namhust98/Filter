package com.example.filter;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.daasuu.epf.EPlayerView;
import com.daasuu.mp4compose.FillMode;
import com.daasuu.mp4compose.composer.Mp4Composer;
import com.daasuu.mp4compose.filter.GlFilter;
import com.daasuu.mp4compose.filter.GlFilterGroup;
import com.daasuu.mp4compose.filter.GlMonochromeFilter;
import com.daasuu.mp4compose.filter.GlVignetteFilter;
import com.example.filter.Video.FilterAdapter;
import com.example.filter.Video.FilterType.FilterTypeEpf;
import com.example.filter.Video.FilterType.FilterTypeMp4Composer;
import com.example.filter.Video.FullVideoActivity;
import com.example.filter.Video.ListVideoActivity;
import com.example.filter.Video.MovieWrapperView;
import com.example.filter.Video.PlayerTimer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

public class VideoActivity extends AppCompatActivity {

    private final List<FilterTypeMp4Composer> filterTypeMp4Composers = FilterTypeMp4Composer.createFilterList();
    private final List<FilterTypeEpf> filterTypeEpfs = FilterTypeEpf.createFilterList();
    private EPlayerView ePlayerView;
    private SimpleExoPlayer player;
    private SeekBar seekBar;
    private PlayerTimer playerTimer;
    private Uri uri;
    private Mp4Composer mp4Composer;
    private boolean issave = true, isplay = true;
    private ListView listView;
    private GlFilter glFilter = new GlFilterGroup(new GlMonochromeFilter(), new GlVignetteFilter());
    private Integer positionfilterTypeFinal = 0;
    private String srcVideopath, destVideopath;
    private LinearLayout progressBar, mainvideo;
    private RelativeLayout title;
    private LinearLayout openvideoTitle, choosevideoTitle;
    private long timeStamp;
    private View videoview;
    private ImageView imageplay, btnsave, btnfullvideo;

    public static void exportMp4ToGallery(Context context, String filePath) {
        final ContentValues values = new ContentValues(2);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, filePath);
        context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                values);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + filePath)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        seekBar = findViewById(R.id.seekBar);
        listView = findViewById(R.id.list);
        btnsave = findViewById(R.id.btnsave);
        progressBar = findViewById(R.id.progressBar);
        title = findViewById(R.id.title);
        mainvideo = findViewById(R.id.mainvideo);
        videoview = findViewById(R.id.videoview);
        imageplay = findViewById(R.id.imageplay);
        openvideoTitle = findViewById(R.id.openVideo_title);
        choosevideoTitle = findViewById(R.id.chooseVideo_title);
        btnfullvideo = findViewById(R.id.btnfullvideo);

        btnfullvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VideoActivity.this, FullVideoActivity.class);
                intent.putExtra("path", uri.toString());
                intent.putExtra("filter", positionfilterTypeFinal);
                startActivity(intent);
            }
        });

        openvideoTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, 10);
                    } else {
                        openCamera();
                    }
                }
            }
        });

        choosevideoTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, 10);
                    } else {
                        chooseVideo();
                    }
                }
            }
        });

        videoview.setOnClickListener(new View.OnClickListener() {
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
                    videoview.setEnabled(false);
                    videoview.setClickable(false);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            imageplay.setVisibility(View.GONE);
                            videoview.setEnabled(true);
                            videoview.setClickable(true);
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

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (issave) {
                    savevideo();
                    issave = false;
                } else {
                    mp4Composer.cancel();
                    issave = true;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.video_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.opencamera) {
            title.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permission, 10);
                } else
                    openCamera();
            }
        } else if (item.getItemId() == R.id.choosevideo) {
            title.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permission, 10);
                } else
                    chooseVideo();
            }
        } else if (item.getItemId() == R.id.useimage) {
            Intent intent = new Intent(VideoActivity.this, ImageActivity.class);
            startActivity(intent);
        }
        return true;
    }

    private String savevideo() {
        destVideopath = "/storage/emulated/0/video/" + System.currentTimeMillis() + ".mp4";
        glFilter = null;
        glFilter = FilterTypeMp4Composer.createGlFilter(filterTypeMp4Composers.get(positionfilterTypeFinal), getApplicationContext());

        mp4Composer = null;
        mp4Composer = new Mp4Composer(srcVideopath, destVideopath)
                // .rotation(Rotation.ROTATION_270)
//                .size(720, 720)
                .fillMode(FillMode.PRESERVE_ASPECT_FIT)
                .filter(glFilter)
                .listener(new Mp4Composer.Listener() {
                    @Override
                    public void onProgress(double progress) {
                        runOnUiThread(() ->
                                progressBar.setVisibility(View.VISIBLE)
                        );
                    }

                    @Override
                    public void onCompleted() {
                        exportMp4ToGallery(getApplicationContext(), destVideopath);
                        runOnUiThread(() -> {
                            Toast.makeText(VideoActivity.this, "Complete", Toast.LENGTH_SHORT).show();
                            issave = true;
                            progressBar.setVisibility(View.GONE);
                        });
                    }

                    @Override
                    public void onCanceled() {
                        runOnUiThread(() -> {
                            Toast.makeText(VideoActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                            issave = true;
                            progressBar.setVisibility(View.GONE);
                        });
                    }

                    @Override
                    public void onFailed(Exception exception) {
                        runOnUiThread(() -> {
                            Toast.makeText(VideoActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            issave = true;
                            progressBar.setVisibility(View.GONE);
                        });
                    }
                })
                .start();
        return destVideopath;
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
                .createMediaSource(uri);

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
        ePlayerView.setGlFilter(FilterTypeEpf.createGlFilter(filterTypeEpfs.get(positionfilterTypeFinal), getApplicationContext()));
        ((MovieWrapperView) findViewById(R.id.layout_movie_wrapper)).addView(ePlayerView);
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
        ((MovieWrapperView) findViewById(R.id.layout_movie_wrapper)).removeAllViews();
        ePlayerView = null;
        player.stop();
        player.release();
        player = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(VideoActivity.this, "Từ chối cấp quyền, vui lòng cấp quyền để ứng dụng có thể truy cập và quay video", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        timeStamp = System.currentTimeMillis();
        values.put(MediaStore.Video.Media.DATA, "/storage/emulated/0/video/" + timeStamp + ".mp4");
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        // Add the date meta data to ensure the image is added at the front of the gallery
        values.put(MediaStore.Video.Media.DATE_ADDED, timeStamp);
        values.put(MediaStore.Video.Media.DATE_TAKEN, timeStamp);
        values.put(MediaStore.Video.Media.ORIENTATION, "0");

        uri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(cameraIntent, 20);
    }

    private void chooseVideo() {
        Intent chooseIntent = new Intent(VideoActivity.this, ListVideoActivity.class);
        startActivityForResult(chooseIntent, 21);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 20) && (resultCode == RESULT_OK)) {
            title.setVisibility(View.GONE);
            mainvideo.setVisibility(View.VISIBLE);
            srcVideopath = "/storage/emulated/0/video/" + timeStamp + ".mp4";
            init();
        } else if ((requestCode == 21) && (resultCode == RESULT_OK)) {
            title.setVisibility(View.GONE);
            mainvideo.setVisibility(View.VISIBLE);
            srcVideopath = data.getStringExtra("result");
            uri = Uri.parse(srcVideopath);
            init();
        }
    }

    protected void init() {
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

        // list
        listView.setAdapter(new FilterAdapter(this, R.layout.row_text, filterTypeEpfs));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ePlayerView.setGlFilter(FilterTypeEpf.createGlFilter(filterTypeEpfs.get(position), getApplicationContext()));
                positionfilterTypeFinal = position;
            }
        });
    }

}