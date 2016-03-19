package com.leprechaun.quotationandweather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.leprechaun.quotationandweather.request.DownloadQuotationData;

import java.util.Timer;
import java.util.TimerTask;

public class QuotationActivity extends AppCompatActivity {

    private ImageButton buttonUpdate;
    private ListView listQuotation;

    private static QuotationActivity quotationActivity;

    public static QuotationActivity getQuotationActivity() {
        return quotationActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonUpdate = (ImageButton) findViewById(R.id.buttonUpdate);
        listQuotation = (ListView) findViewById(R.id.listQuotation);

        quotationActivity = this;

        new DownloadQuotationData(false)
                .execute(getResources().getString(R.string.quotation_url));

        backgroundGetQuotation();
    }

    public void getQuotation(View view)
    {
        new DownloadQuotationData(true)
                .execute(getResources().getString(R.string.quotation_url));
    }

    public ListView getListQuotation() {
        return listQuotation;
    }

    private void backgroundGetQuotation() {
        //Atualiza cotação a cada 5 minutos.
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                new DownloadQuotationData(false)
                        .execute(getResources().getString(R.string.quotation_url));
            }
        }, 0, 5*60*1000);
    }
}
