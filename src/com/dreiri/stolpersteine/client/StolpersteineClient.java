package com.dreiri.stolpersteine.client;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.dreiri.stolpersteine.callback.Callback;
import com.dreiri.stolpersteine.callback.OnJSONResponse;
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
    
    public void retrieveStolpersteine() {
    	retrieveStolpersteine(0, NETWORK_BATCH_SIZE);
    }

    public void retrieveStolpersteine(final int offset, int limit) {
//    	Log.i("XXX", "Request: " + offset + " " + limit);

    	getNumbersOfResultsAndHandleThem(offset, limit, new Callback() {
        	@Override
            public void handle(ArrayList<Stolperstein> stolpersteine) {
//            	Log.i("XXX", "Received: " + stolpersteine.size());
            	if (stolpersteine.size() == NETWORK_BATCH_SIZE) {
            		retrieveStolpersteine(offset + NETWORK_BATCH_SIZE, NETWORK_BATCH_SIZE);
            	}
            }
        });
    }
    
    public void getNumbersOfResultsAndHandleThem(int offset, int limit, Callback callback) {
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
        	ArrayList<Stolperstein> stolpersteine = new ArrayList<Stolperstein>(jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject;
                JSONObject jsonPerson;
                JSONObject jsonLocation;
                
                try {
                    jsonObject = jsonArray.getJSONObject(i);
                    jsonPerson = jsonObject.getJSONObject("person");
                    jsonLocation = jsonObject.getJSONObject("location");
                } catch (JSONException e) {
                    e.printStackTrace();
                    continue;
                }
                
                JSONObject jsonCoordinates = getJSONObjectFromJSONObjectSafely("coordinates", jsonLocation);
                
                String firstName = getStringFromJSONObjectSafely("firstName", jsonPerson);
                String lastName = getStringFromJSONObjectSafely("lastName", jsonPerson);
                String biographyUrl = getStringFromJSONObjectSafely("biographyUrl", jsonPerson);
                
                String street = getStringFromJSONObjectSafely("street", jsonLocation);
                String zipCode = getStringFromJSONObjectSafely("zipCode", jsonLocation);
                String city = getStringFromJSONObjectSafely("city", jsonLocation);
                double latitude = getDoubleFromJSONObjectSafely("latitude", jsonCoordinates);
                double longitude = getDoubleFromJSONObjectSafely("longitude", jsonCoordinates);
                
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
    
    private JSONObject getJSONObjectFromJSONObjectSafely(String name, JSONObject jsonObjectIn) {
        JSONObject jsonObjectOut = null;
        try {
          	jsonObjectOut = jsonObjectIn.getJSONObject(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjectOut;
    }
    
    private String getStringFromJSONObjectSafely(String name, JSONObject jsonObject) {
        String out = jsonObject.optString(name, null);
        return out;
    }
    
    private double getDoubleFromJSONObjectSafely(String name, JSONObject jsonObject) {
        double out = jsonObject.optDouble(name, 0.0);
        return out;
    }

}