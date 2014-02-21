package com.dreiri.stolpersteine.activities.bio;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.webkit.WebView;

public class HTMLContentLoader {

    private WebView browser;
    
    public HTMLContentLoader(WebView browser) {
        this.browser = browser;
    }
    
    public void loadContent(final Activity activity, final String url, final String cssQuery) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.parse(new URL(url).openStream(), "utf-8", url);
                    Elements elements = document.select(cssQuery);
                    String content = elements.toString();
                    DataLoaderRunnable dataLoaderRunnable = new DataLoaderRunnable(browser, content);
                    activity.runOnUiThread(dataLoaderRunnable);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    private class DataLoaderRunnable implements Runnable {
        
        private WebView browser;
        private String data;
        private String mimeType = "text/html; charset=UTF-8";
        
        public DataLoaderRunnable(WebView browser, String data) {
            this.browser = browser;
            this.data = data;
        }
        
        @Override
        public void run() {
            browser.loadData(data, mimeType, null);
        }
        
    }

}
