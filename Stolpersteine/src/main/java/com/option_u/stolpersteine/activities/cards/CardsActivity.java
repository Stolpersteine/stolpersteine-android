package com.option_u.stolpersteine.activities.cards;

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

import com.option_u.stolpersteine.R;
import com.option_u.stolpersteine.StolpersteineApplication;
import com.option_u.stolpersteine.activities.description.DescriptionActivity;
import com.option_u.stolpersteine.api.model.Stolperstein;

public class CardsActivity extends Activity {

    public static Intent createIntent(Activity activity, ArrayList<Stolperstein> stolpersteine) {
        StolpersteineApplication stolpersteineApplication = (StolpersteineApplication) activity.getApplication();
        stolpersteineApplication.setStolpersteine(stolpersteine);
        return new Intent(activity, CardsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_info);
        ListView listView = (ListView) findViewById(R.id.list);
        StolpersteineApplication stolpersteineApplication = (StolpersteineApplication) getApplication();
        if (stolpersteineApplication.hasStolperstein()) {
            showStolpersteine(stolpersteineApplication.getStolpersteine(), actionBar, listView);
            stolpersteineApplication.clearStolpersteine();
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

    private void showStolpersteine(ArrayList<Stolperstein> stolpersteine, ActionBar actionBar, final ListView listView) {
        Integer numStolpersteine = stolpersteine.size();
        String title = getResources().getQuantityString(R.plurals.stolpersteine, numStolpersteine);
        actionBar.setTitle(Integer.toString(numStolpersteine) + " " + title);

        StolpersteineAdapter stolpersteinAdapter = new StolpersteineAdapter(this, stolpersteine);
        listView.setAdapter(stolpersteinAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Stolperstein stolperstein = (Stolperstein)listView.getItemAtPosition(position);
                String bioUrl = stolperstein.getPerson().getBiographyUri().toString();
                if (bioUrl.trim().length() > 0) {
                    startActivity(DescriptionActivity.createIntent(CardsActivity.this, bioUrl));
                }
            }
        });
    }

}