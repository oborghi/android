package com.leprechaun.stockandweather;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.leprechaun.quotationandweather.R;
import com.leprechaun.stockandweather.entity.Stock;
import com.leprechaun.stockandweather.request.DownloadStockData;
import com.leprechaun.stockandweather.ui.AdapterQuotationList;
import com.leprechaun.stockandweather.ui.QuotationAndWeatherApp;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StockActivity extends AppCompatActivity {

    private ListView listQuotation;
    private static StockActivity currentActivity;
    private TextView labelWarning;
    private Timer timer;
    private Handler handler;
    private Runnable runnable;

    private static volatile ProgressDialog dialog;
    private AlertDialog errorDialog;

    public static ProgressDialog getDialog() {
        return dialog;
    }

    public static void setDialog(ProgressDialog dialog) {
        StockActivity.dialog = dialog;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        listQuotation = (ListView) findViewById(R.id.listQuotation);
        labelWarning = (TextView) findViewById(R.id.labelWarning);

        currentActivity = this;

        setDialog(ProgressDialog.show(this
                , this.getResources().getString(R.string.dialog_wait)
                , this.getResources().getString(R.string.dialog_wait_message)));

        errorDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_attention)
                .setMessage(R.string.dialog_get_stock_error)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        errorDialog.dismiss();
                    }
                }).create();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (errorDialog.isShowing()) {
                    errorDialog.dismiss();
                }
            }
        };

        errorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });

        handler.postDelayed(runnable, 5000);
    }

    public void showWeather(View view){
        Intent intentCall = new Intent(this, WeatherActivity.class);
        this.startActivity(intentCall);
        finish();
    }

    public void updateQuotationView(List<Stock> result)
    {
        if(getDialog() != null) {
            getDialog().dismiss();
            setDialog(null);
        }

        if(result != null){
            if(result.size() > 0)
            {
                AdapterQuotationList adapter = new AdapterQuotationList(this, R.layout.item_list_instrument, result);
                this.getListQuotation().setAdapter(adapter);
            }
        }
        else
        {
            errorDialog.show();
        }

        labelWarning.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        QuotationAndWeatherApp.activityQuotationResumed();
        backgroundGetQuotation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
        QuotationAndWeatherApp.activityQuotationPaused();
    }

    @Override
    public void onStart() {
        super.onStart();
        QuotationAndWeatherApp.activityQuotationStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
        QuotationAndWeatherApp.activityQuotationStop();
    }

    public ListView getListQuotation() {
        return listQuotation;
    }

    public static StockActivity getCurrentActivity()
    {
        return currentActivity;
    }

    private void backgroundGetQuotation() {
        //Atualiza cotação a cada 5 minutos.

        if(timer != null)
            timer.cancel();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                updateData();
            }
        }, 0, 5*60*1000);
    }

    private void updateData() {
        if(QuotationAndWeatherApp.isActivityQuotationVisible()) {
            getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getDialog() == null) {
                        setDialog(ProgressDialog.show(StockActivity.getCurrentActivity()
                                , StockActivity.getCurrentActivity().getResources().getString(R.string.dialog_wait)
                                , StockActivity.getCurrentActivity().getResources().getString(R.string.dialog_wait_message)));
                    }

                }
            });

            new DownloadStockData(StockActivity.getCurrentActivity())
                    .execute(getResources().getString(R.string.quotation_url));
        }
    }
}
