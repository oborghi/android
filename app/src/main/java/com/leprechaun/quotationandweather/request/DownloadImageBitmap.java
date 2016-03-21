package com.leprechaun.quotationandweather.request;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.leprechaun.quotationandweather.R;
import com.leprechaun.quotationandweather.WeatherActivity;

import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by oborghi on 19/03/16.
 */
public class DownloadImageBitmap extends AsyncTask<String, Void, HashMap<String,Bitmap>> {

    private WeatherActivity activity;

    public DownloadImageBitmap(WeatherActivity activity){
        this.activity = activity;
    }

    @Override
    protected HashMap<String,Bitmap> doInBackground(String... urls) {
        HashMap<String,Bitmap> imagesData = null;

        if(urls != null && urls.length > 0) {
            for (String urlDisplay : urls) {

                Bitmap mIcon11;
                Boolean containsImage = false;

                try {

                    if(imagesData != null) {
                        containsImage = imagesData.containsKey(urlDisplay);
                    }

                    if(!containsImage) {
                        InputStream in = new java.net.URL(urlDisplay).openStream();
                        mIcon11 = BitmapFactory.decodeStream(in);

                        if (imagesData == null)
                            imagesData = new HashMap<>();

                        imagesData.put(urlDisplay, mIcon11);
                    }
                } catch (Exception e) {
                    Log.e("Image Download", "Error Downloading Image " + e.toString());

                    mIcon11 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.cannot_be_load);

                    if (imagesData == null)
                        imagesData = new HashMap<>();

                    imagesData.put(urlDisplay, mIcon11);
                }
            }
        }

        return imagesData;
    }

    @Override
    protected void onPostExecute(HashMap<String,Bitmap> result) {
        super.onPostExecute(result);
        activity.setLastWeatherImagesUpdate(result);
    }
}
