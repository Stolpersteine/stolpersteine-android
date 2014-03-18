package com.dreiri.stolpersteine.activities.bio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.dreiri.stolpersteine.R;

public class BioActivity extends Activity {
    
    private enum ViewFormat {TEXT, WEB};
    private ViewFormat viewFormat;
    private WebView browser;
    private WebSettings settings;
    private String bioUrl;
    private static final String CSS_QUERY = "div#biografie_seite";
    
    public static Intent createIntent(Context context, String url) {
        Intent intent = new Intent(context, BioActivity.class);
        intent.putExtra("url", url);
        
        return intent;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_bio);
        Intent intent = getIntent();
        bioUrl = intent.getStringExtra("url");
        
        browser = (WebView) findViewById(R.id.webview);
        browser.setWebViewClient(new SimpleWebViewClient((ProgressBar) findViewById(R.id.progressBar)));
        settings = browser.getSettings();
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        
        viewFormat = ViewFormat.TEXT;
        loadContentInBrowser(browser, bioUrl, CSS_QUERY);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bio, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_view_format) {
            if (viewFormat == ViewFormat.TEXT) {
                viewFormat = ViewFormat.WEB;
                item.setTitle(R.string.action_item_text);
                item.setIcon(R.drawable.ic_action_view_as_text);
                settings.setLoadWithOverviewMode(true);
                settings.setUseWideViewPort(true);
                loadUrlInBrowser(browser, bioUrl);
            } else if (viewFormat == ViewFormat.WEB) {
                viewFormat = ViewFormat.TEXT;
                item.setTitle(R.string.action_item_web);
                item.setIcon(R.drawable.ic_action_view_as_web);
                settings.setLoadWithOverviewMode(false);
                settings.setUseWideViewPort(false);
                loadContentInBrowser(browser, bioUrl, CSS_QUERY);
            }
        } else if (itemId == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    
    protected void loadContentInBrowser(WebView browser, String url, String cssQuery) {
        new HTMLContentLoader(browser).loadContent(this, url, cssQuery);
    }
    
    protected void loadUrlInBrowser(WebView browser, String url) {
        browser.loadUrl(url);
    }

}
