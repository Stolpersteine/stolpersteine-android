package com.dreiri.stolpersteine.activities;

import java.util.ArrayList;

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
import com.dreiri.stolpersteine.components.StolpersteinAdapter;

public class InfoActivity extends Activity {

    ArrayList<Stolperstein> stolpersteine;
    String bioUrl;
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
                    Intent intent = new Intent(InfoActivity.this, BioActivity.class);
                    intent.putExtra("bioUrl", bioUrl);
                    startActivity(intent);
                }
            });
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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