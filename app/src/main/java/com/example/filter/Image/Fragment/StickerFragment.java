package com.example.filter.Image.Fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.filter.Image.sticker.StickerCallback;
import com.example.filter.ImageActivity;
import com.example.filter.MainActivity;
import com.example.filter.R;

public class StickerFragment extends Fragment{

    private View view;
    private ImageView[] sticker = new ImageView[9];
    private ImageView speaksticker;
    private TextView textsticker;
    private StickerCallback stickerCallback;
    private int i = 0;

    public void setStickerCallback(StickerCallback stickerCallback){
        this.stickerCallback = stickerCallback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sticker_fragment, container, false);

        sticker[0] = view.findViewById(R.id.sticker1);
        sticker[1] = view.findViewById(R.id.sticker2);
        sticker[2] = view.findViewById(R.id.sticker3);
        sticker[3] = view.findViewById(R.id.sticker4);
        sticker[4] = view.findViewById(R.id.sticker5);
        sticker[5] = view.findViewById(R.id.sticker6);
        sticker[6] = view.findViewById(R.id.sticker7);
        sticker[7] = view.findViewById(R.id.sticker8);
        sticker[8] = view.findViewById(R.id.sticker9);
        speaksticker = view.findViewById(R.id.sticker10);
        textsticker = view.findViewById(R.id.sticker11);

        for (i = 0; i < 9; i ++){
            int finalI = i;
            sticker[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stickerCallback.onStickerClick(getDrawable(finalI));
                }
            });
        }

        speaksticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stickerCallback.onTextStickerClick(R.drawable.bubble);
            }
        });

        textsticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stickerCallback.onTextStickerClick();
            }
        });
        return view;
    }

    private int getDrawable(int i){
        int j = 0;
        switch (i){
            case 0: {
                j = R.drawable.a;
                break;
            }
            case 1: {
                j = R.drawable.b;
                break;
            }
            case 2: {
                j = R.drawable.c;
                break;
            }
            case 3: {
                j = R.drawable.d;
                break;
            }
            case 4: {
                j = R.drawable.e;
                break;
            }
            case 5: {
                j = R.drawable.f;
                break;
            }
            case 6: {
                j = R.drawable.g;
                break;
            }
            case 7: {
                j = R.drawable.h;
                break;
            }
            case 8: {
                j = R.drawable.i;
                break;
            }
        }
        return j;
    }
}
