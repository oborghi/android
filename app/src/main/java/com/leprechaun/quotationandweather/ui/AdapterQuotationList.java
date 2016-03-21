package com.leprechaun.quotationandweather.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.leprechaun.quotationandweather.R;
import com.leprechaun.quotationandweather.entity.Quotation;

import java.util.List;
import java.util.Locale;

/**
 * Created by oborghi on 17/03/16.
 */
public class AdapterQuotationList extends ArrayAdapter<Quotation> {

    final Locale brasilLocale = new Locale("pt", "BR");

    public AdapterQuotationList(Context context, int resource, List<Quotation> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.item_list, null);
        }

        Quotation item = getItem(position);

        if(item != null)
        {
            TextView labelQuotationType = (TextView) v.findViewById(R.id.labelQuotationType);
            TextView textQuotationValue = (TextView) v.findViewById(R.id.textQuotationValue);
            TextView textQuotationVariation = (TextView) v.findViewById(R.id.textQuotationVariation);

            if(labelQuotationType != null)
                labelQuotationType.setText(item.getType().capitalize());

            if(textQuotationValue != null)
                textQuotationValue.setText(String.format(brasilLocale, "%1$,.2f", item.getValue()));

            if(textQuotationVariation != null) {

                if(item.getVariation() >= 0) {
                    textQuotationVariation.setTextColor(v.getResources().getColor(R.color.positive_variation));
                } else {
                    textQuotationVariation.setTextColor(v.getResources().getColor(R.color.negative_variation));
                }

                textQuotationVariation.setText(String.format(brasilLocale, "%+,.2f", item.getVariation()));
            }
        }

        return v;
    }
}

