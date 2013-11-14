package com.dreiri.stolpersteine.api;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
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
import com.squareup.okhttp.OkHttpClient;

// BufferedInputStream
// Request 2.0
// Etag

public class RetrieveStolpersteineRequest2 extends AsyncTask<URI, Void, List<Stolperstein>> {

    private static final String BASE_URL = "https://stolpersteine-api.eu01.aws.af.cm/v1";
    private Callback callback;
    private SearchData defaultSearchData;
    private OkHttpClient httpClient;

    public RetrieveStolpersteineRequest2(OkHttpClient httpClient, Callback callback) {
        this.callback = callback;
        this.httpClient = httpClient;
    }
    
    static public interface Callback {
        public void onStolpersteineRetrieved(List<Stolperstein> stolpersteine);
    }
    
    String buildQuery(SearchData searchData, int offset, int limit) {
        StringBuilder queryBuilder = new StringBuilder(BASE_URL)
                .append("/stolpersteine?offset=")
                .append(offset)
                .append("&limit=")
                .append(limit);

        if (searchData != null) {
            String charsetName = Charset.defaultCharset().name();
            String keyword = searchData.getKeyword() != null ? searchData.getKeyword() : defaultSearchData.getKeyword();
            if (keyword != null) {
                try {
                    queryBuilder.append("&q=").append(URLEncoder.encode(keyword, charsetName));
                } catch (UnsupportedEncodingException e) {
                    Log.e("Stolpersteine", "Error encoding " + charsetName, e);
                }
            }

            String street = searchData.getStreet() != null ? searchData.getStreet() : defaultSearchData.getStreet();
            if (street != null) {
                try {
                    queryBuilder.append("&street=").append(URLEncoder.encode(street, charsetName));
                } catch (UnsupportedEncodingException e) {
                    Log.e("Stolpersteine", "Error encoding " + charsetName, e);
                }
            }

            String city = searchData.getCity() != null ? searchData.getCity() : defaultSearchData.getCity();
            if (city != null) {
                try {
                    queryBuilder.append("&city=").append(URLEncoder.encode(city, charsetName));
                } catch (UnsupportedEncodingException e) {
                    Log.e("Stolpersteine", "Error encoding " + charsetName, e);
                }
            }
        }

        return queryBuilder.toString();
    }

    Stolperstein parseStolperstein(JSONObject jsonObject) throws JSONException, URISyntaxException {
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
            source.setUri(new URI(jsonSource.optString("url")));
        }

        Person person = new Person();
        stolperstein.setPerson(person);
        JSONObject jsonPerson = jsonObject.getJSONObject("person");
        if (jsonPerson != null) {
            person.setFirstName(jsonPerson.optString("firstName"));
            person.setLastName(jsonPerson.optString("lastName"));
            person.setBiography(new URI(jsonPerson.optString("biographyUrl")));
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

    List<Stolperstein> parseStolpersteine(JSONArray jsonArray) throws JSONException, URISyntaxException {
        List<Stolperstein> stolpersteine = new ArrayList<Stolperstein>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Stolperstein stolperstein = parseStolperstein(jsonObject);
            stolpersteine.add(stolperstein);
        }

        return stolpersteine;
    }
    
    String get(URL url) throws IOException {
        HttpURLConnection connection = httpClient.open(url);
        String modified = connection.getRequestProperty("If-Modified-Since");
        String tag = connection.getRequestProperty("If-None-Match");
        InputStream in = null;
        try {
          // Read the response.
          in = connection.getInputStream();
          byte[] response = readFully(in);
          return new String(response, "UTF-8");
        } finally {
          if (in != null) in.close();
        }
      }

      byte[] readFully(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int count; (count = in.read(buffer)) != -1; ) {
          out.write(buffer, 0, count);
        }
        return out.toByteArray();
      }

    @Override
    protected List<Stolperstein> doInBackground(URI... uris) {
        List<Stolperstein> stolpersteine = null;

        try {
            
            String response = get(uris[0].toURL());
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
