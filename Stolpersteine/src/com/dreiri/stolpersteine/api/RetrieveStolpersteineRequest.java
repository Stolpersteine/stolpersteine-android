package com.dreiri.stolpersteine.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.dreiri.stolpersteine.api.model.Location;
import com.dreiri.stolpersteine.api.model.Person;
import com.dreiri.stolpersteine.api.model.Source;
import com.dreiri.stolpersteine.api.model.Stolperstein;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.okhttp.Failure;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class RetrieveStolpersteineRequest implements Response.Receiver {
    private ByteArrayOutputStream data = new ByteArrayOutputStream();
    private Handler handler;
    
    public RetrieveStolpersteineRequest(final Callback callback) {
        this.handler = new Handler(Looper.getMainLooper()) {
            @Override
            @SuppressWarnings("unchecked")
            public void handleMessage(Message message) {
                List<Stolperstein> stolpersteine = (List<Stolperstein>)message.obj;
                callback.onStolpersteineRetrieved(stolpersteine);
            }
        };
    }
    
    public static interface Callback {
        public void onStolpersteineRetrieved(List<Stolperstein> stolpersteine);
    }
    
    public static Request buildRequest(String baseUrl, SearchData searchData, SearchData defaultSearchData, int offset, int limit, String encodedClientCredentials) {
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.get();
        requestBuilder.header("Authorization", "Basic " + encodedClientCredentials);
        requestBuilder.url(buildQuery(baseUrl, searchData, defaultSearchData, offset, limit));
        
        return requestBuilder.build();
    }
    
    private static String buildQuery(String baseUrl, SearchData searchData, SearchData defaultSearchData, int offset, int limit) {
        Uri.Builder uriBuilder = new Uri.Builder()
                .encodedPath(baseUrl)
                .appendEncodedPath("stolpersteine")
                .appendQueryParameter("offset", Integer.toString(offset))
                .appendQueryParameter("limit", Integer.toString(limit));

        if (searchData == null) {
            searchData = new SearchData();
        }
        
        if (defaultSearchData == null) {
            defaultSearchData = new SearchData();
        }
        
        String keyword = searchData.getKeyword() != null ? searchData.getKeyword() : defaultSearchData.getKeyword();
        if (keyword != null) {
            uriBuilder.appendQueryParameter("q", keyword);
        }

        String street = searchData.getStreet() != null ? searchData.getStreet() : defaultSearchData.getStreet();
        if (street != null) {
            uriBuilder.appendQueryParameter("street", street);
        }

        String city = searchData.getCity() != null ? searchData.getCity() : defaultSearchData.getCity();
        if (city != null) {
            uriBuilder.appendQueryParameter("city", city);
        }

        String urlString = uriBuilder.build().toString();
        return urlString;
    }

    @Override
    public void onFailure(Failure failure) {
        Log.e("Stolpersteine", "failure");
    }

    @Override
    public boolean onResponse(Response response) throws IOException {
        Response.Body body = response.body();
        byte[] buffer = new byte[1024];
        while (body.ready()) {
            int count = body.byteStream().read(buffer);
            if (count == -1) {
                List<Stolperstein> stolpersteine = parseData(data);
                dispatchStolpersteine(stolpersteine);
                
                return true;
            }

            data.write(buffer, 0, count);
        }

        return false;
    }
    
    private void dispatchStolpersteine(List<Stolperstein> stolpersteine) {
        Message message = handler.obtainMessage(0, stolpersteine);
        message.sendToTarget();  
    }
    
    private static List<Stolperstein> parseData(ByteArrayOutputStream data) {
        List<Stolperstein> stolpersteine = null;
        try {
            String dataAsString = data.toString("UTF-8");
            JSONArray jsonArray = new JSONArray(dataAsString);
            stolpersteine = parseStolpersteine(jsonArray);
        } catch (Exception e) {
            Log.e("Stolpersteine", "Error retrieving JSON response", e);
        }
        
        return stolpersteine;
    }
    
    private static List<Stolperstein> parseStolpersteine(JSONArray jsonArray) {
        List<Stolperstein> stolpersteine = new ArrayList<Stolperstein>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Stolperstein stolperstein = parseStolperstein(jsonObject);
                stolpersteine.add(stolperstein);
            } catch (Exception e) {
                Log.e("Stolpersteine", "Error parsing JSON", e);
            }
        }

        return stolpersteine;
    }
    
    private static Stolperstein parseStolperstein(JSONObject jsonObject) throws JSONException, URISyntaxException, UnsupportedEncodingException {
        Stolperstein stolperstein = new Stolperstein();
        stolperstein.setId(jsonObject.getString("id"));
        String type = jsonObject.optString("type");
        if (type.equals("stolperschwelle")) {
            stolperstein.setType(Stolperstein.Type.STOLPERSCHWELLE);
        } else {
            stolperstein.setType(Stolperstein.Type.STOLPERSTEIN);
        }

        Source source = new Source();
        stolperstein.setSource(source);
        JSONObject jsonSource = jsonObject.optJSONObject("source");
        String charsetName = "utf-8";
        if (jsonSource != null) {
            source.setName(jsonSource.optString("name"));
            String uri = URLDecoder.decode(URLEncoder.encode(jsonSource.optString("url"), charsetName), charsetName);
            source.setUri(new URI(uri));
        }

        Person person = new Person();
        stolperstein.setPerson(person);
        JSONObject jsonPerson = jsonObject.getJSONObject("person");
        if (jsonPerson != null) {
            person.setFirstName(jsonPerson.optString("firstName"));
            person.setLastName(jsonPerson.optString("lastName"));
            String uri = URLDecoder.decode(URLEncoder.encode(jsonPerson.optString("biographyUrl"), charsetName), charsetName);
            person.setBiography(new URI(uri));
        }

        Location location = new Location();
        stolperstein.setLocation(location);
        JSONObject jsonLocation = jsonObject.getJSONObject("location");
        if (jsonLocation != null) {
            location.setStreet(jsonLocation.optString("street"));
            location.setZipCode(jsonLocation.optString("zipCode"));
            location.setCity(jsonLocation.optString("city"));

            JSONObject jsonCoordinates = jsonLocation.getJSONObject("coordinates");
            if (jsonCoordinates != null) {
                double latitude = jsonCoordinates.optDouble("latitude", 0.0);
                double longitude = jsonCoordinates.optDouble("longitude", 0.0);
                location.setCoordinates(new LatLng(latitude, longitude));
            }
        }

        return stolperstein;
    }
    
}
