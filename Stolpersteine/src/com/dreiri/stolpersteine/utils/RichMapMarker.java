package com.dreiri.stolpersteine.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.maps.model.Marker;

public class RichMapMarker {

    private Map<Marker, ArrayList<Object>> associations;
    
    public RichMapMarker() {
        this.associations = new HashMap<Marker, ArrayList<Object>>();
    }
    
    public boolean isEmpty(Marker marker) {
        return this.associations.get(marker).isEmpty();
    }
    
    public boolean hasOneProperty(Marker marker) {
        return this.associations.get(marker).size() == 1;
    }
    
    public boolean hasManyProperties(Marker marker) {
        return this.associations.get(marker).size() > 1;
    }
    
    public boolean hasMarker(Marker marker) {
        return this.associations.containsKey(marker);
    }
    
    public boolean hasProperty(Object object) {
        for (Marker maker : this.associations.keySet()) {
            if (this.associations.get(maker).contains(object)) {
                return true;
            }
        }
        return false;
    }
    
    public ArrayList<Object> getProperties(Marker marker) {
        return this.associations.get(marker);
    }
    
    public void addProperty(Marker marker, Object object) {
        ArrayList<Object> list;
        if (hasMarker(marker)) {
            list = getProperties(marker);
        } else {
            list = new ArrayList<Object>();
        }
        list.add(object);
        this.associations.put(marker, list);
    }

}