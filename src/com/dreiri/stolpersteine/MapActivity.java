
package com.dreiri.stolpersteine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dreiri.stolpersteine.models.Location;
import com.dreiri.stolpersteine.models.Person;
import com.dreiri.stolpersteine.models.Stolperstein;
import com.dreiri.stolpersteine.utils.Client;
import com.dreiri.stolpersteine.utils.LocationFinder;
import com.dreiri.stolpersteine.utils.OnJSONResponse;
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
    StringBuilder baseUri = new StringBuilder("https://stolpersteine-api.eu01.aws.af.cm/v1");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragmentMap)).getMap();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(berlin, 12));
        map.setOnInfoWindowClickListener(new InfoWindowHandler());
        
        Client client = new Client();
        StringBuilder queryUri = baseUri.append("/stolpersteine?offset=0&limit=10");
        client.getJSONFeed(queryUri.toString(), new JSONResponseHandler());
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
    
    private class JSONResponseHandler implements OnJSONResponse {

        @Override
        public void execute(JSONArray jsonArray) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject;
                try {
                    jsonObject = jsonArray.getJSONObject(i);
                    JSONObject jsonPerson = jsonObject.getJSONObject("person");
                    JSONObject jsonLocation = jsonObject.getJSONObject("location");
                    JSONObject jsonCoordinates = jsonLocation.getJSONObject("coordinates");
                    LatLng coordinates = new LatLng(jsonCoordinates.getDouble("latitude"), jsonCoordinates.getDouble("longitude"));
                    
                    Person person = new Person(jsonPerson.getString("firstName"), jsonPerson.getString("lastName"), jsonPerson.getString("biographyUrl"));
                    Location location = new Location(jsonLocation.getString("street"), jsonLocation.getString("zipCode"), jsonLocation.getString("city"), coordinates);
                    Stolperstein stolperstein = new Stolperstein(person, location);
                    
                    MarkerOptions markerOptions = new MarkerOptions().position(coordinates).title(person.name()).snippet(location.address()).icon(BitmapDescriptorFactory.fromResource(R.drawable.stolpersteine_tile)); 
                    Marker marker = map.addMarker(markerOptions);
                    
                    richMapMarker.setProperty(marker, stolperstein);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        
    }
    
    private class InfoWindowHandler implements OnInfoWindowClickListener {

        @Override
        public void onInfoWindowClick(Marker marker) {
            if (richMapMarker.hasMarker(marker)) {
                Stolperstein stolperstein = (Stolperstein) richMapMarker.getProperty(marker);
                Intent intent = new Intent("com.dreiri.stolpersteine.InfoActivity");
                intent.putExtra("stolperstein", stolperstein);
                startActivity(intent);
            }
        }
        
    }

}