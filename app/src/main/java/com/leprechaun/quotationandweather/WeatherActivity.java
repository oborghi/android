package com.leprechaun.quotationandweather;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.leprechaun.quotationandweather.entity.Weather;
import com.leprechaun.quotationandweather.entity.WeatherCurrentCondition;
import com.leprechaun.quotationandweather.entity.WeatherPrevision;
import com.leprechaun.quotationandweather.gps.LocationProvider;
import com.leprechaun.quotationandweather.gps.LocationResult;
import com.leprechaun.quotationandweather.request.DownloadImageBitmap;
import com.leprechaun.quotationandweather.request.DownloadLocationData;
import com.leprechaun.quotationandweather.request.DownloadWeatherData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class WeatherActivity extends AppCompatActivity {

    private Weather lastWeatherUpdate;
    private TextView textCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        textCity = (TextView) findViewById(R.id.labelCity);

        LocationResult locationResult = new LocationResult(){
            @Override
            public void gotLocation(Location location){
                updateCity(location);
            }
        };

        LocationProvider provider = new LocationProvider(this);
        provider.getLocation(locationResult);
    }

    public void showQuotation(View view){
        Intent intentCall = new Intent(getApplicationContext(), QuotationActivity.class);
        startActivity(intentCall);
        finish();
    }

    private void updateCity(Location location) {
        if (location != null) {
            String url = getResources().getString(R.string.maps_url, location.getLatitude(), location.getLongitude());
            new DownloadLocationData(this).execute(url);
        }
    }

    public void setCity(String city)
    {
        if(city != null) {
            Uri builtUri = Uri.parse(getResources().getString(R.string.weather_url))
                    .buildUpon()
                    .appendPath(city)
                    .build();

            String url = builtUri.toString();
            new DownloadWeatherData(this).execute(url);
        }
    }

    public Weather getLastWeatherUpdate() {
        return lastWeatherUpdate;
    }

    public void setLastWeatherUpdate(Weather lastWeatherUpdate) {
        if(lastWeatherUpdate != null) {

            List<String> imagesUrls = new ArrayList<>();

            if(lastWeatherUpdate.getCurrentCondition() != null)
                imagesUrls.add(lastWeatherUpdate.getCurrentCondition().getImageUrl());

            if(lastWeatherUpdate.getPrevisions() != null)
                for(WeatherPrevision weatherPrevision : lastWeatherUpdate.getPrevisions())
                {
                    imagesUrls.add(weatherPrevision.getImageUrl());
                }

            String[] imageUrlsArray = new String[imagesUrls.size()];
            imagesUrls.toArray(imageUrlsArray);

            new DownloadImageBitmap(this).execute(imageUrlsArray);

            this.lastWeatherUpdate = lastWeatherUpdate;
        }
    }

    public void setLastWeatherImagesUpdate(HashMap<String,Bitmap> images) {
        if(images != null)
        {
            if(this.lastWeatherUpdate != null) {
                WeatherCurrentCondition weatherCurrentCondition = this.lastWeatherUpdate.getCurrentCondition();

                if (weatherCurrentCondition != null) {
                    Bitmap currentConditionImage = images.get(weatherCurrentCondition.getImageUrl());

                    if (currentConditionImage != null)
                        weatherCurrentCondition.setImage(currentConditionImage);

                    this.lastWeatherUpdate.setCurrentCondition(weatherCurrentCondition);
                }

                List<WeatherPrevision> weatherPrevisions = this.lastWeatherUpdate.getPrevisions();

                if(weatherPrevisions != null){
                    Iterator<WeatherPrevision> weatherPrevisionsIterator = weatherPrevisions.iterator();
                    while (weatherPrevisionsIterator.hasNext())
                    {
                        WeatherPrevision weatherPrevision = weatherPrevisionsIterator.next();

                        Bitmap previsionImage = images.get(weatherPrevision.getImageUrl());
                        if(previsionImage != null)
                        {
                            weatherPrevision.setImage(previsionImage);
                        }
                    }

                    this.lastWeatherUpdate.setPrevisions(weatherPrevisions);
                }
            }

            //TODO: Fill interface view here.
            textCity.setText(this.lastWeatherUpdate.getCity());
        }
    }
}
