package com.option_u.stolpersteine.activities.cards;

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
import com.option_u.stolpersteine.StolpersteineApplication;
import com.option_u.stolpersteine.activities.description.DescriptionActivity;
import com.option_u.stolpersteine.api.model.Stolperstein;

public class CardsActivity extends Activity {

    private static final String EXTRA_NAME = "stolpersteine";

    public static Intent createIntent(Context context, ArrayList<Stolperstein> stolpersteine) {
        Intent intent = new Intent(context, CardsActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_NAME, stolpersteine);

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
        if (intent.hasExtra(EXTRA_NAME)) {
            ArrayList<Stolperstein> stolpersteine = intent.getParcelableArrayListExtra(EXTRA_NAME);
            Integer numStolpersteine = stolpersteine.size();
            int resourceID = (numStolpersteine > 1) ? R.string.app_stolpersteine_plural : R.string.app_stolpersteine_singular;
            String title = getResources().getString(resourceID);
            actionBar.setTitle(Integer.toString(numStolpersteine) + " " + title);

            StolpersteineAdapter stolpersteinAdapter = new StolpersteineAdapter(this, stolpersteine);
            listView.setAdapter(stolpersteinAdapter);
            listView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Stolperstein stolperstein = (Stolperstein) listView.getItemAtPosition(position);
                    String bioUrl = stolperstein.getPerson().getBiographyUri().toString();
                    startActivity(DescriptionActivity.createIntent(CardsActivity.this, bioUrl));
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        StolpersteineApplication stolpersteineApplication = (StolpersteineApplication) getApplication();
        stolpersteineApplication.trackView(this.getClass());
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