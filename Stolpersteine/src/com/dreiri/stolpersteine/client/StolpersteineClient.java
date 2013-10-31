package com.dreiri.stolpersteine.client;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dreiri.stolpersteine.models.Location;
import com.dreiri.stolpersteine.models.Person;
import com.dreiri.stolpersteine.models.Stolperstein;
import com.google.android.gms.maps.model.LatLng;

public class StolpersteineClient {
	final static int NETWORK_BATCH_SIZE = 500;
	
    Client client;
    Callback callback;
    StringBuilder baseUri = new StringBuilder("https://stolpersteine-api.eu01.aws.af.cm/v1");
    
    public StolpersteineClient() {
        this.client = new Client();
    }
    
    public void retrieveAllStolpersteine(Callback callback) {
    	retrieveStolpersteine(0, NETWORK_BATCH_SIZE, callback);
    }

    public void retrieveStolpersteine(final int offset, int limit, final Callback callback) {
    	retrieveRangeOfResultsAndHandleThem(offset, limit, new Callback() {
        	@Override
            public void handle(List<Stolperstein> stolpersteine) {
        	    callback.handle(stolpersteine);
            	if (stolpersteine.size() == NETWORK_BATCH_SIZE) {
            		retrieveStolpersteine(offset + NETWORK_BATCH_SIZE, NETWORK_BATCH_SIZE, callback);
            	}
            }
        });
    }
    
    public void retrieveRangeOfResultsAndHandleThem(int offset, int limit, Callback callback) {
        this.callback = callback;
        StringBuilder queryUri = new StringBuilder()
        								.append(baseUri) 
        								.append("/stolpersteine?offset=")
        								.append(offset)
        								.append("&limit=")
        								.append(limit);
        client.getJSONFeed(queryUri.toString(), new JSONResponseHandler());
    }
    
    private class JSONResponseHandler implements OnJSONResponse {

        @Override
        public void execute(JSONArray jsonArray) {
        	List<Stolperstein> stolpersteine = new ArrayList<Stolperstein>(jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                
                JSONObject jsonPerson = jsonObject.optJSONObject("person");
                String firstName = jsonPerson.optString("firstName", null);
                String lastName = jsonPerson.optString("lastName", null);
                String biographyUrl = jsonPerson.optString("biographyUrl", null);

                JSONObject jsonLocation = jsonObject.optJSONObject("location");
                String street = jsonLocation.optString("street", null);
                String zipCode = jsonLocation.optString("zipCode", null);
                String city = jsonLocation.optString("city", null);
                
                JSONObject jsonCoordinates = jsonLocation.optJSONObject("coordinates");
                double latitude = jsonCoordinates.optDouble("latitude", 0.0);
                double longitude = jsonCoordinates.optDouble("longitude", 0.0);
                
                Person person = new Person(firstName, lastName, biographyUrl);
                LatLng coordinates = new LatLng(latitude, longitude);
                Location location = new Location(street, zipCode, city, coordinates);
                Stolperstein stolperstein = new Stolperstein(person, location);
                stolpersteine.add(stolperstein);
            }
            
            if (callback != null) {
                callback.handle(stolpersteine);
            }
        }
        
    }
    
}