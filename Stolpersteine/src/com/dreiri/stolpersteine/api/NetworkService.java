package com.dreiri.stolpersteine.api;

import java.io.File;
import java.io.IOException;
import java.net.ResponseCache;
import java.net.URL;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.HttpResponseCache;
import com.squareup.okhttp.OkHttpClient;

public class NetworkService {
    private static final String BASE_URL = "https://stolpersteine-api.eu01.aws.af.cm/v1";
    private static final int CACHE_SIZE_BYTES = 1024 * 1024;
    private SearchData defaultSearchData = new SearchData();
    OkHttpClient httpClient = new OkHttpClient();
    
    public NetworkService(Context context) {
        try {
            File cacheDir = new File(context.getCacheDir(), "http.cache");
            ResponseCache responseCache = new HttpResponseCache(cacheDir, CACHE_SIZE_BYTES);
            httpClient.setResponseCache(responseCache);
        } catch (IOException e) {
            Log.e("Stolpersteine", "Error creating disk cache", e);
        }
    }

    public SearchData getDefaultSearchData() {
        return defaultSearchData;
    }

    public void setDefaultSearchData(SearchData defaultSearchData) {
        this.defaultSearchData = defaultSearchData;
    }

    public void retrieveStolpersteine(SearchData searchData, int offset, int limit, RetrieveStolpersteineRequest.Callback callback) {
        URL url = RetrieveStolpersteineRequest.buildQuery(BASE_URL, searchData, defaultSearchData, offset, limit);
        RetrieveStolpersteineRequest task = new RetrieveStolpersteineRequest(httpClient, callback);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }

}
