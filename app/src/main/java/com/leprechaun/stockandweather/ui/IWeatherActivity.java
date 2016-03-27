package com.leprechaun.stockandweather.ui;

import android.content.Context;
import android.content.res.Resources;

public interface IWeatherActivity extends IBackgroundProcessActivity {
    WeatherFragment getFragment();
    Context getContext();
    Resources getResources();
}
