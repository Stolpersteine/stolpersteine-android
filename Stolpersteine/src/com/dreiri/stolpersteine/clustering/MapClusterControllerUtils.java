package com.dreiri.stolpersteine.clustering;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class MapClusterControllerUtils {
	
	public static LatLngBounds expandBounds(LatLngBounds bounds, double factorLat, double factorLng) {
		double dLat = Math.abs(bounds.northeast.latitude - bounds.southwest.latitude) * factorLat;
		double dLng = Math.abs(bounds.northeast.longitude - bounds.southwest.longitude) * factorLng;
		LatLng southwest = new LatLng(bounds.southwest.latitude - dLat, bounds.southwest.longitude - dLng);
		LatLng northeast = new LatLng(bounds.northeast.latitude + dLat, bounds.northeast.longitude + dLng);
		
		return new LatLngBounds(southwest, northeast);
	}

}
