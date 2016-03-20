package com.leprechaun.quotationandweather.entity;

/**
 * Created by oborghi on 19/03/16.
 */
public class Weather {

    private String city;
    private WeatherCurrentCondition currentCondition;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public WeatherCurrentCondition getCurrentCondition() {
        return currentCondition;
    }

    public void setCurrentCondition(WeatherCurrentCondition currentCondition) {
        this.currentCondition = currentCondition;
    }
}
