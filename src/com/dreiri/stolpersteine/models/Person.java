package com.dreiri.stolpersteine.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable {

    private String firstName;
    private String lastName;
    private String biography;
    
    public Person(String firstName, String lastName, String biography) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.biography = biography;
    }
    
    public Person(String firstName, String lastName) {
        this(firstName, lastName, "");
    }
    
    public Person(Parcel orig) {
        readFromParcel(orig);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }
    
    public String name() {
        return firstName + " " + lastName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.biography);
    }
    
    private void readFromParcel(Parcel orig) {
        this.firstName = orig.readString();
        this.lastName = orig.readString();
        this.biography = orig.readString();
    }
    
    public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {

        @Override
        public Person createFromParcel(Parcel source) {
            return new Person(source);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

}