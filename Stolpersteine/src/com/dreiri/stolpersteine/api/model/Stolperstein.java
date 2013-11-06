package com.dreiri.stolpersteine.api.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Stolperstein implements Parcelable {
	
	public enum Type {
		STOLPERSTEIN, STOLPERSCHWELLE;
	}

	private String id;
	private Type type;

	private Source source;
	private Person person;
    private Location location;
    
    public Stolperstein() {
    }
    
    public Stolperstein(Parcel orig) {
        readFromParcel(orig);
    }
    
    public String getId() {
	    return id;
    }

	public void setId(String id) {
	    this.id = id;
    }

    public Type getType() {
	    return type;
    }

	public void setType(Type type) {
	    this.type = type;
    }

	public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
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