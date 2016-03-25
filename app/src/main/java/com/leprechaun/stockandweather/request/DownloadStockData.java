package com.leprechaun.stockandweather.request;

import android.os.AsyncTask;
import android.util.Log;

import com.leprechaun.stockandweather.StockActivity;
import com.leprechaun.stockandweather.entity.Stock;
import com.leprechaun.stockandweather.entity.StockType;
import com.leprechaun.stockandweather.entity.StockValues;
import com.leprechaun.stockandweather.json.HttpMethod;
import com.leprechaun.stockandweather.json.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oborghi on 17/03/16 - 19:13.
 */
public class DownloadStockData extends AsyncTask<String, Void, List<Stock>> {

    StockActivity activity;

    public DownloadStockData(StockActivity activity)
    {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<Stock> doInBackground(String... params) {
        JSONParser jsonRequest = new JSONParser();

        String urlString = params[0];

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

    @Override
    protected void onPostExecute(List<Stock> result)
    {
        super.onPostExecute(result);
        activity.updateQuotationView(result);
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
