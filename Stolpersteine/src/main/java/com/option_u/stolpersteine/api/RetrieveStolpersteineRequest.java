package com.option_u.stolpersteine.api;

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

import com.google.android.gms.maps.model.LatLng;
import com.option_u.stolpersteine.api.model.Location;
import com.option_u.stolpersteine.api.model.Person;
import com.option_u.stolpersteine.api.model.Source;
import com.option_u.stolpersteine.api.model.Stolperstein;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class RetrieveStolpersteineRequest implements Callback {
    private Handler handler;

    public RetrieveStolpersteineRequest(final Callback callback) {
        this.handler = new Handler(Looper.getMainLooper()) {
            @Override
            @SuppressWarnings("unchecked")
            public void handleMessage(Message message) {
                List<Stolperstein> stolpersteine = (List<Stolperstein>) message.obj;
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
        if (encodedClientCredentials != null) {
            requestBuilder.header("Authorization", "Basic " + encodedClientCredentials);
        }
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

        return uriBuilder.build().toString();
    }

    @Override
    public void onFailure(Request request, IOException e) {
        Log.e("Stolpersteine", e.getMessage());
        e.printStackTrace();
    }

    @Override
    public void onResponse(Response response) throws IOException {
        String data = response.body().string();
        List<Stolperstein> stolpersteine = parseData(data);
        dispatchStolpersteine(stolpersteine);
    }

    private void dispatchStolpersteine(List<Stolperstein> stolpersteine) {
        Message message = handler.obtainMessage(0, stolpersteine);
        message.sendToTarget();
    }

    private static List<Stolperstein> parseData(String data) {
        List<Stolperstein> stolpersteine = null;
        try {
            JSONArray jsonArray = new JSONArray(data);
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
            String biographyUrl = jsonPerson.optString("biographyUrl");
            String biographyUrlDecoded = URLDecoder.decode(URLEncoder.encode(biographyUrl, charsetName), charsetName);
            URI uri = null;
            try {
                uri = new URI(biographyUrlDecoded);
            } catch (URISyntaxException e) {
                // ignore
            }
            person.setBiography(uri);
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
