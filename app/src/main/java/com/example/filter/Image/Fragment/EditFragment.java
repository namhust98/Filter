package com.example.filter.Image.Fragment;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.filter.R;


public class EditFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private EditFragmentListener listener;
    private View view;
    private SeekBar brightness, contrast, saturation;

    public void setListener(EditFragmentListener listener) {
        this.listener = listener;
    }

    public EditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.edit_fragment, container, false);


        brightness = view.findViewById(R.id.seekbar_brightness);
        contrast = view.findViewById(R.id.seekbar_contrast);
        saturation = view.findViewById(R.id.seekbar_saturation);

        brightness.setMax(200);
        brightness.setProgress(100);

        contrast.setMax(20);
        contrast.setProgress(0);

        saturation.setMax(30);
        saturation.setProgress(10);

        brightness.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        brightness.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        contrast.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        contrast.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        saturation.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        saturation.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        brightness.setOnSeekBarChangeListener(this);
        contrast.setOnSeekBarChangeListener(this);
        saturation.setOnSeekBarChangeListener(this);
        return view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (listener != null) {

            if (seekBar.getId() == R.id.seekbar_brightness) {
                listener.onBrightnessChanged(progress - 100);
            }

            if (seekBar.getId() == R.id.seekbar_contrast) {
                progress += 10;
                float floatVal = .10f * progress;
                listener.onContrastChanged(floatVal);
            }

            if (seekBar.getId() == R.id.seekbar_saturation) {
                float floatVal = .10f * progress;
                listener.onSaturationChanged(floatVal);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (listener != null)
            listener.onEditStarted();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (listener != null)
            listener.onEditCompleted();
    }

    public void resetControls() {
        brightness.setProgress(100);
        contrast.setProgress(0);
        saturation.setProgress(10);
    }

    public interface EditFragmentListener {
        void onBrightnessChanged(int brightness);

        void onSaturationChanged(float saturation);

        void onContrastChanged(float contrast);

        void onEditStarted();

        void onEditCompleted();
    }
}
