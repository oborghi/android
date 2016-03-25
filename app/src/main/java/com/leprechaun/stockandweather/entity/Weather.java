package com.leprechaun.stockandweather.entity;

import java.util.List;

/**
 * Created by oborghi on 19/03/16.
 */
public class Weather {

    private String city;
    private WeatherCurrentCondition currentCondition;
    private List<WeatherPrevision> previsions;

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

    public List<WeatherPrevision> getPrevisions() {
        return previsions;
    }

    public void setPrevisions(List<WeatherPrevision> previsions) {
        this.previsions = previsions;
    }
}
