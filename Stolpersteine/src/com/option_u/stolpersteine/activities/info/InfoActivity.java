package com.option_u.stolpersteine.activities.info;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.option_u.stolpersteine.R;
import com.option_u.stolpersteine.activities.bio.BioActivity;
import com.option_u.stolpersteine.api.model.Stolperstein;

public class InfoActivity extends Activity {

    public static Intent createIntent(Context context, ArrayList<Stolperstein> stolpersteine) {
        Intent intent = new Intent(context, InfoActivity.class);
        intent.putParcelableArrayListExtra("stolpersteine", stolpersteine);
        
        return intent;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_info);
        final ListView listView = (ListView) findViewById(R.id.list);
        Intent intent = getIntent();
        if (intent.hasExtra("stolpersteine")) {
            ArrayList<Stolperstein> stolpersteine = intent.getParcelableArrayListExtra("stolpersteine");
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
                    Stolperstein stolperstein = (Stolperstein)listView.getItemAtPosition(position);
                    String bioUrl = stolperstein.getPerson().getBiographyUri().toString();
                    startActivity(BioActivity.createIntent(InfoActivity.this, bioUrl));
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