package com.example.filter.Video;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.filter.R;
import com.example.filter.Video.VideoLoader.VideoItem;
import com.example.filter.Video.VideoLoader.VideoListAdapter;
import com.example.filter.Video.VideoLoader.VideoLoadListener;
import com.example.filter.Video.VideoLoader.VideoLoader;

import org.w3c.dom.Text;

import java.util.List;

public class ListVideoActivity extends AppCompatActivity {

    private VideoItem videoItem;
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_video);

        title = findViewById(R.id.titleListVideo);

        VideoLoader videoLoader = new VideoLoader(getApplicationContext());
        videoLoader.loadDeviceVideos(new VideoLoadListener() {
            @Override
            public void onVideoLoaded(final List<VideoItem> items) {

                ListView lv = findViewById(R.id.listvideo);
                VideoListAdapter adapter = new VideoListAdapter(getApplicationContext(), R.layout.row_video_list, items);
                if (items.size() == 0) {
                    title.setVisibility(View.VISIBLE);
                }
                lv.setAdapter(adapter);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        videoItem = null;
                        videoItem = items.get(position);
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result",videoItem.getPath());
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                    }
                });
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
