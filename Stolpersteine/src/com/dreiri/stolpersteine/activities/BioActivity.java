package com.dreiri.stolpersteine.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;

import com.dreiri.stolpersteine.R;

public class BioActivity extends Activity {

    WebView browser;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_bio);
        Intent intent = getIntent();
        final String bioData = intent.getStringExtra("bioData");
        browser = (WebView) findViewById(R.id.webkit);
        browser.getSettings().setBuiltInZoomControls(true);
        browser.getSettings().setDisplayZoomControls(false);
        String mimeType = "text/html; charset=UTF-8";
        browser.loadData(bioData, mimeType, null);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bio, menu);
        return true;
    }

}
