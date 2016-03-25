package com.leprechaun.stockandweather.request;

import android.os.AsyncTask;
import android.util.Log;

import com.leprechaun.stockandweather.WeatherActivity;
import com.leprechaun.stockandweather.json.HttpMethod;
import com.leprechaun.stockandweather.json.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oborghi on 20/03/16 - 19:13.
 */
public class DownloadLocationData extends AsyncTask<String, Void, String> {

    private WeatherActivity activity;

    public DownloadLocationData(WeatherActivity activity){
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... params) {
        JSONParser jsonRequest = new JSONParser();

        String urlString = params[0];
        String locality = null;
        String state = null;
        String city = null;

        try {

            JSONObject locationJson = jsonRequest.makeHttpRequest(urlString, HttpMethod.GET, null);

            if(locationJson != null) {
                JSONArray results = locationJson.getJSONArray("results");
                if (results != null) {
                    JSONObject addressJson = results.getJSONObject(0);
                    if (addressJson != null) {
                        JSONArray addressComponentsJson = addressJson.getJSONArray("address_components");

                        for (int componentsCounter = 0; componentsCounter < addressComponentsJson.length(); componentsCounter++) {
                            JSONObject addressComponentJson = addressComponentsJson.getJSONObject(componentsCounter);

                            if (addressComponentJson != null) {
                                JSONArray addressComponentTypesJson = addressComponentJson.getJSONArray("types");

                                List<String> types = new ArrayList<>();

                                for (int typesCounter = 0; typesCounter < addressComponentTypesJson.length(); typesCounter++) {
                                    types.add(addressComponentTypesJson.getString(typesCounter));
                                }

                                if (locality != null & state != null) {
                                    break;
                                }

                                if (types.contains("locality")) {
                                    locality = addressComponentJson.getString("long_name");
                                }

                                if (types.contains("administrative_area_level_1")) {
                                    state = addressComponentJson.getString("short_name");
                                }
                            }
                        }
                    }
                }

                if (locality != null & state != null) {
                    city = String.format("%1$s-%2$s", locality, state);
                }
            }
        } catch (IOException e) {
            Log.e("JSON Request", "Error obtains data " + e.toString());
        } catch (JSONException e) {
            Log.e("JSON Parsing", "Invalid Json data " + e.toString());
        }

        return city;
    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);
        activity.setCity(result);
    }
}
