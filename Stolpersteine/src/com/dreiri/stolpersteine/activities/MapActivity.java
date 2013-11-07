package com.dreiri.stolpersteine.activities;

import java.util.List;

import org.csdgn.util.KDTree;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dreiri.stolpersteine.R;
import com.dreiri.stolpersteine.api.NetworkService;
import com.dreiri.stolpersteine.api.NetworkService.Callback;
import com.dreiri.stolpersteine.api.SynchronizationController;
import com.dreiri.stolpersteine.api.model.Stolperstein;
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
    NetworkService networkService = new NetworkService();
    SynchronizationController stolpersteineClient = new SynchronizationController(networkService);
    GoogleMap map;
    LatLng berlin = new LatLng(52.5191710, 13.40609120);
    RichMapMarker richMapMarker = new RichMapMarker();
    KDTree<Stolperstein> tree = new KDTree<Stolperstein>(2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragmentMap)).getMap();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(berlin, 12));
        map.setOnInfoWindowClickListener(new InfoWindowHandler());
        
        stolpersteineClient.retrieveStolpersteine(new Callback() {
            
            @Override
            public void handle(List<Stolperstein> stolpersteine) {
                for (Stolperstein stolperstein : stolpersteine) {
                	LatLng coordinates = stolperstein.getLocation().getCoordinates();
                	double[] key = new double[] {coordinates.latitude, coordinates.longitude}; 
                	tree.add(key, stolperstein);
                }
                
                double[] bottomLeft = new double[] {52.50, 13.40};
                double[] topRight = new double[] {52.52, 13.41};
                List<Stolperstein> list = tree.getRange(bottomLeft, topRight);
                for (Stolperstein stolperstein : list) {
					MarkerOptions markerOptions = new MarkerOptions()
							.position(stolperstein.getLocation().getCoordinates())
							.title(stolperstein.getPerson().getNameAsString())
							.snippet(stolperstein.getLocation().getAddressAsString())
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.stolpersteine_tile));
					Marker marker = map.addMarker(markerOptions);
					richMapMarker.addProperty(marker, stolperstein);
                }
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
                Stolperstein stolperstein = (Stolperstein) richMapMarker.getProperties(marker).get(0);
                Intent intent = new Intent(MapActivity.this, InfoActivity.class);
                intent.putExtra("stolperstein", stolperstein);
                startActivity(intent);
            }
        }
        
    }

}