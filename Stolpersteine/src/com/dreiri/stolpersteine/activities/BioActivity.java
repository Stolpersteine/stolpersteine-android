package com.dreiri.stolpersteine.activities;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

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
    String bioUrl;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_bio);
        Intent intent = getIntent();
        bioUrl = intent.getStringExtra("bioUrl");
        
        browser = (WebView) findViewById(R.id.webview);
        settings = browser.getSettings();
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        
        viewFormat = ViewFormat.TEXT;
        loadContentInBrowser(browser, bioUrl);
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
                    loadContentInBrowser(browser, bioUrl);
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
    
    protected void loadContentInBrowser(final WebView browser, final String url) {
        final String mimeType = "text/html; charset=UTF-8";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.parse(new URL(url).openStream(), "utf-8", url);
                    Elements elements = document.select("div#biografie_seite");
                    String bioData = elements.toString();
                    browser.loadData(bioData, mimeType, null);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    protected void loadUrlInBrowser(WebView browser, String url) {
        browser.loadUrl(url);
    }
    
}
