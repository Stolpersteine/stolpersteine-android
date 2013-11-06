package com.dreiri.stolpersteine.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.dreiri.stolpersteine.api.model.Location;
import com.dreiri.stolpersteine.api.model.Person;
import com.dreiri.stolpersteine.api.model.Source;
import com.dreiri.stolpersteine.api.model.Stolperstein;
import com.google.android.gms.maps.model.LatLng;

public class NetworkService {
    private Callback callback;
    private StringBuilder baseUri = new StringBuilder("https://stolpersteine-api.eu01.aws.af.cm/v1");
	
    public void retrieveStolpersteine(SearchData searchData, int offset, int limit, Callback callback) {
        this.callback = callback;
        StringBuilder queryUri = new StringBuilder()
        								.append(baseUri) 
        								.append("/stolpersteine?offset=")
        								.append(offset)
        								.append("&limit=")
        								.append(limit);
        getJSONFeed(queryUri.toString(), new JSONResponseHandler());
    }
    
    private class JSONResponseHandler implements OnJSONResponse {

        @Override
        public void execute(JSONArray jsonArray) {
        	List<Stolperstein> stolpersteine = new ArrayList<Stolperstein>(jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
            	try {
            		JSONObject jsonObject = jsonArray.getJSONObject(i);
            		
                    Stolperstein stolperstein = new Stolperstein();
                	stolperstein.setId(jsonObject.getString("id"));
                	String type = jsonObject.getString("id");
                	if (type.equals("stolperschwelle")) {
                		stolperstein.setType(Stolperstein.Type.STOLPERSCHWELLE);
                	} else {
                		stolperstein.setType(Stolperstein.Type.STOLPERSTEIN);
                	}
                	
                	Source source = new Source();
                	JSONObject jsonSource = jsonObject.optJSONObject("source");
                	source.setName(jsonSource.getString("name"));
                	source.setUri(new URI(jsonSource.getString("url")));
                	stolperstein.setSource(source);

                    Person person = new Person();
                    JSONObject jsonPerson = jsonObject.getJSONObject("person");
                    person.setFirstName(jsonPerson.getString("firstName"));
                    person.setLastName(jsonPerson.getString("lastName"));
                    person.setBiography(new URI(jsonPerson.getString("biographyUrl")));
                    stolperstein.setPerson(person);

                    Location location = new Location();
                	JSONObject jsonLocation = jsonObject.getJSONObject("location");
                	JSONObject jsonCoordinates = jsonLocation.getJSONObject("coordinates");
                	double latitude = jsonCoordinates.getDouble("latitude");
                	double longitude = jsonCoordinates.getDouble("longitude");
                	location.setCoordinates(new LatLng(latitude, longitude));
                	location.setStreet(jsonLocation.getString("street"));
                	location.setZipCode(jsonLocation.optString("zipCode", null));	// optional
                	location.setCity(jsonLocation.getString("city"));
                	stolperstein.setLocation(location);
                
                	stolpersteine.add(stolperstein);
            	} catch(Exception e) {
            		Log.e("Stolpersteine", "Failed to parse stolperstein", e);
            	}
            }
            
            if (callback != null) {
                callback.handle(stolpersteine);
            }
        }
    }

    private String readJSONFeed(String uri) {
        StringBuilder sb = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(uri);
        
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                HttpEntity httpEntity = httpResponse.getEntity();
                InputStream inputStream = httpEntity.getContent();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                for (String line; (line = bufferedReader.readLine()) != null;) {
                    sb.append(line);
                }
            } else {
                Log.e("HTTP", "Can't retrieve JSON response");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    
    private class ReadJSONFeedTask extends AsyncTask<String, Void, String> {
        
        private OnJSONResponse onJSONResponse;
        
        private ReadJSONFeedTask(OnJSONResponse onJSONResponse) {
            this.onJSONResponse = onJSONResponse;
        }

        @Override
        protected String doInBackground(String... uris) {
            return readJSONFeed(uris[0]);
        }
        
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                onJSONResponse.execute(jsonArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }
    
    public void getJSONFeed(String uri, OnJSONResponse onJSONResponse) {
        new ReadJSONFeedTask(onJSONResponse).execute(uri);
    }
    
}
