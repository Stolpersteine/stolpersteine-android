package com.dreiri.stolpersteine.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import android.os.AsyncTask;
import android.util.Log;

public class Client {

    private String readJSONFeed(String uri) {
        StringBuilder sb = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(uri);
        
        try {
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
                Log.e("HTTP", "Can't retrieve JSON response");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    
    private class ReadJSONFeedTask extends AsyncTask<String, Void, String> {
        
        private OnJSONResponse onJSONResponse;
        
        private ReadJSONFeedTask(OnJSONResponse onJSONResponse) {
            this.onJSONResponse = onJSONResponse;
        }

        @Override
        protected String doInBackground(String... uris) {
            return readJSONFeed(uris[0]);
        }
        
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                Log.i("HTTP", "Number of Stolpersteine retrieved: " + jsonArray.length());
                onJSONResponse.execute(jsonArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }
    
    public void getJSONFeed(String uri, OnJSONResponse onJSONResponse) {
        new ReadJSONFeedTask(onJSONResponse).execute(uri);
    }
    
}
