package com.leprechaun.stockandweather;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.leprechaun.quotationandweather.R;
import com.leprechaun.stockandweather.entity.Stock;
import com.leprechaun.stockandweather.request.DownloadStockData;
import com.leprechaun.stockandweather.ui.AdapterStockList;
import com.leprechaun.stockandweather.ui.IStockActivity;
import com.leprechaun.stockandweather.ui.ProgressDialogFragment;
import com.leprechaun.stockandweather.ui.StockFragment;

import java.util.List;

public class StockActivity extends AppCompatActivity implements IStockActivity {

    private ListView listQuotation;
    private TextView labelWarning;

    //private AlertDialog errorDialog;

    private StockFragment retainedFragment;
    private static StockActivity instance;

    private final String retained = "retainedStock";
    private final String retainedProcess = "retainedStockProcess";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        instance = this;

        listQuotation = (ListView) findViewById(R.id.listQuotation);
        labelWarning = (TextView) findViewById(R.id.labelWarning);

        retainedFragment = getRetainedFragment();

        if (retainedFragment == null) {
            retainedFragment = createRetainedFragment();
            setRetainedFragment(retainedFragment);
        } else {
            updateQuotationView(retainedFragment.getStockList());
        }
    }

    public void showWeather(View view){
        Intent intentCall = new Intent(this, WeatherActivity.class);
        this.startActivity(intentCall);
        finish();
    }

    public void updateQuotationView(List<Stock> result)
    {

        if(result != null){
            if(result.size() > 0)
            {
                AdapterStockList adapter = new AdapterStockList(this, R.layout.item_list_instrument, result);
                instance.listQuotation.setAdapter(adapter);
            }

            closeProcessDialog();
        }

        //TODO: add error dialog fragment
//        else
//        {
//            errorDialog.show();
//        }

        labelWarning.setVisibility(View.VISIBLE);
    }

    public StockFragment getRetainedFragment() {
        return (StockFragment) getFragmentManager().findFragmentByTag(retained);
    }

    @Override
    public StockFragment getFragment() {
        return retainedFragment;
    }

    public void setRetainedFragment(StockFragment fragment)
    {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().add(fragment, retained).commit();
    }

    @Override
    public void onPreExecute() {
        showProcessDialog();
    }

    @Override
    public void onProgressUpdate(int percent) {

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute() {
        StockFragment fragment = getRetainedFragment();
        if (fragment != null)
        {
            updateQuotationView(fragment.getStockList());
        }
    }

    private void showProcessDialog() {
        FragmentManager fm = getFragmentManager();
        ProgressDialogFragment dialogFragment = ProgressDialogFragment.newInstance();
        dialogFragment.show(fm, retainedProcess);
    }

    private ProgressDialogFragment getProcessDialog() {
        return (ProgressDialogFragment) getFragmentManager().findFragmentByTag(retainedProcess);
    }

    public void closeProcessDialog() {
        ProgressDialogFragment dialogFragment = getProcessDialog();

        if (dialogFragment != null) {
            dialogFragment.dismiss();
        }
    }

    private StockFragment createRetainedFragment() {

        final IStockActivity mCallbacks = this;

        Runnable asyncRun = new Runnable() {
            @Override
            public void run() {
                new DownloadStockData(mCallbacks).execute();
            }
        };

        retainedFragment = new StockFragment(asyncRun);

        return retainedFragment;
    }
}
