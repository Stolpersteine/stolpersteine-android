package com.dreiri.stolpersteine.activities;

import java.io.IOException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.dreiri.stolpersteine.R;
import com.dreiri.stolpersteine.api.model.Location;
import com.dreiri.stolpersteine.api.model.Person;
import com.dreiri.stolpersteine.api.model.Stolperstein;

public class InfoActivity extends Activity {

    Stolperstein stolperstein;
    Person person;
    Location location;
    String bioUrl;
    String bioData;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.fragment_info);
        
        Intent intent = getIntent();
        if (intent.hasExtra("stolperstein")) {
            stolperstein = intent.getParcelableExtra("stolperstein");
            readProperties(stolperstein);
        }
        
        TextView textViewName = (TextView) findViewById(R.id.textViewName);
        TextView textViewAddress = (TextView) findViewById(R.id.textViewAddress);
        final Button btnBio = (Button) findViewById(R.id.btnBio);
        
        textViewName.setText(person.getNameAsString());
        textViewAddress.setText(location.getAddressAsString());
//        UIEnhancer.insertImageIntoTextView(this, R.drawable.img_doughface, R.id.textViewBio);
        
        // pre-caching
        Thread downloadThread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Document document = Jsoup.parse(new URL(bioUrl).openStream(), "utf-8", bioUrl);
                    Elements elements = document.select("div#biografie_seite");
                    bioData = elements.toString();
                    btnBio.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(InfoActivity.this, BioActivity.class);
                            intent.putExtra("bioData", bioData);
                            intent.putExtra("bioUrl", bioUrl);
                            startActivity(intent);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        downloadThread.start();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.info, menu);
        return true;
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("stolperstein", stolperstein);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("stolperstein")) {
            stolperstein = savedInstanceState.getParcelable("stolperstein");
            readProperties(stolperstein);
        }
    }
    
    private void readProperties(Stolperstein stolperstein) {
        person = stolperstein.getPerson();
        location = stolperstein.getLocation();
        bioUrl = person.getBiographyUri().toString();
    }

}