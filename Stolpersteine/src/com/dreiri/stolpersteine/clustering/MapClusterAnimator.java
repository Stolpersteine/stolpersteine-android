package com.dreiri.stolpersteine.clustering;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public interface MapClusterAnimator {
    
    public void setMap(GoogleMap map);
    public Marker addMarker(MarkerOptions markerOptions);
    public void removeMarker(Marker marker);

}
