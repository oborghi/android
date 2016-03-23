package com.leprechaun.quotationandweather;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.List;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity {

    private Weather lastWeatherUpdate;
    private TextView textCity;
    private LinearLayout layoutWeatherInfo;
    private LinearLayout layoutLoadingData;
    private TextView labelTempeature;
    private TextView labelDescription;
    private TextView textHumidity;
    private TextView textPressure;
    private TextView textPressureStatus;
    private TextView textVisibility;
    private TextView textSunrise;
    private TextView textSunset;
    private TextView textWindDirection;
    private TextView textWindSpeed;
    private ImageView imageCurrentCondiction;

    final Locale brasilLocale = new Locale("pt", "BR");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        textCity = (TextView) findViewById(R.id.labelCity);
        layoutWeatherInfo = (LinearLayout) findViewById(R.id.layoutWeatherInfo);
        layoutLoadingData = (LinearLayout) findViewById(R.id.layoutLoadingData);
        labelTempeature = (TextView) findViewById(R.id.labelTempeature);
        labelDescription = (TextView) findViewById(R.id.labelDescription);
        textHumidity = (TextView) findViewById(R.id.textHumidity);
        textPressure = (TextView) findViewById(R.id.textPreassure);
        textPressureStatus = (TextView) findViewById(R.id.textPreassureStatus);
        textVisibility = (TextView) findViewById(R.id.textVisibility);
        textSunrise = (TextView) findViewById(R.id.textSunrise);
        textSunset = (TextView) findViewById(R.id.textSunset);
        textWindDirection = (TextView) findViewById(R.id.textWindDirection);
        textWindSpeed = (TextView) findViewById(R.id.textWindSpeed);
        imageCurrentCondiction = (ImageView) findViewById(R.id.imageCurrentCondiction);

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

//    public Weather getLastWeatherUpdate() {
//        return lastWeatherUpdate;
//    }

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
                    for (WeatherPrevision weatherPrevision : weatherPrevisions) {
                        Bitmap previsionImage = images.get(weatherPrevision.getImageUrl());
                        if (previsionImage != null) {
                            weatherPrevision.setImage(previsionImage);
                        }
                    }

                    this.lastWeatherUpdate.setPrevisions(weatherPrevisions);
                }
            }

            if(this.lastWeatherUpdate != null) {

                WeatherCurrentCondition currentCondition = this.lastWeatherUpdate.getCurrentCondition();

                textCity.setText(this.lastWeatherUpdate.getCity());
                labelTempeature.setText(String.format(brasilLocale, "%dºC", currentCondition.getTemperature()));
                labelDescription.setText(currentCondition.getDescription());
                textHumidity.setText(currentCondition.getHumidity());
                textPressure.setText(currentCondition.getPressure());
                textPressureStatus.setText(currentCondition.getPressureStatus());
                textVisibility.setText(currentCondition.getVisibility());
                textSunrise.setText(currentCondition.getSunrise());
                textSunset.setText(currentCondition.getSunset());
                textWindDirection.setText(currentCondition.getWindDirection());
                textWindSpeed.setText(currentCondition.getWindSpeedy());
                imageCurrentCondiction.setImageBitmap(currentCondition.getImage());


                //TODO: Fill interface view with previsions.

                layoutWeatherInfo.setVisibility(View.VISIBLE);
                layoutLoadingData.setVisibility(View.INVISIBLE);
            }
        }
    }
}
