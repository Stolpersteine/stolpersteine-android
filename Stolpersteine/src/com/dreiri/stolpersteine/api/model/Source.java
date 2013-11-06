package com.dreiri.stolpersteine.api.model;

import java.net.URI;
import java.net.URISyntaxException;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Source implements Parcelable {

    private String name;
    private URI uri;
    
    public Source() {
    }

    public Source(Parcel orig) {
        readFromParcel(orig);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(uri.toString());
    }
    
    private void readFromParcel(Parcel orig) {
        name = orig.readString();
        
        try {
            uri = new URI(orig.readString());
        } catch (URISyntaxException e) {
            Log.e("Stolpersteine", "Failed to read source URI from parcel", e);
        }
    }
    
    public static final Parcelable.Creator<Source> CREATOR = new Parcelable.Creator<Source>() {

        @Override
        public Source createFromParcel(Parcel source) {
            return new Source(source);
        }

        @Override
        public Source[] newArray(int size) {
            return new Source[size];
        }
    };
}
