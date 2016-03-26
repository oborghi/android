package com.leprechaun.stockandweather.request;

import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.leprechaun.quotationandweather.R;
import com.leprechaun.stockandweather.entity.Weather;
import com.leprechaun.stockandweather.entity.WeatherCurrentCondition;
import com.leprechaun.stockandweather.entity.WeatherPrevision;
import com.leprechaun.stockandweather.gps.LocationProvider;
import com.leprechaun.stockandweather.gps.LocationResult;
import com.leprechaun.stockandweather.json.HttpMethod;
import com.leprechaun.stockandweather.json.JSONParser;
import com.leprechaun.stockandweather.ui.IWeatherActivity;
import com.leprechaun.stockandweather.ui.WeatherFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by oborghi on 20/03/16 - 19:13.
 */
public class DownloadLocationData extends AsyncTask<Void, Integer, Weather> {

    private LocationProvider provider;
    private LocationResult locationResult;
    private static volatile Location currentLocation;
    private Boolean locationGot = false;
    private Boolean locationServiceDisabled = false;
    private WeatherFragment fragment;
    private IWeatherActivity mCallbacks;

    public DownloadLocationData(IWeatherActivity mCallbacks)
    {
        this.mCallbacks = mCallbacks;
        this.fragment = mCallbacks.getFragment();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mCallbacks.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate();
        mCallbacks.onProgressUpdate(values[0]);
    }

    @Override
    protected void onCancelled() {
        if (mCallbacks != null) {
            mCallbacks.onCancelled();
        }
    }

