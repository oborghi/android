package com.leprechaun.stockandweather.ui;

public interface IBackgroundProcessActivity {
    void onPreExecute();
    void onProgressUpdate(int percent);
    void onCancelled();
    void onPostExecute();
}
