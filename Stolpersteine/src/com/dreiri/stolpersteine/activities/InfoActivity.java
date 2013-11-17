package com.dreiri.stolpersteine.activities;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.dreiri.stolpersteine.R;
import com.dreiri.stolpersteine.api.model.Stolperstein;
import com.dreiri.stolpersteine.utils.StolpersteinAdapter;

public class InfoActivity extends Activity {

    ArrayList<Stolperstein> stolpersteine;
    String bioUrl;
    String bioData;
    ListView listView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_info);
        listView = (ListView) findViewById(R.id.list);
        Intent intent = getIntent();
        if (intent.hasExtra("stolpersteine")) {
            stolpersteine = intent.getParcelableArrayListExtra("stolpersteine");
            Integer number_found = stolpersteine.size();
            if (number_found > 1) {
                actionBar.setTitle(Integer.toString(number_found) + " Stolpersteine");
            } else {
                actionBar.setTitle(Integer.toString(number_found) + " Stolperstein");
            }
            StolpersteinAdapter stolpersteinAdapter = new StolpersteinAdapter(this, stolpersteine);
            listView.setAdapter(stolpersteinAdapter);
            listView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Stolperstein stolperstein = (Stolperstein) listView.getItemAtPosition(position);
                    bioUrl = stolperstein.getPerson().getBiographyUri().toString();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Document document = Jsoup.parse(new URL(bioUrl).openStream(), "utf-8", bioUrl);
                                Elements elements = document.select("div#biografie_seite");
                                bioData = elements.toString();
                                Intent intent = new Intent(InfoActivity.this, BioActivity.class);
                                intent.putExtra("bioData", bioData);
                                intent.putExtra("bioUrl", bioUrl);
                                startActivity(intent);
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            });
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.info, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
        }
        return true;
    }
    
}