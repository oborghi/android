package com.leprechaun.stockandweather.request;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.leprechaun.stockandweather.WeatherActivity;
import com.leprechaun.stockandweather.entity.Weather;
import com.leprechaun.stockandweather.entity.WeatherCurrentCondition;
import com.leprechaun.stockandweather.entity.WeatherPrevision;
import com.leprechaun.stockandweather.json.HttpMethod;
import com.leprechaun.stockandweather.json.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by oborghi on 20/03/16 - 19:13.
 */
public class DownloadWeatherData extends AsyncTask<String, Void, Weather> {

    private WeatherActivity activity;

    public DownloadWeatherData(WeatherActivity activity){
        this.activity = activity;
    }

    @Override
    protected Weather doInBackground(String... params) {
        JSONParser jsonRequest = new JSONParser();

        String urlString = params[0];
        Weather weather = null;

        try {

            JSONObject weatherJson = jsonRequest.makeHttpRequest(urlString, HttpMethod.POST, null);

            if(weatherJson != null)
            {
                weather = new Weather();
                weather.setCity(weatherJson.getString("cidade"));

                JSONObject weatherCurrentConditionJson = weatherJson.getJSONObject("agora");

                if(weatherCurrentConditionJson != null)
                {
                    WeatherCurrentCondition weatherCurrentCondition = new WeatherCurrentCondition();

                    String dateAndTime = weatherCurrentConditionJson.getString("data_hora");
                    if(dateAndTime != null)
                        weatherCurrentCondition.setDateAndTime(getWeatherDate(dateAndTime));

                    String description = weatherCurrentConditionJson.getString("descricao");
                    if(description != null)
                        weatherCurrentCondition.setDescription(description);

                    Integer temperature = weatherCurrentConditionJson.getInt("temperatura");
                    weatherCurrentCondition.setTemperature(temperature);

                    String humidity = weatherCurrentConditionJson.getString("umidade");
                    if(humidity != null)
                        weatherCurrentCondition.setHumidity(humidity);

                    String visibility = weatherCurrentConditionJson.getString("visibilidade");
                    if(visibility != null)
                        weatherCurrentCondition.setVisibility(visibility);

                    String windSpeedy = weatherCurrentConditionJson.getString("vento_velocidade");
                    if(windSpeedy != null)
                        weatherCurrentCondition.setWindSpeedy(windSpeedy);

                    String windDirection = weatherCurrentConditionJson.getString("vento_direcao");
                    if(windDirection != null)
                        weatherCurrentCondition.setWindDirection(windDirection);

                    String pressure = weatherCurrentConditionJson.getString("pressao");
                    if(pressure != null)
                        weatherCurrentCondition.setPressure(pressure);

                    String pressureStatus = weatherCurrentConditionJson.getString("pressao_status");
                    if(pressureStatus != null)
                        weatherCurrentCondition.setPressureStatus(pressureStatus);

                    String sunrise = weatherCurrentConditionJson.getString("nascer_do_sol");
                    if(sunrise != null)
                        weatherCurrentCondition.setSunrise(sunrise);

                    String sunset = weatherCurrentConditionJson.getString("por_do_sol");
                    if(sunset != null)
                        weatherCurrentCondition.setSunset(sunset);

                    String imageUrl = weatherCurrentConditionJson.getString("imagem");
                    if(imageUrl != null)
                        weatherCurrentCondition.setImageUrl(imageUrl);

                    weather.setCurrentCondition(weatherCurrentCondition);
                }

                weather.setPrevisions(getWeatherPrevisions(weatherJson));
            }

        } catch (IOException e) {
            Log.e("JSON Request", "Error obtains data " + e.toString());
        } catch (JSONException e) {
            Log.e("JSON Parsing", "Invalid Json data " + e.toString());
        }

        return weather;
    }

    @Override
    protected void onPostExecute(Weather result)
    {
        super.onPostExecute(result);
        activity.setLastWeatherUpdate(result);
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
}
