package com.example.filter.Video.FilterType;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.daasuu.mp4compose.filter.GlBilateralFilter;
import com.daasuu.mp4compose.filter.GlBoxBlurFilter;
import com.daasuu.mp4compose.filter.GlBrightnessFilter;
import com.daasuu.mp4compose.filter.GlCGAColorspaceFilter;
import com.daasuu.mp4compose.filter.GlContrastFilter;
import com.daasuu.mp4compose.filter.GlExposureFilter;
import com.daasuu.mp4compose.filter.GlFilter;
import com.daasuu.mp4compose.filter.GlFilterGroup;
import com.daasuu.mp4compose.filter.GlGammaFilter;
import com.daasuu.mp4compose.filter.GlGaussianBlurFilter;
import com.daasuu.mp4compose.filter.GlGrayScaleFilter;
import com.daasuu.mp4compose.filter.GlHazeFilter;
import com.daasuu.mp4compose.filter.GlHighlightShadowFilter;
import com.daasuu.mp4compose.filter.GlHueFilter;
import com.daasuu.mp4compose.filter.GlInvertFilter;
import com.daasuu.mp4compose.filter.GlLookUpTableFilter;
import com.daasuu.mp4compose.filter.GlLuminanceFilter;
import com.daasuu.mp4compose.filter.GlLuminanceThresholdFilter;
import com.daasuu.mp4compose.filter.GlMonochromeFilter;
import com.daasuu.mp4compose.filter.GlOpacityFilter;
import com.daasuu.mp4compose.filter.GlPixelationFilter;
import com.daasuu.mp4compose.filter.GlPosterizeFilter;
import com.daasuu.mp4compose.filter.GlRGBFilter;
import com.daasuu.mp4compose.filter.GlSaturationFilter;
import com.daasuu.mp4compose.filter.GlSepiaFilter;
import com.daasuu.mp4compose.filter.GlSharpenFilter;
import com.daasuu.mp4compose.filter.GlSolarizeFilter;
import com.daasuu.mp4compose.filter.GlToneCurveFilter;
import com.daasuu.mp4compose.filter.GlToneFilter;
import com.daasuu.mp4compose.filter.GlVibranceFilter;
import com.daasuu.mp4compose.filter.GlVignetteFilter;
import com.daasuu.mp4compose.filter.GlWhiteBalanceFilter;
import com.daasuu.mp4compose.filter.GlZoomBlurFilter;
import com.example.filter.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sudamasayuki on 2017/05/18.
 */

public enum FilterTypeMp4Composer {
    DEFAULT,
    BILATERAL_BLUR,
    BRIGHTNESS,
    FILTER_GROUP_SAMPLE,
    GAMMA,
    GRAY_SCALE,
    HAZE,
    HIGHLIGHT_SHADOW,
    HUE,
    INVERT,
    LUMINANCE,
    MONOCHROME,
    OPACITY,
    RGB,
    SATURATION,
    SEPIA,
    SHARP,
    TONE_CURVE_SAMPLE,
    VIBRANCE,
    VIGNETTE,
    LOOK_UP_TABLE_SAMPLE,
    ZOOM_BLUR,
    ;


    public static List<FilterTypeMp4Composer> createFilterList() {
        return Arrays.asList(FilterTypeMp4Composer.values());
    }

    public static GlFilter createGlFilter(FilterTypeMp4Composer filterType, Context context) {
        switch (filterType) {
            case DEFAULT:
                return new GlFilter();
            case BILATERAL_BLUR:
                return new GlBilateralFilter();
            case BRIGHTNESS:
                GlBrightnessFilter glBrightnessFilter = new GlBrightnessFilter();
                glBrightnessFilter.setBrightness(0.2f);
                return glBrightnessFilter;
            case FILTER_GROUP_SAMPLE:
                return new GlFilterGroup(new GlSepiaFilter(), new GlVignetteFilter());
            case GAMMA:
                GlGammaFilter glGammaFilter = new GlGammaFilter();
                glGammaFilter.setGamma(2f);
                return glGammaFilter;
            case GRAY_SCALE:
                return new GlGrayScaleFilter();
            case HAZE:
                GlHazeFilter glHazeFilter = new GlHazeFilter();
                glHazeFilter.setSlope(-0.5f);
                return glHazeFilter;
            case HIGHLIGHT_SHADOW:
                return new GlHighlightShadowFilter();
            case HUE:
                return new GlHueFilter();
            case INVERT:
                return new GlInvertFilter();
            case LOOK_UP_TABLE_SAMPLE:
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.lookup_sample);
                return new GlLookUpTableFilter(bitmap);
            case LUMINANCE:
                return new GlLuminanceThresholdFilter();
            case MONOCHROME:
                return new GlMonochromeFilter();
            case OPACITY:
                return new GlOpacityFilter();
            case RGB:
                GlRGBFilter glRGBFilter = new GlRGBFilter();
                glRGBFilter.setRed(0f);
                return glRGBFilter;
            case SATURATION:
                return new GlSaturationFilter();
            case SEPIA:
                return new GlSepiaFilter();
            case SHARP:
                GlSharpenFilter glSharpenFilter = new GlSharpenFilter();
                glSharpenFilter.setSharpness(4f);
                return glSharpenFilter;
            case TONE_CURVE_SAMPLE:
                try {
                    InputStream is = context.getAssets().open("acv/tone_cuver_sample.acv");
                    return new GlToneCurveFilter(is);
                } catch (IOException e) {
                    Log.e("FilterTypeMp4Composer", "Error");
                }
                return new GlFilter();
            case VIBRANCE:
                GlVibranceFilter glVibranceFilter = new GlVibranceFilter();
                glVibranceFilter.setVibrance(3f);
                return glVibranceFilter;
            case VIGNETTE:
                return new GlVignetteFilter();
            case ZOOM_BLUR:
                return new GlZoomBlurFilter();
            default:
                return new GlFilter();
        }
    }


}
