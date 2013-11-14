package com.dreiri.stolpersteine.api;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.dreiri.stolpersteine.api.model.Location;
import com.dreiri.stolpersteine.api.model.Person;
import com.dreiri.stolpersteine.api.model.Source;
import com.dreiri.stolpersteine.api.model.Stolperstein;
import com.google.android.gms.maps.model.LatLng;

public class RetrieveStolpersteineRequest extends JsonRequest<List<Stolperstein>> {

	public RetrieveStolpersteineRequest(String url, Listener<List<Stolperstein>> listener, ErrorListener errorListener) {
	    super(Request.Method.GET, url, null, listener, errorListener);
    }
	
	@Override
    protected Response<List<Stolperstein>> parseNetworkResponse(NetworkResponse networkResponse) {
		Response<List<Stolperstein>> response;
		try {
			String jsonString = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
            JSONArray jsonArray = new JSONArray(jsonString);
    		List<Stolperstein> stolpersteine = parseStolpersteine(jsonArray);
    		response = Response.success(stolpersteine, HttpHeaderParser.parseCacheHeaders(networkResponse));
        } catch (Exception e) {
            response = Response.error(new ParseError(e));
        }
		
	    return response;
    }

    static String buildUrl(String baseUrl, SearchData searchData, SearchData defaultSearchData, int offset, int limit) {
        StringBuilder queryBuilder = new StringBuilder(baseUrl)
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
    
    private static Stolperstein parseStolperstein(JSONObject jsonObject) throws JSONException, URISyntaxException {
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

    private static List<Stolperstein> parseStolpersteine(JSONArray jsonArray) throws JSONException, URISyntaxException {
        List<Stolperstein> stolpersteine = new ArrayList<Stolperstein>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Stolperstein stolperstein = parseStolperstein(jsonObject);
            stolpersteine.add(stolperstein);
        }

        return stolpersteine;
    }
    
    public interface Callback {
        public void onStolpersteineRetrieved(List<Stolperstein> stolpersteine);
    }
    
    public static class Builder {
    	private String baseUrl;
    	private Listener<List<Stolperstein>> listener;
    	private ErrorListener errorListener;
    	private SearchData searchData;
    	private SearchData defaultSearchData;
    	private int offset;
    	private int limit;
    	
    	public Builder(String baseUrl, final Callback callback) {
    		this.baseUrl = baseUrl;
    		this.listener = new Response.Listener<List<Stolperstein>>() {

		        @Override
		        public void onResponse(List<Stolperstein> stolpersteine) {
		        	if (callback != null) {
		        		callback.onStolpersteineRetrieved(stolpersteine);
		        	}
		        }
	        };
    		this.errorListener = new Response.ErrorListener() {

		        @Override
		        public void onErrorResponse(VolleyError error) {
		        	if (callback != null) {
		        		callback.onStolpersteineRetrieved(null);
		        	}
		        }
	        };
    	}

    	public Builder setSearchData(SearchData searchData) {
    		this.searchData = searchData;
    		return this;
    	}

    	public Builder setDefaultSearchData(SearchData defaultSearchData) {
    		this.defaultSearchData = defaultSearchData;
    		return this;
    	}
    	
    	public Builder setRange(int offset, int limit) {
    		this.offset = offset;
    		this.limit = limit;
    		return this;
    	}

    	public RetrieveStolpersteineRequest build() {
    		String url = buildUrl(baseUrl, searchData, defaultSearchData, offset, limit);
    		return new RetrieveStolpersteineRequest(url, listener, errorListener);
    	}
    }

}
