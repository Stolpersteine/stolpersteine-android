package com.dreiri.stolpersteine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

public class BioActivity extends Activity {

    WebView browser;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_web);
        Intent intent = getIntent();
        String bioUrl = intent.getStringExtra("bioUrl");
        browser = (WebView) findViewById(R.id.webkit);
        browser.loadUrl(bioUrl);
    }
    
}
