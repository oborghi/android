package com.leprechaun.quotationandweather.entity;

import android.graphics.Bitmap;

/**
 * Created by oborghi on 19/03/16.
 */
public class WeatherPrevision {

    private String date;
    private String description;
    private int maxTempeature;
    private int minTempeature;
    private String imageUrl;
    private Bitmap image;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxTempeature() {
        return maxTempeature;
    }

    public void setMaxTempeature(int maxTempeature) {
        this.maxTempeature = maxTempeature;
    }

    public int getMinTempeature() {
        return minTempeature;
    }

    public void setMinTempeature(int minTempeature) {
        this.minTempeature = minTempeature;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
