package com.dreiri.stolpersteine.api;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ResponseCache;
import java.net.URL;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.squareup.okhttp.HttpResponseCache;
import com.squareup.okhttp.OkHttpClient;

public class NetworkService {
    private static final String API_BASE_URL = "https://stolpersteine-api.eu01.aws.af.cm/v1";
//  private static final String API_BASE_URL = "http://10.0.3.2:3000/v1";	// localhost genymotion
//  private static final String API_BASE_URL = "http://localhost.:3000/v1";	// localhost genymotion via Charles
    private static final String API_CLIENT_USER = "android";
    private static final String API_CLIENT_PASSWORD = "test";
    private static final int CACHE_SIZE_BYTES = 1024 * 1024;
    private SearchData defaultSearchData = new SearchData();
    private OkHttpClient httpClient = new OkHttpClient();
    private String encodedClientCredentials;
    
    public NetworkService(Context context) {
    	// Caching
        try {
            File cacheDir = new File(context.getCacheDir(), "http.cache");
            ResponseCache responseCache = new HttpResponseCache(cacheDir, CACHE_SIZE_BYTES);
            httpClient.setResponseCache(responseCache);
        } catch (IOException e) {
            Log.e("Stolpersteine", "Error creating disk cache", e);
        }
        
        // Basic auth
        try {
            String clientCredentials = String.format("%s:%s", API_CLIENT_USER, API_CLIENT_PASSWORD, null);
	        encodedClientCredentials = Base64.encodeToString(clientCredentials.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            Log.e("Stolpersteine", "Error encoding client credentials", e);
        }
        
        // HTTP configuration
        HttpURLConnection.setFollowRedirects(true);
    }

    public SearchData getDefaultSearchData() {
        return defaultSearchData;
    }

    public void setDefaultSearchData(SearchData defaultSearchData) {
        this.defaultSearchData = defaultSearchData;
    }

    public void retrieveStolpersteine(SearchData searchData, int offset, int limit, RetrieveStolpersteineRequest.Callback callback) {
        URL url = RetrieveStolpersteineRequest.buildQuery(API_BASE_URL, searchData, defaultSearchData, offset, limit);
        RetrieveStolpersteineRequest request = new RetrieveStolpersteineRequest(httpClient, callback);
        request.setEncodedClientCredentials(encodedClientCredentials);
        request.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }

}
