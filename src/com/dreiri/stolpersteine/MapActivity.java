
package com.dreiri.stolpersteine;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dreiri.stolpersteine.utils.LocationFinder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends Activity {
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragmentMap)).getMap();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_positioning:
                LocationFinder locationFinder = new LocationFinder(MapActivity.this);
                LatLng currentLocation = new LatLng(locationFinder.getLat(), locationFinder.getLng());
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                map.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
                break;
            default:
                break;
        }
        return true;
    }

}
