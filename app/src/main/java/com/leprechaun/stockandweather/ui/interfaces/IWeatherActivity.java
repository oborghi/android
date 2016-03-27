package com.leprechaun.stockandweather.ui.interfaces;

import android.content.Context;
import android.content.res.Resources;

import com.leprechaun.stockandweather.ui.fragment.WeatherFragment;

public interface IWeatherActivity extends IBackgroundProcessActivity {
    WeatherFragment getFragment();
    Context getContext();
    Resources getResources();
    void showError();
}
