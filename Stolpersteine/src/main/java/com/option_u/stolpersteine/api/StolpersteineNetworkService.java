package com.option_u.stolpersteine.api;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

public class StolpersteineNetworkService {
    private static final String API_BASE_URL = "https://stolpersteine-api.eu01.aws.af.cm/v1";
    // private static final String API_BASE_URL = "http://10.0.3.2:3000/v1"; // localhost genymotion
    // private static final String API_BASE_URL = "http://localhost.:3000/v1"; // localhost genymotion via Charles
    private static final int CACHE_SIZE_BYTES = 1024 * 1024;
    private SearchData defaultSearchData = new SearchData();
    private OkHttpClient httpClient = new OkHttpClient();
    private String encodedClientCredentials;

    public StolpersteineNetworkService(Context context, String apiUser, String apiPassword) {
        try {
            // Caching
            File cacheDir = new File(context.getCacheDir(), "http.cache");
            Cache cache = new Cache(cacheDir, CACHE_SIZE_BYTES);
            httpClient.setCache(cache);
        } catch (IOException e) {
            Log.e("Stolpersteine", "Error creating disk cache", e);
        }

        try {
            // Basic auth
            if (apiUser != null && apiPassword != null) {
                String clientCredentials = String.format("%s:%s", apiUser, apiPassword, null);
                encodedClientCredentials = Base64.encodeToString(clientCredentials.getBytes("UTF-8"), Base64.DEFAULT);
            }
        } catch (UnsupportedEncodingException e) {
            Log.e("Stolpersteine", "Error encoding client credentials", e);
        }
    }

    public SearchData getDefaultSearchData() {
        return defaultSearchData;
    }

    public void setDefaultSearchData(SearchData defaultSearchData) {
        this.defaultSearchData = defaultSearchData;
    }

    public Object retrieveStolpersteine(SearchData searchData, int offset, int limit, RetrieveStolpersteineRequest.Callback callback) {
        Request request = RetrieveStolpersteineRequest.buildRequest(API_BASE_URL, searchData, defaultSearchData, offset, limit, encodedClientCredentials);
        httpClient.newCall(request).enqueue(new RetrieveStolpersteineRequest(callback));

        return request;
    }

    public void cancelRequest(Object tag) {
        httpClient.cancel(tag);
    }

}
