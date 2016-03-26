package com.leprechaun.stockandweather.ui;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;

import com.leprechaun.stockandweather.entity.Weather;

/**
 * Created by oborghi on 25/03/16 - 21:22.
 */
public class WeatherFragment extends Fragment {
    private Weather weather;
    private TaskControl taskControl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if(taskControl != null && taskControl.getStatus() == TaskStatus.Pending)
            taskControl.executeTask();
    }

    public WeatherFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public WeatherFragment(TaskControl taskControl)
    {
        this.taskControl = taskControl;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }
}
