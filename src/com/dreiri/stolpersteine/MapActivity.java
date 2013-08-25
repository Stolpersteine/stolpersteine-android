package com.dreiri.stolpersteine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dreiri.stolpersteine.callback.Callback;
import com.dreiri.stolpersteine.client.StolpersteineClient;
import com.dreiri.stolpersteine.models.Stolperstein;
import com.dreiri.stolpersteine.utils.LocationFinder;
import com.dreiri.stolpersteine.utils.RichMapMarker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity {
    GoogleMap map;
    LatLng berlin = new LatLng(52.5191710, 13.40609120);
    RichMapMarker richMapMarker = new RichMapMarker();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragmentMap)).getMap();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(berlin, 12));
        map.setOnInfoWindowClickListener(new InfoWindowHandler());
        
        StolpersteineClient stolpersteineClient = new StolpersteineClient();
        stolpersteineClient.getNumbersOfResultsAndHandleThem(50, new Callback() {
            
            @Override
            public void handle(Object object) {
                Stolperstein stolperstein = (Stolperstein) object;
                MarkerOptions markerOptions = new MarkerOptions().position(stolperstein.coordinates()).title(stolperstein.name()).snippet(stolperstein.address()).icon(BitmapDescriptorFactory.fromResource(R.drawable.stolpersteine_tile));
                Marker marker = map.addMarker(markerOptions);
                richMapMarker.setProperty(marker, stolperstein);
            }
        });
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
    
    private class InfoWindowHandler implements OnInfoWindowClickListener {

        @Override
        public void onInfoWindowClick(Marker marker) {
            if (richMapMarker.hasMarker(marker)) {
                Stolperstein stolperstein = (Stolperstein) richMapMarker.getProperty(marker);
                Intent intent = new Intent(MapActivity.this, InfoActivity.class);
                intent.putExtra("stolperstein", stolperstein);
                startActivity(intent);
            }
        }
        
    }

}