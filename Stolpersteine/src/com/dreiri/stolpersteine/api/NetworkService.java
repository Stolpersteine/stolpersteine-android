package com.dreiri.stolpersteine.api;

import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.ResponseCache;
import java.net.URL;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.HttpResponseCache;
import com.squareup.okhttp.OkAuthenticator;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.internal.http.HttpAuthenticator;

public class NetworkService {
    private static final String API_BASE_URL = "https://stolpersteine-api.eu01.aws.af.cm/v1";
    private static final String API_CLIENT_USER = "android";
    private static final String API_CLIENT_PASSWORD = "test";
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
        
        // Authentication
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(API_CLIENT_USER, API_CLIENT_PASSWORD.toCharArray());
            }
        });
        OkAuthenticator authenticator = HttpAuthenticator.SYSTEM_DEFAULT; 
        httpClient.setAuthenticator(authenticator);
    }

    public SearchData getDefaultSearchData() {
        return defaultSearchData;
    }

    public void setDefaultSearchData(SearchData defaultSearchData) {
        this.defaultSearchData = defaultSearchData;
    }

    public void retrieveStolpersteine(SearchData searchData, int offset, int limit, RetrieveStolpersteineRequest.Callback callback) {
        URL url = RetrieveStolpersteineRequest.buildQuery(API_BASE_URL, searchData, defaultSearchData, offset, limit);
        RetrieveStolpersteineRequest task = new RetrieveStolpersteineRequest(httpClient, callback);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }

}
