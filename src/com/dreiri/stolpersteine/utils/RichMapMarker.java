package com.dreiri.stolpersteine.utils;

import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.maps.model.Marker;

public class RichMapMarker {

    private Map<Marker, Object> association;
    
    public RichMapMarker() {
        this.association = new HashMap<Marker, Object>();
    }
    
    public boolean hasMarker(Marker marker) {
        return this.association.containsKey(marker);
    }
    
    public boolean hasProperty(Object object) {
        return this.association.containsValue(object);
    }
    
    public Object getProperty(Marker marker) {
        return this.association.get(marker);
    }
    
    public void setProperty(Marker marker, Object object) {
        this.association.put(marker, object);
    }

}