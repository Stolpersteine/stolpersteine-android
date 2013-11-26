package com.dreiri.stolpersteine.clustering;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapClusterFadeAnimator implements MapClusterAnimator {
    private GoogleMap map;

    @Override
    public void setMap(GoogleMap map) {
        this.map = map;
    }

    @Override
    public Marker addMarker(MarkerOptions markerOptions) {
        Marker marker = map.addMarker(markerOptions);
        return marker;
    }

    @Override
    public void removeMarker(Marker marker) {
        marker.remove();
    }

}
