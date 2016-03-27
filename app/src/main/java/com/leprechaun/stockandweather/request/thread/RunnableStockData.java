package com.leprechaun.stockandweather.request.thread;

import android.os.AsyncTask;

import com.leprechaun.stockandweather.entity.Stock;
import com.leprechaun.stockandweather.request.DownloadStockData;
import com.leprechaun.stockandweather.ui.interfaces.IStockActivity;

import java.util.List;

public class RunnableStockData implements Runnable {

    private IStockActivity callbacks;
    private static AsyncTask<Void, Integer, List<Stock>> asyncTask;

    public RunnableStockData(IStockActivity callbacks)
    {
        this.callbacks = callbacks;
    }

    @Override
    public void run() {
        if(asyncTask != null && asyncTask.getStatus() == AsyncTask.Status.RUNNING)
        {
            asyncTask.cancel(true);
        }

        asyncTask = new DownloadStockData(callbacks);
        asyncTask.execute();
    }
}
