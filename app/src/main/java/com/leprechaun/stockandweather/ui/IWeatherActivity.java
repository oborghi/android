package com.leprechaun.stockandweather.ui;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by oborghi on 25/03/16 - 21:41.
 */
public interface IWeatherActivity {

    WeatherFragment getFragment();
    Context getContext();
    Resources getResources();
    void onPreExecute();
    void onProgressUpdate(int percent);
    void onCancelled();
    void onPostExecute();
}
