package com.dreiri.stolpersteine.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.dreiri.stolpersteine.api.model.Location;
import com.dreiri.stolpersteine.api.model.Person;
import com.dreiri.stolpersteine.api.model.Source;
import com.dreiri.stolpersteine.api.model.Stolperstein;
import com.google.android.gms.maps.model.LatLng;

public class NetworkService {
    private final String baseUri = "https://stolpersteine-api.eu01.aws.af.cm/v1";
	
    public void retrieveStolpersteine(SearchData searchData, int offset, int limit, Callback callback) {
        String query = new StringBuilder(baseUri)
        	.append("/stolpersteine?offset=")
        	.append(offset)
        	.append("&limit=")
        	.append(limit)
        	.toString();

        ReadJSONFeedTask task = new ReadJSONFeedTask(callback);
    	task.execute(URI.create(query));
    }
    
    Stolperstein parseStolperstein(JSONObject jsonObject) throws JSONException, URISyntaxException {
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

    	return stolperstein;    	
    }
    
    List<Stolperstein> parseStolpersteine(JSONArray jsonArray) throws JSONException, URISyntaxException {
    	List<Stolperstein> stolpersteine = new ArrayList<Stolperstein>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
        	JSONObject jsonObject = jsonArray.getJSONObject(i);
        	Stolperstein stolperstein = parseStolperstein(jsonObject);
        	stolpersteine.add(stolperstein);
        }
        
        return stolpersteine;
    }
    
    private String readJSONFeed(URI uri) throws ClientProtocolException, IOException {
        StringBuilder sb = new StringBuilder();
        
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(uri);
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
        	throw new IOException("HTTP status code: " + statusCode);
        }
        
        return sb.toString();
    }
    
    public interface Callback {
        public void handle(List<Stolperstein> stolpersteine);
    }

    private class ReadJSONFeedTask extends AsyncTask<URI, Void, List<Stolperstein>> {
        private Callback callback;
        
        private ReadJSONFeedTask(Callback callback) {
            this.callback = callback;
        }

        @Override
        protected List<Stolperstein> doInBackground(URI... uris) {
        	List<Stolperstein> stolpersteine = null;
        	
        	try {
        		String response = readJSONFeed(uris[0]);
        		JSONArray jsonArray = new JSONArray(response);
        		stolpersteine = parseStolpersteine(jsonArray); 
        	} catch(Exception e) {
        		Log.e("Stolpersteine", "Error retrieving JSON response", e);
        	}
            
            return stolpersteine;
        }
        
        @Override
        protected void onPostExecute(List<Stolperstein> stolpersteine) {
        	if (callback != null) {
        		callback.handle(stolpersteine);
        	}
        }
    }
}
