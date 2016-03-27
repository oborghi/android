package com.leprechaun.stockandweather.ui.interfaces;

public interface IBackgroundProcessActivity {
    void onPreExecute();
    void onProgressUpdate(int percent);
    void onCancelled();
    void onPostExecute();
}
