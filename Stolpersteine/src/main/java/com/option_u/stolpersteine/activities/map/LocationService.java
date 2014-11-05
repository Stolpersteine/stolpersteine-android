package com.option_u.stolpersteine.activities.map;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class LocationService implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

    // private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int REQUEST_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY;
    private static final int REQUEST_INTERVAL = 10000;
    private static final int REQUEST_FASTEST_INTERVAL = 5000;

    private GoogleMap map;
    private CameraUpdate region;
    private LocationClient locationClient;
    private Location currentLocation;

    public LocationService(Context context, GoogleMap map, CameraUpdate region) {
        this.map = map;
        this.locationClient = new LocationClient(context, this, this);
        this.region = region;
        zoomToRegion(false);
    }

    public void start() {
        locationClient.connect();
    }

    public void stop() {
        if (locationClient.isConnecting() || locationClient.isConnected()) {
            locationClient.removeLocationUpdates(this);
            locationClient.disconnect();
        }
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    @SuppressWarnings("SameParameterValue")
    public boolean zoomToCurrentLocation(float zoom, boolean animated) {
        Location location = getCurrentLocation();
        boolean hasLocation = (location != null);
        if (hasLocation) {
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate region = CameraUpdateFactory.newLatLngZoom(currentLocation, zoom);
            if (animated) {
                map.animateCamera(region);
            } else {
                map.moveCamera(region);
            }
        }

        return hasLocation;
    }

    public void zoomToRegion(boolean animated) {
        if (animated) {
            map.animateCamera(region);
        } else {
            map.moveCamera(region);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // if (connectionResult.hasResolution()) {
        // try {
        // // Start an Activity that tries to resolve the error
        // connectionResult.startResolutionForResult(context, CONNECTION_FAILURE_RESOLUTION_REQUEST);
        // } catch (IntentSender.SendIntentException e) {
        // // Log the error
        // e.printStackTrace();
        // }
        // } else {
        // // showErrorDialog(connectionResult.getErrorCode());
        // }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(REQUEST_PRIORITY);
        locationRequest.setInterval(REQUEST_INTERVAL);
        locationRequest.setFastestInterval(REQUEST_FASTEST_INTERVAL);
        locationClient.requestLocationUpdates(locationRequest, this);
    }

    @Override
    public void onDisconnected() {
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
    }

}
