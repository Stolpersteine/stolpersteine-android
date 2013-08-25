package com.dreiri.stolpersteine.client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dreiri.stolpersteine.callback.Callback;
import com.dreiri.stolpersteine.callback.OnJSONResponse;
import com.dreiri.stolpersteine.models.Location;
import com.dreiri.stolpersteine.models.Person;
import com.dreiri.stolpersteine.models.Stolperstein;
import com.google.android.gms.maps.model.LatLng;

public class StolpersteineClient {
    Client client;
    Callback callback;
    StringBuilder baseUri = new StringBuilder("https://stolpersteine-api.eu01.aws.af.cm/v1");
    
    public StolpersteineClient() {
        this.client = new Client();
    }
    
    public void getNumbersOfResultsAndHandleThem(int number, Callback callback) {
        this.callback = callback;
        StringBuilder queryUri = baseUri.append("/stolpersteine?offset=0&limit=").append(number);
        client.getJSONFeed(queryUri.toString(), new JSONResponseHandler());
    }
    
    private class JSONResponseHandler implements OnJSONResponse {

        @Override
        public void execute(JSONArray jsonArray) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject;
                try {
                    jsonObject = jsonArray.getJSONObject(i);
                    JSONObject jsonPerson = jsonObject.getJSONObject("person");
                    JSONObject jsonLocation = jsonObject.getJSONObject("location");
                    JSONObject jsonCoordinates = jsonLocation.getJSONObject("coordinates");
                    LatLng coordinates = new LatLng(jsonCoordinates.getDouble("latitude"), jsonCoordinates.getDouble("longitude"));
                    Person person = new Person(jsonPerson.getString("firstName"), jsonPerson.getString("lastName"), jsonPerson.getString("biographyUrl"));
                    Location location = new Location(jsonLocation.getString("street"), jsonLocation.getString("zipCode"), jsonLocation.getString("city"), coordinates);
                    Stolperstein stolperstein = new Stolperstein(person, location);
                    
                    if (callback != null) {
                        callback.handle(stolperstein);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        
    }

}
