package com.dreiri.stolpersteine.models;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

public class Location {

    private String street;
    private String zipCode;
    private String city;
    private ArrayList<String> sublocalities;
    private LatLng coordinates; 
    
    public Location(String street, String zipCode, String city, ArrayList<String> sublocalities, LatLng coordinates) {
        this.street = street;
        this.zipCode = zipCode;
        this.city = city;
        this.sublocalities = sublocalities;
        this.coordinates = coordinates;
    }
    
    public Location(String street, String zipCode, String city, ArrayList<String> sublocalities, double latitude, double longitude) {
        LatLng coordinates = new LatLng(latitude, longitude);
        new Location(street, zipCode, city, sublocalities, coordinates);
    }
    
    public Location(String street, String zipCode, String city, ArrayList<String> sublocalities, String latitude, String longitude) {
        LatLng coordinates = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        new Location(street, zipCode, city, sublocalities, coordinates);
    }
    
    public Location(String street, String zipCode, String city, ArrayList<String> sublocalities) {
        new Location(street, zipCode, city, sublocalities, null);
    }
    
    public Location(String street, String zipCode, String city, LatLng coordinates) {
        new Location(street, zipCode, city, null, coordinates);
    }
    
    public Location(String street, String zipCode, String city) {
        new Location(street, zipCode, city, null, null);
    }
    
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public ArrayList<String> getSublocalities() {
        return sublocalities;
    }

    public void setSublocalities(ArrayList<String> sublocalities) {
        this.sublocalities = sublocalities;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }
    
    public double getLatitude() {
        return coordinates.latitude;
    }
    
    public double getLongitude() {
        return coordinates.longitude;
    }
    
    public boolean equals(Location location) {
        return (this.street.equals(location.street) && this.zipCode.equals(location.zipCode)) ? true : false;
    }

}
