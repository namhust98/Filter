package com.example.filter.Video;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Aspect 16 : 9 of View
 * Created by sudamasayuki on 2017/05/17.
 */
public class FullScreenVideo extends FrameLayout {

    public FullScreenVideo(@NonNull Context context) {
        super(context);
    }

    public FullScreenVideo(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FullScreenVideo(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
