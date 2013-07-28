package com.dreiri.stolpersteine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.dreiri.stolpersteine.models.Location;
import com.dreiri.stolpersteine.models.Person;
import com.dreiri.stolpersteine.models.Stolperstein;
import com.dreiri.stolpersteine.utils.UIEnhancer;

public class InfoActivity extends Activity {

    Stolperstein stolperstein;
    Person person;
    Location location;
    String bioUrl;
    
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
        TextView textViewBio = (TextView) findViewById(R.id.textViewBio);
        
        textViewName.setText(person.name());
        textViewAddress.setText(location.address());
        UIEnhancer.insertImageIntoTextView(this, R.drawable.img_doughface, R.id.textViewBio);
        
        textViewBio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.dreiri.stolpersteine.BioActivity");
                intent.putExtra("bioUrl", bioUrl);
                startActivity(intent);
            }
        });
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
        bioUrl = person.getBiography();
    }

}