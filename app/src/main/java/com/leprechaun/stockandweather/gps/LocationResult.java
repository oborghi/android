package com.leprechaun.stockandweather.gps;

import android.location.Location;

public abstract class LocationResult {
    public abstract void gotLocation(Location location);
}
