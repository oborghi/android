package com.leprechaun.stockandweather.request.thread;

import android.os.AsyncTask;

import com.leprechaun.stockandweather.entity.Weather;
import com.leprechaun.stockandweather.request.DownloadLocationData;
import com.leprechaun.stockandweather.ui.interfaces.IWeatherActivity;

public class RunnableWeatherData implements Runnable {

    private IWeatherActivity callbacks;
    private static AsyncTask<Void, Integer, Weather> asyncTask;

    public RunnableWeatherData(IWeatherActivity callbacks)
    {
        this.callbacks = callbacks;
    }

    @Override
    public void run() {
        if(asyncTask != null && asyncTask.getStatus() == AsyncTask.Status.RUNNING)
        {
            asyncTask.cancel(true);
        }

        asyncTask = new DownloadLocationData(callbacks);
        asyncTask.execute();
    }
}
