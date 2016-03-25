package com.leprechaun.stockandweather.gps;

import android.location.Location;

/**
 * Created by oborghi on 20/03/16.
 */
public abstract class LocationResult {
    public abstract void gotLocation(Location location);
}
