
package com.dreiri.stolpersteine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dreiri.stolpersteine.utils.Client;
import com.dreiri.stolpersteine.utils.JSONResponsible;
import com.dreiri.stolpersteine.utils.LocationFinder;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_map);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragmentMap)).getMap();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(berlin, 12));
        map.setOnInfoWindowClickListener(new InfoWindowHandler());
        String uri = "https://stolpersteine-api.eu01.aws.af.cm/v1/stolpersteine?offset=0&limit=1";
        Client client = new Client();
        client.getJSONFeed(uri, new JSONResponseHandler());
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
    
    private class JSONResponseHandler implements JSONResponsible {

        @Override
        public void actOnJSON(JSONArray jsonArray) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject;
                try {
                    jsonObject = jsonArray.getJSONObject(i);
                    JSONObject person = jsonObject.getJSONObject("person");
                    JSONObject location = jsonObject.getJSONObject("location");
                    JSONObject coordinates = location.getJSONObject("coordinates");
                    String name = person.getString("firstName") + " " + person.getString("lastName");
                    String address = location.getString("street") + ", " + location.getString("zipCode") + " " + location.getString("city");
                    LatLng position = new LatLng(coordinates.getDouble("latitude"), coordinates.getDouble("longitude"));
                    MarkerOptions markerOptions = new MarkerOptions().position(position).title(name).snippet(address).icon(BitmapDescriptorFactory.fromResource(R.drawable.stolpersteine_tile)); 
                    map.addMarker(markerOptions);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        
    }
    
    private class InfoWindowHandler implements OnInfoWindowClickListener {

        @Override
        public void onInfoWindowClick(Marker marker) {
            
        }
        
    }

}
