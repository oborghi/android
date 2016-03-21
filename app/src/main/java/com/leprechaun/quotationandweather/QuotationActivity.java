package com.leprechaun.quotationandweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.leprechaun.quotationandweather.request.DownloadQuotationData;

import java.util.Timer;
import java.util.TimerTask;

public class QuotationActivity extends AppCompatActivity {

    private ListView listQuotation;
    private static QuotationActivity currentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listQuotation = (ListView) findViewById(R.id.listQuotation);
        currentActivity = this;

        new DownloadQuotationData(this, false)
                .execute(getResources().getString(R.string.quotation_url));

        backgroundGetQuotation();
    }

    public void showWeather(View view){
        Intent intentCall = new Intent(getApplicationContext(), WeatherActivity.class);
        startActivity(intentCall);
        finish();
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
                new DownloadQuotationData(QuotationActivity.getCurrentActivity(), false)
                        .execute(getResources().getString(R.string.quotation_url));
            }
        }, 0, 5*60*1000);
    }
}
