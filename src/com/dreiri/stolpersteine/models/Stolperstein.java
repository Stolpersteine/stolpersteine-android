package com.dreiri.stolpersteine.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Stolperstein implements Parcelable {

    private Person person;
    private Location location;
    
    public Stolperstein(Person person, Location location) {
        this.person = person;
        this.location = location;
    }
    
    public Stolperstein(Parcel orig) {
        readFromParcel(orig);
    }
    
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.person, 0);
        dest.writeParcelable(this.location, 0);
    }
    
    private void readFromParcel(Parcel orig) {
        this.person = orig.readParcelable(Person.class.getClassLoader());
        this.location = orig.readParcelable(Location.class.getClassLoader());
    }
    
    public static final Parcelable.Creator<Stolperstein> CREATOR = new Parcelable.Creator<Stolperstein>() {

        @Override
        public Stolperstein createFromParcel(Parcel source) {
            return new Stolperstein(source);
        }

        @Override
        public Stolperstein[] newArray(int size) {
            return new Stolperstein[size];
        }
    };

}