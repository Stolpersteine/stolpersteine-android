package com.dreiri.stolpersteine.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.dreiri.stolpersteine.R;

public class BioActivity extends Activity {
    
    protected enum ViewFormat {TEXT, WEB};
    ViewFormat viewFormat;
    WebView browser;
    WebSettings settings;
    String bioData;
    String bioUrl;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_bio);
        Intent intent = getIntent();
        bioData = intent.getStringExtra("bioData");
        bioUrl = intent.getStringExtra("bioUrl");
        
        browser = (WebView) findViewById(R.id.webview);
        settings = browser.getSettings();
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        
        viewFormat = ViewFormat.TEXT;
        loadContentInBrowser(browser, bioData);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
                    loadContentInBrowser(browser, bioData);
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
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    protected void loadContentInBrowser(WebView browser, String content) {
        String mimeType = "text/html; charset=UTF-8";
        browser.loadData(content, mimeType, null);
    }
    
    protected void loadUrlInBrowser(WebView browser, String url) {
        browser.loadUrl(url);
    }

}
