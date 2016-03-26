package com.leprechaun.stockandweather.ui;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.leprechaun.quotationandweather.R;
import com.leprechaun.stockandweather.entity.Stock;

import java.util.List;
import java.util.Locale;

/**
 * Created by oborghi on 17/03/16 - 19:12 - 02:30.
 */
public class AdapterStockList extends ArrayAdapter<Stock> {

    final Locale brazilLocale = new Locale("pt", "BR");
    private Context context;

    public AdapterStockList(Context context, int resource, List<Stock> items) {
        super(context, resource, items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.item_list_instrument, null);
        }

        Stock item = getItem(position);

        if(item != null)
        {
            TextView labelQuotationType = (TextView) v.findViewById(R.id.labelQuotationType);
            TextView textQuotationValue = (TextView) v.findViewById(R.id.textQuotationValue);
            TextView textQuotationVariation = (TextView) v.findViewById(R.id.textQuotationVariation);

            if(labelQuotationType != null)
                labelQuotationType.setText(item.getType().capitalize());

            if(textQuotationValue != null)
                textQuotationValue.setText(String.format(brazilLocale, "%1$,.2f", item.getValue()));

            if(textQuotationVariation != null) {

                if(item.getVariation() >= 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        textQuotationVariation.setTextColor(v.getResources().getColor(R.color.positive_variation, context.getTheme()));
                    }
                    else
                    {
                        textQuotationVariation.setTextColor(v.getResources().getColor(R.color.positive_variation));
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        textQuotationVariation.setTextColor(v.getResources().getColor(R.color.negative_variation, context.getTheme()));
                    }
                    else
                    {
                        textQuotationVariation.setTextColor(v.getResources().getColor(R.color.negative_variation));
                    }
                }

                textQuotationVariation.setText(String.format(brazilLocale, "%+,.2f%%", item.getVariation()));
            }
        }

        return v;
    }
}

