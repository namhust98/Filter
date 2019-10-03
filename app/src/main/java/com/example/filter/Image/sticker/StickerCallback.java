package com.example.filter.Image.sticker;

import android.graphics.drawable.Drawable;

public interface StickerCallback {

    void onStickerClick(int drawableId);

    void onTextStickerClick(int drawableId);

    void onTextStickerClick();
}
