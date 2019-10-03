package com.example.filter.Video.FilterType;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.daasuu.epf.filter.GlBilateralFilter;
import com.daasuu.epf.filter.GlBoxBlurFilter;
import com.daasuu.epf.filter.GlBrightnessFilter;
import com.daasuu.epf.filter.GlCGAColorspaceFilter;
import com.daasuu.epf.filter.GlContrastFilter;
import com.daasuu.epf.filter.GlExposureFilter;
import com.daasuu.epf.filter.GlFilter;
import com.daasuu.epf.filter.GlFilterGroup;
import com.daasuu.epf.filter.GlGammaFilter;
import com.daasuu.epf.filter.GlGaussianBlurFilter;
import com.daasuu.epf.filter.GlGrayScaleFilter;
import com.daasuu.epf.filter.GlHazeFilter;
import com.daasuu.epf.filter.GlHighlightShadowFilter;
import com.daasuu.epf.filter.GlHueFilter;
import com.daasuu.epf.filter.GlInvertFilter;
import com.daasuu.epf.filter.GlLookUpTableFilter;
import com.daasuu.epf.filter.GlLuminanceFilter;
import com.daasuu.epf.filter.GlLuminanceThresholdFilter;
import com.daasuu.epf.filter.GlMonochromeFilter;
import com.daasuu.epf.filter.GlOpacityFilter;
import com.daasuu.epf.filter.GlPixelationFilter;
import com.daasuu.epf.filter.GlPosterizeFilter;
import com.daasuu.epf.filter.GlRGBFilter;
import com.daasuu.epf.filter.GlSaturationFilter;
import com.daasuu.epf.filter.GlSepiaFilter;
import com.daasuu.epf.filter.GlSharpenFilter;
import com.daasuu.epf.filter.GlSolarizeFilter;
import com.daasuu.epf.filter.GlToneCurveFilter;
import com.daasuu.epf.filter.GlToneFilter;
import com.daasuu.epf.filter.GlVibranceFilter;
import com.daasuu.epf.filter.GlVignetteFilter;
import com.daasuu.epf.filter.GlWhiteBalanceFilter;
import com.daasuu.epf.filter.GlZoomBlurFilter;
import com.example.filter.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sudamasayuki on 2017/05/18.
 */

public enum FilterTypeEpf {
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


    public static List<FilterTypeEpf> createFilterList() {
        return Arrays.asList(FilterTypeEpf.values());
    }

    public static GlFilter createGlFilter(FilterTypeEpf filterType, Context context) {
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
                return new GlLuminanceFilter();
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
                    Log.e("FilterTypeEpf", "Error");
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
