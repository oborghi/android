package com.leprechaun.stockandweather.request;

import android.os.AsyncTask;
import android.util.Log;

import com.leprechaun.stockandweather.R;
import com.leprechaun.stockandweather.entity.Stock;
import com.leprechaun.stockandweather.entity.StockType;
import com.leprechaun.stockandweather.entity.StockValues;
import com.leprechaun.stockandweather.request.json.HttpMethod;
import com.leprechaun.stockandweather.request.json.JSONParser;
import com.leprechaun.stockandweather.ui.interfaces.IStockActivity;
import com.leprechaun.stockandweather.ui.fragment.StockFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DownloadStockData extends AsyncTask<Void, Integer, List<Stock>> {

    private StockFragment fragment;
    private IStockActivity mCallbacks;


    public DownloadStockData(IStockActivity mCallbacks)
    {
        this.mCallbacks = mCallbacks;
        this.fragment = mCallbacks.getFragment();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mCallbacks.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate();
        mCallbacks.onProgressUpdate(values[0]);
    }

    @Override
    protected void onCancelled() {
        if (mCallbacks != null) {
            mCallbacks.onCancelled();
        }
    }

    @Override
    protected void onPostExecute(List<Stock> result)
    {
        super.onPostExecute(result);
        fragment.setStockList(result);

        if(result == null)
            mCallbacks.showError();

        mCallbacks.onPostExecute();
    }


    @Override
    protected List<Stock> doInBackground(Void... ignored) {
        JSONParser jsonRequest = new JSONParser();

        String urlString = mCallbacks.getResources().getString(R.string.stock_url);

        try {

            JSONObject jsonRoot = jsonRequest.makeHttpRequest(urlString, HttpMethod.POST, null);
            return  getQuotations(jsonRoot);

        } catch (IOException e) {
            Log.e("JSON Request", "Error obtains data " + e.toString());
        } catch (JSONException e) {
            Log.e("JSON Parsing", "Invalid Json data " + e.toString());
        }

        return null;
    }

    private List<Stock> getQuotations(JSONObject json) throws JSONException
    {
        List<Stock> stocks;

        JSONObject bovespaJson = json.getJSONObject(StockType.BOVESPA.toString());
        JSONObject dolarJson = json.getJSONObject(StockType.DOLAR.toString());
        JSONObject euroJson = json.getJSONObject(StockType.EURO.toString());

        Stock bovespaStock = new Stock();
        bovespaStock.setType(StockType.BOVESPA);
        bovespaStock.setValue(bovespaJson.getDouble(StockValues.COTACAO.toString()));
        bovespaStock.setVariation(bovespaJson.getDouble(StockValues.VARIACAO.toString()));

        Stock dolarStock = new Stock();
        dolarStock.setType(StockType.DOLAR);
        dolarStock.setValue(dolarJson.getDouble(StockValues.COTACAO.toString()));
        dolarStock.setVariation(dolarJson.getDouble(StockValues.VARIACAO.toString()));

        Stock euroStock = new Stock();
        euroStock.setType(StockType.EURO);
        euroStock.setValue(euroJson.getDouble(StockValues.COTACAO.toString()));
        euroStock.setVariation(euroJson.getDouble(StockValues.VARIACAO.toString()));

        stocks = new ArrayList<>();

        stocks.add(bovespaStock);
        stocks.add(dolarStock);
        stocks.add(euroStock);

        return stocks;
    }
}
