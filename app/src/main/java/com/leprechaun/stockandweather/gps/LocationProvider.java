package com.leprechaun.stockandweather.gps;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by oborghi on 19/03/16 - 19:12.
 */
public class LocationProvider {

    LocationManager lm;
    LocationResult locationResult;
    GetLastLocation getLastLocation;
    Handler gpsHandler;
    Handler networkHandler;

    boolean gps_enabled=false;
    boolean network_enabled=false;
    boolean gettingLocation=false;
    Context context;

    public LocationProvider(Context context){
        this.context = context;
    }

    public boolean getLocation(LocationResult result)
    {
        //I use LocationResult callback class to pass location value from MyLocation to user code.
        locationResult=result;
        if(lm==null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //exceptions will be thrown if provider is not permitted.
        try{gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ignored){}
        try{network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ignored){}

        //don't start listeners if no provider is enabled
        if(!gps_enabled && !network_enabled)
            return false;

        getLastLocation = new GetLastLocation(context);
        gpsHandler = new Handler();
        networkHandler = new Handler();

        if(gps_enabled && checkPermission(context, permission.ACCESS_FINE_LOCATION))
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        if(network_enabled && checkPermission(context, permission.ACCESS_COARSE_LOCATION))
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);

        return true;
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            if(!gettingLocation) {
                gettingLocation = true;

                if (gps_enabled && checkPermission(context, permission.ACCESS_FINE_LOCATION)) {
                    lm.removeUpdates(this);
                    lm.removeUpdates(locationListenerNetwork);
                }
                locationResult.gotLocation(location);

                //Update location in five minutes interval
                gpsHandler.postDelayed(getLastLocation, 5 * 60 * 1000);

                gettingLocation = false;
            }
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            if(!gettingLocation) {
                gettingLocation = true;

                if (network_enabled && checkPermission(context, permission.ACCESS_COARSE_LOCATION)) {
                    lm.removeUpdates(this);
                    lm.removeUpdates(locationListenerGps);
                }
                locationResult.gotLocation(location);

                //Update location in five minutes interval
                networkHandler.postDelayed(getLastLocation, 5 * 60 * 1000);

                gettingLocation = false;
            }
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    class GetLastLocation implements Runnable {

        Context context;

        public GetLastLocation(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            updateData();
        }
    }

    public void updateData() {
        //exceptions will be thrown if provider is not permitted.
        try{gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ignored){}
        try{network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ignored){}

        if(gps_enabled && checkPermission(context, permission.ACCESS_FINE_LOCATION))
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        if(network_enabled && checkPermission(context, permission.ACCESS_COARSE_LOCATION))
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
    }

    private Boolean checkPermission(Context context,String permission)
    {
        PackageManager pm = context.getPackageManager();
        int hasPerm = pm.checkPermission(
                permission,
                context.getPackageName());
        return (hasPerm == PackageManager.PERMISSION_GRANTED);
    }

    public void cancelBackgroundUpdates() {
        gpsHandler.removeCallbacks(getLastLocation);
        networkHandler.removeCallbacks(getLastLocation);

        if(gps_enabled && checkPermission(context, permission.ACCESS_FINE_LOCATION))
            lm.removeUpdates(locationListenerGps);
        if(network_enabled && checkPermission(context, permission.ACCESS_COARSE_LOCATION))
            lm.removeUpdates(locationListenerNetwork);
    }
}
