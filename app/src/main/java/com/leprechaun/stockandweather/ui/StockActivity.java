package com.leprechaun.stockandweather.ui;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.leprechaun.quotationandweather.R;
import com.leprechaun.stockandweather.entity.Stock;
import com.leprechaun.stockandweather.ui.adapters.AdapterStockList;
import com.leprechaun.stockandweather.ui.interfaces.IStockActivity;
import com.leprechaun.stockandweather.ui.fragment.ProgressDialogFragment;
import com.leprechaun.stockandweather.request.thread.RunnableStockData;
import com.leprechaun.stockandweather.ui.fragment.StockFragment;

import java.util.List;

public class StockActivity extends AppCompatActivity implements IStockActivity {

    private static StockActivity instance;
    private final String retained = "retainedStock";

    private final String retainedProcess = "retainedStockProcess";
    private ListView listQuotation;
    private StockFragment retainedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        instance = this;

        listQuotation = (ListView) findViewById(R.id.listQuotation);

        retainedFragment = getRetainedFragment();

        if (retainedFragment == null) {
            retainedFragment = createRetainedFragment();
            setRetainedFragment(retainedFragment);
        } else {
            updateQuotationView(retainedFragment.getStockList());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.stock_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case R.id.menu_item_weather :
                startActivity(new Intent(this, WeatherActivity.class));
                finish();
                return true;
            default:
                return false;
        }
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
    }

    public StockFragment getRetainedFragment() {
        return (StockFragment) getFragmentManager().findFragmentByTag(retained);
    }

    public void setRetainedFragment(StockFragment fragment)
    {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().add(fragment, retained).commit();
    }

    @Override
    public StockFragment getFragment() {
        return retainedFragment;
    }

    @Override
    public void showError()
    {
        closeProcessDialog();
        showToast(R.string.dialog_get_stock_error);
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

    public void showToast(final @StringRes int id)
    {
        Context context = getApplicationContext();
        CharSequence text = getString(id);
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
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
        retainedFragment = new StockFragment(new RunnableStockData(this));
        return retainedFragment;
    }
}
