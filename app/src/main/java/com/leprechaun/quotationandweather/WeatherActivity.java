package com.leprechaun.quotationandweather;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.leprechaun.quotationandweather.entity.Weather;
import com.leprechaun.quotationandweather.gps.LocationProvider;
import com.leprechaun.quotationandweather.gps.LocationResult;
import com.leprechaun.quotationandweather.request.DownloadLocationData;

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
        textCity.setText(city);
    }

    public Weather getLastWeatherUpdate() {
        return lastWeatherUpdate;
    }

    public void setLastWeatherUpdate(Weather lastWeatherUpdate) {
        this.lastWeatherUpdate = lastWeatherUpdate;
    }
}
