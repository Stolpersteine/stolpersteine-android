package com.dreiri.stolpersteine.activities;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.dreiri.stolpersteine.R;

public class BioActivity extends Activity {
    
    protected enum ViewFormat {TEXT, WEB};
    ViewFormat viewFormat;
    WebView browser;
    WebSettings settings;
    ProgressBar progressBar;
    String bioUrl;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_bio);
        Intent intent = getIntent();
        bioUrl = intent.getStringExtra("bioUrl");
        
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        
        browser = (WebView) findViewById(R.id.webview);
        browser.setWebViewClient(new SimpleWebViewClient());
        settings = browser.getSettings();
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        
        viewFormat = ViewFormat.TEXT;
        loadContentInBrowser(bioUrl, new ContentLoader());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bio, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_text:
                if (viewFormat == ViewFormat.WEB) {
                    viewFormat = ViewFormat.TEXT;
                    settings.setLoadWithOverviewMode(false);
                    settings.setUseWideViewPort(false);
                    loadContentInBrowser(bioUrl, new ContentLoader());
                }
                break;
            case R.id.action_web:
                if (viewFormat == ViewFormat.TEXT) {
                    viewFormat = ViewFormat.WEB;
                    settings.setLoadWithOverviewMode(true);
                    settings.setUseWideViewPort(true);
                    loadUrlInBrowser(browser, bioUrl);
                }
                break;
            case android.R.id.home:
                finish();
                break;
        	default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    protected void loadContentInBrowser(final String url, final Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.parse(new URL(url).openStream(), "utf-8", url);
                    Elements elements = document.select("div#biografie_seite");
                    String bioData = elements.toString();
                    callback.execute(bioData);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    private interface Callback {
        public void execute(String content);
    }
    
    private class ContentLoader implements Callback {
        @Override
        public void execute(final String content) {
            BioActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String mimeType = "text/html; charset=UTF-8";
                    browser.loadData(content, mimeType, null);
                }
            });
        }
    }
    
    protected void loadUrlInBrowser(WebView browser, String url) {
        browser.loadUrl(url);
    }
    
    private class SimpleWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
        
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        
    }
    
}
