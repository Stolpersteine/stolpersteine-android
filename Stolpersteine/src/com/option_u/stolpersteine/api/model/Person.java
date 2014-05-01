package com.option_u.stolpersteine.api.model;

import java.net.URI;
import java.net.URISyntaxException;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Person implements Parcelable {

    private String firstName;
    private String lastName;
    private URI biographyUri;

    public Person() {
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

    public URI getBiographyUri() {
        return biographyUri;
    }

    public void setBiography(URI biography) {
        this.biographyUri = biography;
    }

    public String getNameAsString() {
        return firstName + " " + lastName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(biographyUri.toString());
    }

    private void readFromParcel(Parcel orig) {
        firstName = orig.readString();
        lastName = orig.readString();

        try {
            this.biographyUri = new URI(orig.readString());
        } catch (URISyntaxException e) {
            Log.e("Stolpersteine", "Failed to read biography URI from parcel", e);
        }
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