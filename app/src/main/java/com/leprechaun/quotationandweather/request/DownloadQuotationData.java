package com.leprechaun.quotationandweather.request;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.leprechaun.quotationandweather.QuotationActivity;
import com.leprechaun.quotationandweather.R;
import com.leprechaun.quotationandweather.entity.Quotation;
import com.leprechaun.quotationandweather.entity.QuotationType;
import com.leprechaun.quotationandweather.entity.QuotationValues;
import com.leprechaun.quotationandweather.json.HttpMethod;
import com.leprechaun.quotationandweather.json.JSONParser;
import com.leprechaun.quotationandweather.ui.AdapterQuotationList;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oborghi on 17/03/16.
 */
public class DownloadQuotationData extends AsyncTask<String, Void, List<Quotation>> {

    ProgressDialog dialog;
    QuotationActivity activity;
    Boolean showDialog;

    public DownloadQuotationData(QuotationActivity activity, Boolean showDialog)
    {
        this.showDialog = showDialog;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if(showDialog) {
            dialog = ProgressDialog.show(activity
                    , activity.getResources().getString(R.string.dialog_wait)
                    , activity.getResources().getString(R.string.dialog_wait_message));
        }
    }

    @Override
    protected List<Quotation> doInBackground(String... params) {
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
    protected void onPostExecute(List<Quotation> result)
    {
        super.onPostExecute(result);

        if(showDialog)
            dialog.dismiss();

        if(result != null){
           if(result.size() > 0)
            {
                AdapterQuotationList adapter = new AdapterQuotationList(activity, R.layout.item_list, result);
                activity.getListQuotation().setAdapter(adapter);
            }
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                    .setTitle(R.string.dialog_attention)
                    .setMessage(R.string.dialog_get_quotation_error)
                    .setPositiveButton("OK", null);

            builder.create().show();
        }
    }

    private List<Quotation> getQuotations(JSONObject json) throws JSONException
    {
        List<Quotation> quotations;

        JSONObject bovespaJson = json.getJSONObject(QuotationType.BOVESPA.toString());
        JSONObject dolarJson = json.getJSONObject(QuotationType.DOLAR.toString());
        JSONObject euroJson = json.getJSONObject(QuotationType.EURO.toString());

        Quotation bovespaQuotation = new Quotation();
        bovespaQuotation.setType(QuotationType.BOVESPA);
        bovespaQuotation.setValue(bovespaJson.getDouble(QuotationValues.COTACAO.toString()));
        bovespaQuotation.setVariation(bovespaJson.getDouble(QuotationValues.VARIACAO.toString()));

        Quotation dolarQuotation = new Quotation();
        dolarQuotation.setType(QuotationType.DOLAR);
        dolarQuotation.setValue(dolarJson.getDouble(QuotationValues.COTACAO.toString()));
        dolarQuotation.setVariation(dolarJson.getDouble(QuotationValues.VARIACAO.toString()));

        Quotation euroQuotation = new Quotation();
        euroQuotation.setType(QuotationType.EURO);
        euroQuotation.setValue(euroJson.getDouble(QuotationValues.COTACAO.toString()));
        euroQuotation.setVariation(euroJson.getDouble(QuotationValues.VARIACAO.toString()));

        quotations = new ArrayList<>();

        quotations.add(bovespaQuotation);
        quotations.add(dolarQuotation);
        quotations.add(euroQuotation);

        return quotations;
    }
}
