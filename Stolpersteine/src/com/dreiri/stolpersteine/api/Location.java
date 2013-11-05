package com.dreiri.stolpersteine.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Location implements Parcelable {

    private String street;
    private String zipCode;
    private String city;
    private LatLng coordinates; 
    
    public Location() {
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

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }
    
    public boolean equals(Location location) {
        return (this.street.equals(location.street) && this.zipCode.equals(location.zipCode)) ? true : false;
    }
    
    public String getAddressAsString() {
        AddressContext addressContext = new AddressContext(this);
        return addressContext.getAddress();
    }
    
    private class AddressContext {
        
        private AddressFormatter addressFormatter;
        
        private AddressContext(Location location) {
            if (location.street != null && location.zipCode != null && location.city != null) {
                addressFormatter = new AddressWithAllFields(location);
            } else if (location.street == null && location.zipCode != null && location.city != null) {
                addressFormatter = new AddressWithoutStreet(location);
            } else if (location.street != null && location.zipCode == null && location.city != null) {
                addressFormatter = new AddressWithoutZipCode(location);
            } else if (location.street != null && location.zipCode != null && location.city == null) {
                addressFormatter = new AddressWithoutCity(location);
            } else if (location.street == null && location.zipCode == null && location.city != null) {
                addressFormatter = new AddressWithoutStreetAndZipCode(location);
            } else if (location.street == null && location.zipCode != null && location.city == null) {
                addressFormatter = new AddressWithoutStreetAndCity(location);
            } else if (location.street != null && location.zipCode == null && location.city == null) {
                addressFormatter = new AddressWithoutZipCodeAndCity(location);
            } else {
                addressFormatter = new AddressWithoutAnything(location);
            }
        }
        
        private String getAddress() {
            return addressFormatter.formatAddress();
        }
        
    }

    private abstract class AddressFormatter {
        
        protected Location location;
        
        abstract String formatAddress();
        
    }
    
    private class AddressWithAllFields extends AddressFormatter {
        
        private AddressWithAllFields(Location location) {
            this.location = location;
        }

        @Override
        String formatAddress() {
            return location.street + ", " + location.zipCode + " " + location.city;
        }
        
    }
    
    private class AddressWithoutStreet extends AddressFormatter {
        
        private AddressWithoutStreet(Location location) {
            this.location = location;
        }

        @Override
        String formatAddress() {
            return location.zipCode + " " + location.city;
        }
        
    }
    
    private class AddressWithoutZipCode extends AddressFormatter {
        
        private AddressWithoutZipCode(Location location) {
            this.location = location;
        }

        @Override
        String formatAddress() {
            return location.street + ", " + location.city;
        }
        
    }
    
    private class AddressWithoutCity extends AddressFormatter {
        
        private AddressWithoutCity(Location location) {
            this.location = location;
        }

        @Override
        String formatAddress() {
            return location.street + ", " + location.zipCode;
        }
        
    }
    
    private class AddressWithoutStreetAndZipCode extends AddressFormatter {
        
        private AddressWithoutStreetAndZipCode(Location location) {
            this.location = location;
        }

        @Override
        String formatAddress() {
            return location.city;
        }
        
    }
    
    private class AddressWithoutStreetAndCity extends AddressFormatter {
        
        private AddressWithoutStreetAndCity(Location location) {
            this.location = location;
        }

        @Override
        String formatAddress() {
            return location.zipCode;
        }
        
    }
    
    private class AddressWithoutZipCodeAndCity extends AddressFormatter {
        
        private AddressWithoutZipCodeAndCity(Location location) {
            this.location = location;
        }

        @Override
        String formatAddress() {
            return location.street;
        }
        
    }
    
    private class AddressWithoutAnything extends AddressFormatter {
        
        private AddressWithoutAnything(Location location) {
            this.location = location;
        }

        @Override
        String formatAddress() {
            return "";
        }
        
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
        dest.writeParcelable(this.coordinates, 0);
    }
    
    private void readFromParcel(Parcel orig) {
        this.street = orig.readString();
        this.zipCode = orig.readString();
        this.city = orig.readString();
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