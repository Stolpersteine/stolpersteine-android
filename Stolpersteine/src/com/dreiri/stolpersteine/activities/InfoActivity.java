package com.dreiri.stolpersteine.activities;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.dreiri.stolpersteine.R;
import com.dreiri.stolpersteine.api.model.Location;
import com.dreiri.stolpersteine.api.model.Person;
import com.dreiri.stolpersteine.api.model.Stolperstein;
import com.dreiri.stolpersteine.utils.StolpersteinAdapter;

public class InfoActivity extends Activity {

    ArrayList<Stolperstein> stolpersteine;
    Person person;
    Location location;
    String bioUrl;
    String bioData;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_info);
        ListView listView = (ListView) findViewById(R.id.list);
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
    
    private void readProperties(Stolperstein stolperstein) {
        person = stolperstein.getPerson();
        location = stolperstein.getLocation();
        bioUrl = person.getBiographyUri().toString();
    }

}