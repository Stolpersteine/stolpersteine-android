package com.dreiri.stolpersteine.models;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Location implements Parcelable {

    private String street;
    private String zipCode;
    private String city;
    private ArrayList<String> sublocalities = new ArrayList<String>();
    private LatLng coordinates; 
    
    public Location(String street, String zipCode, String city, ArrayList<String> sublocalities, LatLng coordinates) {
        this.street = street;
        this.zipCode = zipCode;
        this.city = city;
        this.sublocalities.addAll(sublocalities);
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
    
    public Location(String street, String zipCode, String city, LatLng coordinates) {
        new Location(street, zipCode, city, new ArrayList<String>(), coordinates);
    }
    
    public Location(String street, String zipCode, String city, ArrayList<String> sublocalities) {
        new Location(street, zipCode, city, sublocalities, null);
    }
    
    public Location(String street, String zipCode, String city) {
        new Location(street, zipCode, city, new ArrayList<String>(), null);
    }
    
    public Location(Parcel orig) {
        readFromParcel(orig);
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
        this.sublocalities.clear();
        this.sublocalities.addAll(sublocalities);
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
    
    public String address() {
        return this.street + ", " + this.zipCode + " " + this.city;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.street);
        dest.writeString(this.zipCode);
        dest.writeString(this.city);
//        dest.writeSerializable(this.sublocalities);
        dest.writeList(this.sublocalities);
        dest.writeParcelable(this.coordinates, 0);
    }
    
    private void readFromParcel(Parcel orig) {
        this.street = orig.readString();
        this.zipCode = orig.readString();
        this.city = orig.readString();
//        this.sublocalities = orig.readArrayList(String.class.getClassLoader());
        orig.readList(this.sublocalities, getClass().getClassLoader());
        this.coordinates = orig.readParcelable(LatLng.class.getClassLoader());
    }
    
    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {

        @Override
        public Location createFromParcel(Parcel source) {
            return new Location(source);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

}
