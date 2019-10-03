package com.example.filter.Image.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filter.Image.ListFilter.SampleFilters;
import com.example.filter.ImageActivity;
import com.example.filter.Image.Thumbnails.ThumbnailCallback;
import com.example.filter.Image.Thumbnails.ThumbnailItem;
import com.example.filter.Image.Thumbnails.ThumbnailsAdapter;
import com.example.filter.Image.Thumbnails.ThumbnailsManager;
import com.example.filter.R;

import java.util.List;

public class FilterFragment extends Fragment {
    private View view;
    private RecyclerView thumbListView;
    private Bitmap bitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.filter_fragment, container, false);

        if (ImageActivity.finalbitmap != null) {
            bitmap = ImageActivity.finalbitmap;
        }
        thumbListView = view.findViewById(R.id.thumbnails);

        initHorizontalList();
        return view;
    }

    private void initHorizontalList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.scrollToPosition(0);
        thumbListView.setLayoutManager(layoutManager);
        thumbListView.setHasFixedSize(true);
        bindDataToAdapter();
    }

    private void bindDataToAdapter() {
        final Context context = getContext();
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                ThumbnailsManager.clearThumbs();
                Bitmap thumbImage = bitmap;

                for (int i = 1; i < 15; i++) {
                    ThumbnailItem thumbnailItem = new ThumbnailItem();
                    thumbnailItem.image = thumbImage;
                    addThumb(i, thumbnailItem);
                }

                List<ThumbnailItem> thumbs = ThumbnailsManager.processThumbs(context);

                ThumbnailsAdapter adapter = new ThumbnailsAdapter(thumbs, (ThumbnailCallback) getActivity());
                thumbListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        };
        handler.post(r);
    }

    private void addThumb(int i, ThumbnailItem t) {
        switch (i) {
            case 1:
                break;
            case 2:
                t.filter = SampleFilters.getStarLitFilter();
                break;
            case 3:
                t.filter = SampleFilters.getBlueMessFilter();
                break;
            case 4:
                t.filter = SampleFilters.getAweStruckVibeFilter();
                break;
            case 5:
                t.filter = SampleFilters.getLimeStutterFilter();
                break;
            case 6:
                t.filter = SampleFilters.getNightWhisperFilter();
                break;
            case 7:
                t.filter = SampleFilters.getAmazonFilter();
                break;
            case 8:
                t.filter = SampleFilters.getAdeleFilter();
                break;
            case 9:
                t.filter = SampleFilters.getRiseFilter();
                break;
            case 10:
                t.filter = SampleFilters.getMarsFilter();
                break;
            case 11:
                t.filter = SampleFilters.getAprilFilter();
                break;
            case 12:
                t.filter = SampleFilters.getHaanFilter();
                break;
            case 13:
                t.filter = SampleFilters.getOldManFilter();
                break;
            case 14:
                t.filter = SampleFilters.getClarendon();
                break;
        }
        ThumbnailsManager.addThumb(t);

    }
}
