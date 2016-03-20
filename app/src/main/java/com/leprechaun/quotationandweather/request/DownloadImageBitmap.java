package com.leprechaun.quotationandweather.request;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.leprechaun.quotationandweather.R;

import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by oborghi on 19/03/16.
 */
public class DownloadImageBitmap extends AsyncTask<String, Void, HashMap<String,Bitmap>> {

    private Boolean loadError;
    private Activity activity;

    public DownloadImageBitmap(Activity activity){
        this.activity = activity;
    }

    @Override
    protected HashMap<String,Bitmap> doInBackground(String... urls) {
        HashMap<String,Bitmap> imagesData = null;
        try {

            loadError = false;

            for(String urlDisplay : urls)
            {
                Bitmap mIcon11;
                InputStream in = new java.net.URL(urlDisplay).openStream();

                mIcon11 = BitmapFactory.decodeStream(in);

                if(imagesData == null)
                    imagesData = new HashMap<>();

                imagesData.put(urlDisplay, mIcon11);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            loadError = true;
        }

        return imagesData;
    }

    @Override
    protected void onPostExecute(HashMap<String,Bitmap> result) {
        if(loadError)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                    .setTitle(R.string.dialog_attention)
                    .setMessage(R.string.dialog_get_weather_info_error)
                    .setPositiveButton("OK", null);

            builder.create().show();
        }
        else
        {

        }
    }
}
