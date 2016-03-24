package com.leprechaun.quotationandweather;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.leprechaun.quotationandweather.entity.Quotation;
import com.leprechaun.quotationandweather.request.DownloadQuotationData;
import com.leprechaun.quotationandweather.ui.AdapterQuotationList;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class QuotationActivity extends AppCompatActivity {

    private ListView listQuotation;
    private static QuotationActivity currentActivity;
    private TextView labelWarning;

    private static volatile ProgressDialog dialog;
    private AlertDialog errorDialog;

    public static ProgressDialog getDialog() {
        return dialog;
    }

    public static void setDialog(ProgressDialog dialog) {
        QuotationActivity.dialog = dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listQuotation = (ListView) findViewById(R.id.listQuotation);
        labelWarning = (TextView) findViewById(R.id.labelWarning);

        currentActivity = this;

        setDialog(ProgressDialog.show(this
                , this.getResources().getString(R.string.dialog_wait)
                , this.getResources().getString(R.string.dialog_wait_message)));

        errorDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_attention)
                .setMessage(R.string.dialog_get_quotation_error)
                .setPositiveButton("OK", null).create();

        backgroundGetQuotation();
    }

    public void showWeather(View view){
        Intent intentCall = new Intent(this, WeatherActivity.class);
        this.startActivity(intentCall);
        finish();
    }

    public void updateQuotationView(List<Quotation> result)
    {
        if(getDialog() != null) {
            getDialog().dismiss();
            setDialog(null);
        }

        if(result != null){
            if(result.size() > 0)
            {
                AdapterQuotationList adapter = new AdapterQuotationList(this, R.layout.item_list, result);
                this.getListQuotation().setAdapter(adapter);
            }
        }
        else
        {
            errorDialog.show();
        }

        labelWarning.setVisibility(View.VISIBLE);
    }

    public ListView getListQuotation() {
        return listQuotation;
    }

    public static QuotationActivity getCurrentActivity()
    {
        return currentActivity;
    }

    private void backgroundGetQuotation() {

        //Atualiza cotação a cada 5 minutos.
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {

                getCurrentActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(getDialog() == null)
                        {
                            setDialog(ProgressDialog.show(QuotationActivity.getCurrentActivity()
                                    , QuotationActivity.getCurrentActivity().getResources().getString(R.string.dialog_wait)
                                    , QuotationActivity.getCurrentActivity().getResources().getString(R.string.dialog_wait_message)));
                        }

                    }
                });

                new DownloadQuotationData(QuotationActivity.getCurrentActivity())
                        .execute(getResources().getString(R.string.quotation_url));

            }
        }, 0, 5*60*1000);
    }
}
