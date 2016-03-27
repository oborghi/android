package com.leprechaun.stockandweather.ui;

import android.annotation.SuppressLint;

import com.leprechaun.stockandweather.entity.Weather;

public class WeatherFragment extends RunnableFragment {
    private Weather weather;

    public WeatherFragment()
    {
        super();
    }
    @SuppressLint("ValidFragment")
    public WeatherFragment(Runnable action) {
        super(action);
    }

    public Weather getWeather() { return weather; }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }
}
