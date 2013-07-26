package com.dreiri.stolpersteine;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;

public class BioActivity extends Activity {

    WebView browser;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_web);
        Intent intent = getIntent();
        final String bioUrl = intent.getStringExtra("bioUrl");
        browser = (WebView) findViewById(R.id.webkit);
        browser.getSettings().setBuiltInZoomControls(true);
        browser.getSettings().setDisplayZoomControls(false);
        
        Thread downloadThread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Document document = Jsoup.connect(bioUrl).get();
//                    Elements elements = document.select("div.panel-pane pane-entity-view pane-person");
//                    Elements elements = document.select("div.panel-content");
                    Elements elements = document.select("div#biografie_seite");
                    String data = elements.toString();
                    String mimeType = "text/html";
                    String encoding = "utf-8";
                    browser.loadData(data, mimeType, encoding);
                } catch (IOException e) {
                    e.printStackTrace();
                    browser.loadUrl(bioUrl);
                }
            }
        };
        
        downloadThread.start();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bio, menu);
        return true;
    }

}
