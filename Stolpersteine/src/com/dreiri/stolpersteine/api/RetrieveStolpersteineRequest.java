package com.dreiri.stolpersteine.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.dreiri.stolpersteine.api.model.Location;
import com.dreiri.stolpersteine.api.model.Person;
import com.dreiri.stolpersteine.api.model.Source;
import com.dreiri.stolpersteine.api.model.Stolperstein;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.okhttp.OkHttpClient;

public class RetrieveStolpersteineRequest extends AsyncTask<URL, Void, List<Stolperstein>> {

    private OkHttpClient httpClient;
    private Callback callback;
    private String encodedClientCredentials;

    public RetrieveStolpersteineRequest(OkHttpClient httpClient, Callback callback) {
        this.callback = callback;
        this.httpClient = httpClient;
    }

    public String getEncodedClientCredentials() {
        return encodedClientCredentials;
    }

    public void setEncodedClientCredentials(String encodedClientCredentials) {
        this.encodedClientCredentials = encodedClientCredentials;
    }

    static public interface Callback {
        public void onStolpersteineRetrieved(List<Stolperstein> stolpersteine);
    }

    public static URL buildQuery(String baseUrl, SearchData searchData, SearchData defaultSearchData, int offset, int limit) {
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

        URL url;
        try {
            String urlString = uriBuilder.build().toString();
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }

        return url;
    }

    private Stolperstein parseStolperstein(JSONObject jsonObject) throws JSONException, URISyntaxException, UnsupportedEncodingException {
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
        if (jsonSource != null) {
            source.setName(jsonSource.optString("name"));
            String uri = URLEncoder.encode(jsonSource.optString("url"), "utf-8");
            source.setUri(new URI(uri));
        }

        Person person = new Person();
        stolperstein.setPerson(person);
        JSONObject jsonPerson = jsonObject.getJSONObject("person");
        if (jsonPerson != null) {
            person.setFirstName(jsonPerson.optString("firstName"));
            person.setLastName(jsonPerson.optString("lastName"));
            String uri = URLEncoder.encode(jsonPerson.optString("biographyUrl"), "utf-8");
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

    private List<Stolperstein> parseStolpersteine(JSONArray jsonArray) {
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

    private String retrieveData(URL url) throws IOException {
        String result;
        HttpURLConnection connection = httpClient.open(url);
        // Log.i("Stolpersteine", "" + connection.getRequestProperties());
        // connection.addRequestProperty("Authorization", "Basic " + encodedClientCredentials);
        InputStream in = null;
        try {
            result = readFully(connection.getInputStream());
        } finally {
            if (in != null) {
                in.close();
            }
        }

        return result;
    }

    private String readFully(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int count; (count = in.read(buffer)) != -1;) {
            out.write(buffer, 0, count);
        }
        return out.toString("UTF-8");
    }

    @Override
    protected List<Stolperstein> doInBackground(URL... urls) {
        List<Stolperstein> stolpersteine = null;

        try {
            String response = retrieveData(urls[0]);
            JSONArray jsonArray = new JSONArray(response);
            stolpersteine = parseStolpersteine(jsonArray);
        } catch (Exception e) {
            Log.e("Stolpersteine", "Error retrieving JSON response", e);
        }

        return stolpersteine;
    }

    @Override
    protected void onPostExecute(List<Stolperstein> stolpersteine) {
        if (callback != null) {
            callback.onStolpersteineRetrieved(stolpersteine);
        }
    }

}
