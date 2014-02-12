package com.dreiri.stolpersteine.components;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class LocationService implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {
	
//	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private static final int REQUEST_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY;
	private static final int REQUEST_INTERVAL = 10000;
	private static final int REQUEST_FASTEST_INTERVAL = 5000;
	
	private LocationClient locationClient;
	private LocationRequest locationRequest;
	private Location currentLocation;
	
	public LocationService(Context context) {
		this.locationClient = new LocationClient(context, this, this);
	}
	
	public void start() {
		locationClient.connect();
	}
	
	public void stop() {
		locationClient.removeLocationUpdates(this);
		locationClient.disconnect();
	}
	
	public Location getCurrentLocation() {
		return currentLocation;
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
//		if (connectionResult.hasResolution()) {
//            try {
//                // Start an Activity that tries to resolve the error
//                connectionResult.startResolutionForResult(context, CONNECTION_FAILURE_RESOLUTION_REQUEST);
//            } catch (IntentSender.SendIntentException e) {
//                // Log the error
//                e.printStackTrace();
//            }
//        } else {
////            showErrorDialog(connectionResult.getErrorCode());
//        }
	}

	@Override
	public void onConnected(Bundle connectionHint) {		
		locationRequest = LocationRequest.create();
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
		Log.i("Stolpersteine", "location: " + location);
		currentLocation = location;
	}

}
