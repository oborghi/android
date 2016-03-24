package com.leprechaun.quotationandweather;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.leprechaun.quotationandweather.entity.Weather;
import com.leprechaun.quotationandweather.entity.WeatherCurrentCondition;
import com.leprechaun.quotationandweather.entity.WeatherPrevision;
import com.leprechaun.quotationandweather.gps.LocationProvider;
import com.leprechaun.quotationandweather.gps.LocationResult;
import com.leprechaun.quotationandweather.request.DownloadImageBitmap;
import com.leprechaun.quotationandweather.request.DownloadLocationData;
import com.leprechaun.quotationandweather.request.DownloadWeatherData;
import com.leprechaun.quotationandweather.ui.AdapterPrevisionList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity {

    private Weather lastWeatherUpdate;
    private TextView textCity;
    private LinearLayout layoutWeatherInfo;
    private TextView labelTemperature;
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
    private ListView listPrevision;

    private ProgressDialog dialog;
    private final Locale brasilLocale = new Locale("pt", "BR");
    private static WeatherActivity currentActivity;
    private static volatile AlertDialog errorDialog;

    public static WeatherActivity getCurrentActivity() {
        return currentActivity;
    }

    public static AlertDialog getErrorDialog() {
        return errorDialog;
    }

    public static void setErrorDialog(AlertDialog errorDialog) {
        WeatherActivity.errorDialog = errorDialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        currentActivity = this;

        dialog = ProgressDialog.show(this
                , this.getResources().getString(R.string.dialog_wait)
                , this.getResources().getString(R.string.dialog_wait_message));

        textCity = (TextView) findViewById(R.id.labelCity);
        layoutWeatherInfo = (LinearLayout) findViewById(R.id.layoutWeatherInfo);
        labelTemperature = (TextView) findViewById(R.id.labelTempeature);
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
        listPrevision = (ListView) findViewById(R.id.listPrevision);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LocationResult locationResult = new LocationResult(){
            @Override
            public void gotLocation(Location location){
                updateCity(location);
            }
        };
        LocationProvider provider = new LocationProvider(this);

        if(!provider.getLocation(locationResult)){
            ShowDialog(R.string.dialog_get_weather_error);
        }
    }

    public void showQuotation(View view){
        Intent intentCall = new Intent(this, QuotationActivity.class);
        this.startActivity(intentCall);
        finish();
    }

    private void updateCity(Location location) {
        if(dialog == null)
        {
            dialog = ProgressDialog.show(this
                    , this.getResources().getString(R.string.dialog_wait)
                    , this.getResources().getString(R.string.dialog_wait_message));
        }

        if (location != null) {
            String url = getResources().getString(R.string.maps_url, location.getLatitude(), location.getLongitude());
            new DownloadLocationData(this).execute(url);
        }
        else {
            ShowDialog(R.string.dialog_get_weather_error);
        }
    }

    public void setCity(String city)
    {
        if(dialog == null)
        {
            dialog = ProgressDialog.show(this
                    , this.getResources().getString(R.string.dialog_wait)
                    , this.getResources().getString(R.string.dialog_wait_message));
        }

        if(city != null) {
            Uri builtUri = Uri.parse(getResources().getString(R.string.weather_url))
                    .buildUpon()
                    .appendPath(city)
                    .build();

            String url = builtUri.toString();
            new DownloadWeatherData(this).execute(url);
        }
        else
        {
            ShowDialog(R.string.dialog_get_weather_error);
        }
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
        else
        {
            ShowDialog(R.string.dialog_get_weather_info_error);
        }
    }

    private void ShowDialog(final @StringRes int id) {
        getCurrentActivity().runOnUiThread(new Runnable() {
            public void run() {
                if(getErrorDialog() == null) {
                    setErrorDialog(new AlertDialog.Builder(getCurrentActivity())
                            .setTitle(getCurrentActivity().getResources().getString(R.string.dialog_attention))
                            .setMessage(getCurrentActivity().getResources().getString(id))
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    setErrorDialog(null);
                                }
                            }).create());

                    getErrorDialog().show();
                }
            }
        });

        if(dialog != null) {
            dialog.dismiss();
            dialog = null;
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
        }

        if(this.lastWeatherUpdate != null) {

            WeatherCurrentCondition currentCondition = this.lastWeatherUpdate.getCurrentCondition();

            textCity.setText(this.lastWeatherUpdate.getCity());
            labelTemperature.setText(String.format(brasilLocale, "%dºC", currentCondition.getTemperature()));
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

            List<WeatherPrevision> previsions = this.lastWeatherUpdate.getPrevisions();

            if(previsions != null){
                if(previsions.size() > 0)
                {
                    AdapterPrevisionList adapter = new AdapterPrevisionList(this, R.layout.item_list_prevision, previsions);
                    listPrevision.setAdapter(adapter);
                }
            }

            layoutWeatherInfo.setVisibility(View.VISIBLE);
        }

        if(dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
