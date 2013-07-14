package com.dreiri.stolpersteine.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.dreiri.stolpersteine.R;
import com.google.android.gms.maps.model.LatLng;

public class LocationFinder extends Service implements LocationListener {

    boolean isGPSAvailable = false;
    boolean isNetworkAvailable = false;
    boolean positionable = false;
    double lat;
    double lng;
    Location location;
    private static final long MIN_TIME_FOR_UPDATE = 100; // 100 meter
    private static final long MIN_DISTANCE_FOR_UPDATE = 60 * 1000; // 1 minute
    private final Context ctx;
    protected LocationManager locationManager;
    
    public LocationFinder(Context ctx) {
        this.ctx = ctx;
        getLocation();
    }
    
    public Location getLocation() {
        try {
            locationManager = (LocationManager) ctx.getSystemService(LOCATION_SERVICE);
            isGPSAvailable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkAvailable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            
            if (!isGPSAvailable && !isNetworkAvailable) {
                Log.d("Location", "No provider is available");
            } else {
                positionable = true;
                if (isGPSAvailable) {
                    location = getLocationByGPS();
                } else if (isNetworkAvailable) {
                    location = getLocationByNetwork();
                }
                if (location != null) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }
    
    private Location getLocationByProvider(String provider) {
        Location loc = null;
        locationManager.requestLocationUpdates(provider, MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
        if (locationManager != null) {
            loc = locationManager.getLastKnownLocation(provider);
        }
        return loc;
    }
    
    private Location getLocationByGPS() {
        Log.d("Location", "Positioning with GPS");
        return getLocationByProvider(LocationManager.GPS_PROVIDER);
    }
    
    private Location getLocationByNetwork() {
        Log.d("Location", "Positioning with Network");
        return getLocationByProvider(LocationManager.NETWORK_PROVIDER);
    }
    
    private void setMockLocation(LatLng mockPosition) {
        String provider = LocationManager.GPS_PROVIDER;
        locationManager.addTestProvider(provider, false, false, false, false, false, true, true, android.location.Criteria.POWER_LOW, android.location.Criteria.ACCURACY_FINE);
        locationManager.setTestProviderEnabled(provider, true);
        Location mockLocation = new Location(provider);
        mockLocation.setLatitude(mockPosition.latitude);
        mockLocation.setLongitude(mockPosition.longitude);
        mockLocation.setTime(System.currentTimeMillis());
        locationManager.setTestProviderLocation(provider, mockLocation);
    }
    
    public void setMockLocationAndUpdateSelf(LatLng mockPosition) {
        setMockLocation(mockPosition);
        getLocation();
    }
    
    public void removeListener() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }
    
    public boolean isPositionable() {
        return positionable;
    }
    
    public boolean isMockLocationEnabled() {
        if (Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).contentEquals("1")) {
            return true;
        } else {
            return false;
        }
    }

    public double getLat() {
        if (location != null) {
            lat = location.getLatitude();
        }
        return lat;
    }

    public double getLng() {
        if (location != null) {
            lng = location.getLongitude();
        }
        return lng;
    }
    
    public void alertAndGoToGPSSettings() {
        NotificationHelper.alertAndGoToSettings(ctx, R.string.alert_title, R.string.alert_gps_msg, R.string.alert_pos_btn, R.string.alert_neg_btn, Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    }
    
    public void alertAndGoToMockLocationSettings() {
        NotificationHelper.alertAndGoToSettings(ctx, R.string.alert_title, R.string.alert_mock_location_msg, R.string.alert_pos_btn, R.string.alert_neg_btn, Settings.ACTION_SETTINGS);
    }
    
    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}
