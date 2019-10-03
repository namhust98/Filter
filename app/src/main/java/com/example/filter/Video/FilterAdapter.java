package com.example.filter.Video;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.example.filter.R;
import com.example.filter.Video.FilterType.FilterTypeEpf;

import java.util.List;

/**
 * Created by sudamasayuki on 2017/05/18.
 */

public class FilterAdapter extends ArrayAdapter<FilterTypeEpf> {

    static class ViewHolder {
        public TextView text;
    }

    private final Context context;
    private final List<FilterTypeEpf> values;

    public FilterAdapter(Context context, int resource, List<FilterTypeEpf> objects) {
        super(context, resource, objects);
        this.context = context;
        values = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_text, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) rowView.findViewById(R.id.label);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        String s = values.get(position).name();
        String str = "";

        switch (s){
            case "DEFAULT": {
                str = "Default";
                break;
            }
            case "BILATERAL_BLUR": {
                str = "Bilateral Blur";
                break;
            }
            case "BRIGHTNESS": {
                str = "Brightness";
                break;
            }
            case "FILTER_GROUP_SAMPLE": {
                str = "Filter Yellow Sample";
                break;
            }
            case "GAMMA": {
                str = "Filter Gamma";
                break;
            }
            case "GRAY_SCALE": {
                str = "Gray Scale";
                break;
            }
            case "HAZE": {
                str = "Haze";
                break;
            }
            case "HIGHLIGHT_SHADOW": {
                str = "Highlight Shadow";
                break;
            }
            case "HUE": {
                str = "Filter Hue";
                break;
            }
            case "INVERT": {
                str = "Filter Invert";
                break;
            }
            case "LUMINANCE": {
                str = "Filter Luminance";
                break;
            }
            case "MONOCHROME": {
                str = "Filter Mono Chrome";
                break;
            }
            case "OPACITY": {
                str = "Filter Opacity";
                break;
            }
            case "RGB": {
                str = "Filter RGB";
                break;
            }
            case "SATURATION": {
                str = "Saturation";
                break;
            }
            case "SEPIA": {
                str = "Filter Sepia";
                break;
            }
            case "SHARP": {
                str = "Filter Sharp";
                break;
            }
            case "TONE_CURVE_SAMPLE": {
                str = "Filter Tone Curve";
                break;
            }
            case "VIBRANCE": {
                str = "Filter Vibrance";
                break;
            }
            case "VIGNETTE": {
                str = "Filter Vignette";
                break;
            }
            case "LOOK_UP_TABLE_SAMPLE": {
                str = "Look Up Table";
                break;
            }
            case "ZOOM_BLUR": {
                str = "Zoom Blur";
                break;
            }
        }
        holder.text.setText(str);

        return rowView;
    }


}