    @Override
    protected Weather doInBackground(Void... ignored) {

        locationResult = new LocationResult() {
            @Override
            public void gotLocation(Location location) {
                locationGot = true;
                currentLocation = location;
            }
        };

        createLocationProviderHandler();

        try {
            createLocationHandler();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String city = getCity(currentLocation);
        if(city == null)
        {
            return null;
        }

        Weather weather = getWeather(city);

        if(weather != null)
        {
            weather = getWeatherImages(weather);
        }

        return weather;
    }

    private void createLocationProviderHandler() {
        Thread thread = new Thread() {
            public void run() {
                Looper.prepare();

                final Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Do Work

                        provider = new LocationProvider();
                        if(!provider.getLocation(mCallbacks.getContext(), locationResult))
                        {
                            locationServiceDisabled = true;
                        }

                        handler.removeCallbacks(this);
                        //noinspection ConstantConditions
                        Looper.myLooper().quit();
                    }
                });

                Looper.loop();
            }
        };
        thread.start();
    }

    private void createLocationHandler() throws InterruptedException {
        Thread thread = new Thread() {
            public void run() {
                Looper.prepare();
                final int[] maxTries = new int[1];

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do Work
                        if(!locationServiceDisabled && !locationGot && maxTries[0] < 90)
                        {
                            maxTries[0]++;
                            handler.postDelayed(this, 1000);
                        }
                        else
                        {
                            handler.removeCallbacks(this);
                            //noinspection ConstantConditions
                            Looper.myLooper().quit();
                        }
                    }
                }, 1000);

                Looper.loop();
            }
        };
        thread.start();
        thread.join();
    }

    private Weather getWeatherImages(Weather weather) {

        if (weather != null) {
            List<String> imagesUrls = new ArrayList<>();

            if (weather.getCurrentCondition() != null)
                imagesUrls.add(weather.getCurrentCondition().getImageUrl());

            if (weather.getPrevisions() != null)
                for (WeatherPrevision weatherPrevision : weather.getPrevisions()) {
                    imagesUrls.add(weatherPrevision.getImageUrl());
                }

            String[] urls = new String[imagesUrls.size()];
            imagesUrls.toArray(urls);

            HashMap<String,Bitmap> imagesData = null;

            if (urls.length > 0) {
                for (String urlDisplay : urls) {

                    Bitmap mIcon11;
                    Boolean containsImage = false;

                    try {

                        if (imagesData != null) {
                            containsImage = imagesData.containsKey(urlDisplay);
                        }

                        if (!containsImage) {
                            InputStream in = new java.net.URL(urlDisplay).openStream();
                            mIcon11 = BitmapFactory.decodeStream(in);

                            if (imagesData == null)
                                imagesData = new HashMap<>();

                            imagesData.put(urlDisplay, mIcon11);
                        }
                    } catch (Exception e) {
                        Log.e("Image Download", "Error Downloading Image " + e.toString());

                        mIcon11 = BitmapFactory.decodeResource(fragment.getResources(), R.drawable.cannot_be_load);

                        if (imagesData == null)
                            imagesData = new HashMap<>();

                        imagesData.put(urlDisplay, mIcon11);
                    }
                }
            }

            WeatherCurrentCondition weatherCurrentCondition = weather.getCurrentCondition();

            if (weatherCurrentCondition != null) {
                Bitmap currentConditionImage = imagesData != null ? imagesData.get(weatherCurrentCondition.getImageUrl()) : null;

                if (currentConditionImage != null)
                    weatherCurrentCondition.setImage(currentConditionImage);

                weather.setCurrentCondition(weatherCurrentCondition);
            }

            List<WeatherPrevision> weatherPrevisions = weather.getPrevisions();

            if(weatherPrevisions != null){
                for (WeatherPrevision weatherPrevision : weatherPrevisions) {
                    Bitmap previsionImage = imagesData != null ? imagesData.get(weatherPrevision.getImageUrl()) : null;
                    if (previsionImage != null) {
                        weatherPrevision.setImage(previsionImage);
                    }
                }

                weather.setPrevisions(weatherPrevisions);
            }
        }

        return weather;
    }

    @Nullable
    private String getCity(Location location) {
        JSONParser jsonRequest = new JSONParser();

        String locality = null;
        String state = null;
        String city = null;

        try {

            if (location != null) {

                String url = mCallbacks.getResources().getString(R.string.maps_url);
                url = String.format(Locale.ENGLISH, url, location.getLatitude(), location.getLongitude());

                JSONObject locationJson = jsonRequest.makeHttpRequest(url, HttpMethod.GET, null);

                if (locationJson != null) {
                    JSONArray results = locationJson.getJSONArray("results");
                    if (results != null) {
                        JSONObject addressJson = results.getJSONObject(0);
                        if (addressJson != null) {
                            JSONArray addressComponentsJson = addressJson.getJSONArray("address_components");

                            for (int componentsCounter = 0; componentsCounter < addressComponentsJson.length(); componentsCounter++) {
                                JSONObject addressComponentJson = addressComponentsJson.getJSONObject(componentsCounter);

                                if (addressComponentJson != null) {
                                    JSONArray addressComponentTypesJson = addressComponentJson.getJSONArray("types");

                                    List<String> types = new ArrayList<>();

                                    for (int typesCounter = 0; typesCounter < addressComponentTypesJson.length(); typesCounter++) {
                                        types.add(addressComponentTypesJson.getString(typesCounter));
                                    }

                                    if (locality != null & state != null) {
                                        break;
                                    }

                                    if (types.contains("locality")) {
                                        locality = addressComponentJson.getString("long_name");
                                    }

                                    if (types.contains("administrative_area_level_1")) {
                                        state = addressComponentJson.getString("short_name");
                                    }
                                }
                            }
                        }
                    }

                    if (locality != null & state != null) {
                        city = String.format("%1$s-%2$s", locality, state);
                    }
                }
            }
            }catch(IOException e){
                Log.e("JSON Request", "Error obtains data " + e.toString());
            }catch(JSONException e){
                Log.e("JSON Parsing", "Invalid Json data " + e.toString());
            }

        return city;
    }

    public Weather getWeather(String city)
    {
        JSONParser jsonRequest = new JSONParser();

        Weather weather = null;

        try {

            if(city != null) {
                Uri builtUri = Uri.parse(mCallbacks.getResources().getString(R.string.weather_url))
                        .buildUpon()
                        .appendPath(city)
                        .build();

                String url = builtUri.toString();

                JSONObject weatherJson = jsonRequest.makeHttpRequest(url, HttpMethod.POST, null);

                if (weatherJson != null) {
                    weather = new Weather();
                    weather.setCity(weatherJson.getString("cidade"));

                    JSONObject weatherCurrentConditionJson = weatherJson.getJSONObject("agora");

                    if (weatherCurrentConditionJson != null) {
                        WeatherCurrentCondition weatherCurrentCondition = new WeatherCurrentCondition();

                        String dateAndTime = weatherCurrentConditionJson.getString("data_hora");
                        if (dateAndTime != null)
                            weatherCurrentCondition.setDateAndTime(getWeatherDate(dateAndTime));

                        String description = weatherCurrentConditionJson.getString("descricao");
                        if (description != null)
                            weatherCurrentCondition.setDescription(description);

                        Integer temperature = weatherCurrentConditionJson.getInt("temperatura");
                        weatherCurrentCondition.setTemperature(temperature);

                        String humidity = weatherCurrentConditionJson.getString("umidade");
                        if (humidity != null)
                            weatherCurrentCondition.setHumidity(humidity);

                        String visibility = weatherCurrentConditionJson.getString("visibilidade");
                        if (visibility != null)
                            weatherCurrentCondition.setVisibility(visibility);

                        String windSpeedy = weatherCurrentConditionJson.getString("vento_velocidade");
                        if (windSpeedy != null)
                            weatherCurrentCondition.setWindSpeedy(windSpeedy);

                        String windDirection = weatherCurrentConditionJson.getString("vento_direcao");
                        if (windDirection != null)
                            weatherCurrentCondition.setWindDirection(windDirection);

                        String pressure = weatherCurrentConditionJson.getString("pressao");
                        if (pressure != null)
                            weatherCurrentCondition.setPressure(pressure);

                        String pressureStatus = weatherCurrentConditionJson.getString("pressao_status");
                        if (pressureStatus != null)
                            weatherCurrentCondition.setPressureStatus(pressureStatus);

                        String sunrise = weatherCurrentConditionJson.getString("nascer_do_sol");
                        if (sunrise != null)
                            weatherCurrentCondition.setSunrise(sunrise);

                        String sunset = weatherCurrentConditionJson.getString("por_do_sol");
                        if (sunset != null)
                            weatherCurrentCondition.setSunset(sunset);

                        String imageUrl = weatherCurrentConditionJson.getString("imagem");
                        if (imageUrl != null)
                            weatherCurrentCondition.setImageUrl(imageUrl);

                        weather.setCurrentCondition(weatherCurrentCondition);
                    }

                    weather.setPrevisions(getWeatherPrevisions(weatherJson));
                }
            }
        } catch (IOException e) {
            Log.e("JSON Request", "Error obtains data " + e.toString());
        } catch (JSONException e) {
            Log.e("JSON Parsing", "Invalid Json data " + e.toString());
        }

        return weather;
    }

    private List<WeatherPrevision> getWeatherPrevisions(JSONObject objectJson)
    {
        List<WeatherPrevision> previsions = null;

        if(objectJson != null) {
            try {

                JSONArray weatherPrevisionsJson = objectJson.getJSONArray("previsoes");

                if(weatherPrevisionsJson != null) {
                    for (int previsionsCounter = 0; previsionsCounter < weatherPrevisionsJson.length(); previsionsCounter++) {

                        JSONObject weatherPrevisionJson = weatherPrevisionsJson.getJSONObject(previsionsCounter);

                        if (weatherPrevisionJson != null) {
                            WeatherPrevision weatherPrevision = new WeatherPrevision();

                            String date = weatherPrevisionJson.getString("data");
                            if(date != null)
                            {
                                date = date.split("-")[0].trim();
                                weatherPrevision.setDate(date);
                            }

                            String description = weatherPrevisionJson.getString("descricao");
                            if(description != null)
                                weatherPrevision.setDescription(description);

                            String maxTemperature = weatherPrevisionJson.getString("temperatura_max");
                            if(maxTemperature != null)
                                weatherPrevision.setMaxTemperature(Integer.parseInt(maxTemperature));

                            String minTemperature = weatherPrevisionJson.getString("temperatura_min");
                            if(minTemperature != null)
                                weatherPrevision.setMinTemperature(Integer.parseInt(minTemperature));

                            String imageUrl = weatherPrevisionJson.getString("imagem");
                            if(imageUrl != null)
                                weatherPrevision.setImageUrl(imageUrl);

                            if(previsions == null)
                                previsions = new ArrayList<>();

                            previsions.add(weatherPrevision);
                        }
                    }
                }

            } catch (JSONException e) {
                Log.e("JSON Parsing", "Invalid Json data " + e.toString());
            }

        }

        return previsions;
    }

    @NonNull
    private Date getWeatherDate(String dateAndTime) {

        String[] dateAndTimeArray = dateAndTime.split("-");

        String[] dateArray = dateAndTimeArray[0].trim().split("/");
        String[] timeArray = dateAndTimeArray[1].trim().split(":");

        String day = dateArray[0];
        String month = dateArray[1];
        String year = dateArray[2];

        String hour = timeArray[0];
        String minute = timeArray[1];

        Calendar calendar = new GregorianCalendar();

        calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
        calendar.add(Calendar.MONTH, Integer.parseInt(month));
        calendar.add(Calendar.YEAR, Integer.parseInt(year));
        calendar.add(Calendar.HOUR, Integer.parseInt(hour));
        calendar.add(Calendar.MINUTE, Integer.parseInt(minute));

        return calendar.getTime();
    }

    @Override
    protected void onPostExecute(Weather result)
    {
        super.onPostExecute(result);
        fragment.setWeather(result);

        mCallbacks.onPostExecute();
    }
}
