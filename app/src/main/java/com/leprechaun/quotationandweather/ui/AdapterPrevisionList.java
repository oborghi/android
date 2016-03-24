package com.leprechaun.quotationandweather.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.leprechaun.quotationandweather.R;
import com.leprechaun.quotationandweather.entity.WeatherPrevision;

import java.util.List;
import java.util.Locale;

/**
 * Created by oborghi on 17/03/16 - 19:12.
 */
public class AdapterPrevisionList extends ArrayAdapter<WeatherPrevision> {

    final Locale brasilLocale = new Locale("pt", "BR");

    public AdapterPrevisionList(Context context, int resource, List<WeatherPrevision> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.item_list_prevision, null);
        }

        WeatherPrevision item = getItem(position);

        if(item != null)
        {
            TextView labelDate = (TextView) v.findViewById(R.id.labelDate);
            TextView labelDescription = (TextView) v.findViewById(R.id.labelDescription);
            TextView textMaxTemperature = (TextView) v.findViewById(R.id.textMaxTemperature);
            TextView textMinTemperature = (TextView) v.findViewById(R.id.textMinTemperature);
            ImageView imagePrevision = (ImageView) v.findViewById(R.id.imagePrevision);

            if(labelDate != null)
                labelDate.setText(item.getDate());

            if(labelDescription != null)
                labelDescription.setText(item.getDescription());

            if(textMaxTemperature != null) {
                textMaxTemperature.setText(String.format(brasilLocale, "%dºC", item.getMaxTemperature()));
            }

            if(textMinTemperature != null) {
                textMinTemperature.setText(String.format(brasilLocale, "%dºC", item.getMinTemperature()));
            }

            if(imagePrevision != null){
                imagePrevision.setImageBitmap(item.getImage());
            }
        }

        return v;
    }
}

