package com.leprechaun.stockandweather.ui;

import android.app.Application;

/**
 * Created by oborghi on 24/03/16 - 11:08.
 */
public class StockAndWeatherApp extends Application {

//    private static boolean activityWeatherVisible;
    private static boolean activityQuotationVisible;

//    public static boolean isActivityWeatherVisible() {
//        return activityWeatherVisible;
//    }

//    public static void activityWeatherResumed() {
//        activityWeatherVisible = true;
//    }
//
//    public static void activityWeatherPaused() {
//        activityWeatherVisible = false;
//    }
//
//    public static void activityWeatherStart() {
//        activityWeatherVisible = true;
//    }
//
//    public static void activityWeatherStop() {
//        activityWeatherVisible = false;
//    }

    public static boolean isActivityQuotationVisible() {
        return activityQuotationVisible;
    }

    public static void activityQuotationResumed() {
        activityQuotationVisible = true;
    }

    public static void activityQuotationPaused() {
        activityQuotationVisible = false;
    }

    public static void activityQuotationStart() {
        activityQuotationVisible = true;
    }

    public static void activityQuotationStop() {
        activityQuotationVisible = false;
    }
}
