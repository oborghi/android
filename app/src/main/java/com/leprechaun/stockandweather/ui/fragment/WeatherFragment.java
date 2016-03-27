package com.leprechaun.stockandweather.ui.fragment;

import android.annotation.SuppressLint;

import com.leprechaun.stockandweather.entity.Weather;
import com.leprechaun.stockandweather.request.thread.RunnableWeatherData;

public class WeatherFragment extends RunnableFragment {
    private Weather weather;

    public WeatherFragment()
    {
        super();
    }
    @SuppressLint("ValidFragment")
    public WeatherFragment(RunnableWeatherData action) {
        super(action);
    }

    public Weather getWeather() { return weather; }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }
}
