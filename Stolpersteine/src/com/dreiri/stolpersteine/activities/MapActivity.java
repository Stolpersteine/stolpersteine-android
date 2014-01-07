package com.dreiri.stolpersteine.activities;

import java.util.List;
import java.util.ListIterator;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.dreiri.stolpersteine.R;
import com.dreiri.stolpersteine.api.NetworkService;
import com.dreiri.stolpersteine.api.RetrieveStolpersteineRequest.Callback;
import com.dreiri.stolpersteine.api.SearchData;
import com.dreiri.stolpersteine.api.SynchronizationController;
import com.dreiri.stolpersteine.api.model.Stolperstein;
import com.dreiri.stolpersteine.utils.AndroidVersionsUnification;
import com.dreiri.stolpersteine.utils.LocationFinder;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

public class MapActivity extends Activity implements OnInfoWindowClickListener, SynchronizationController.Listener {
    
	private LatLng berlinLatLng;
	private int berlinZoom;
	private final int autoCompleteDropDownListSize = 10;
	private final int autoCompleteActivationMinLength = 3;
	private NetworkService networkService;
	private SynchronizationController synchronizationController;
//	private MapClusterController<Stolperstein> mapClusterController;
	private GoogleMap map;
	private ClusterManager<Stolperstein> clusterManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		// Set up map and clustering
		map = ((MapFragment)getFragmentManager().findFragmentById(R.id.fragmentMap)).getMap();
		if (map != null) {
		    berlinLatLng = getLocationLatLng(R.array.Berlin);
		    berlinZoom = 12;
		    CameraUpdate region = CameraUpdateFactory.newLatLngZoom(berlinLatLng, berlinZoom);
		    map.moveCamera(region);
//		    map.setOnInfoWindowClickListener(this);
		    //mapClusterController = new MapClusterController<Stolperstein>(map);
		    
		    clusterManager = new ClusterManager<Stolperstein>(this, map);
		    map.setOnCameraChangeListener(clusterManager);
//		    map.setOnMarkerClickListener(clusterManager);
		}

		// Start synchronizing data
		networkService = new NetworkService(this);
		synchronizationController = new SynchronizationController(networkService);
		synchronizationController.setListener(this);
		synchronizationController.synchronize();

		// Search interface
		final AutoCompleteTextView autoCompleteTextViewQuery = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewQuery);
		Drawable background = new ColorDrawable(Color.GRAY);
		background.setAlpha(128);
		AndroidVersionsUnification.setBackgroundForView(autoCompleteTextViewQuery, background);
		autoCompleteTextViewQuery.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				SearchData searchData = new SearchData();
				searchData.setKeyword(s.toString());
				networkService.retrieveStolpersteine(searchData, 0, autoCompleteDropDownListSize, new Callback() {
					@Override
					public void onStolpersteineRetrieved(List<Stolperstein> stolpersteine) {
					    String[] suggestions = new String[stolpersteine.size()];
					    ListIterator<Stolperstein> iterator = stolpersteine.listIterator();
					    while (iterator.hasNext()) {
					        int idx = iterator.nextIndex();
					        Stolperstein matchedStolperstein = iterator.next();
					        String name = matchedStolperstein.getPerson().getNameAsString();
					        String street = matchedStolperstein.getLocation().getStreet();
					        suggestions[idx] = name + ", " + street;
                        }
					    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MapActivity.this, android.R.layout.simple_dropdown_item_1line, suggestions);
					    autoCompleteTextViewQuery.setThreshold(autoCompleteActivationMinLength);
					    autoCompleteTextViewQuery.setAdapter(adapter);
					}
				});
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

	@Override
	public void onInfoWindowClick(Marker marker) {
//		ArrayList<Stolperstein> stolpersteine = mapClusterController.getItems(marker);
//		if (!stolpersteine.isEmpty()) {
//			Intent intent = new Intent(MapActivity.this, InfoActivity.class);
//			intent.putParcelableArrayListExtra("stolpersteine", stolpersteine);
//			startActivity(intent);
//		}
	}
	
	@Override
    public void onStolpersteineAdded(List<Stolperstein> stolpersteine) {
	    if (stolpersteine != null) {
//            ArrayList<MarkerOptions> optionsList = new ArrayList<MarkerOptions>(stolpersteine.size());
//            for (Stolperstein stolperstein : stolpersteine) {
//                MarkerOptions markerOptions = new MarkerOptions().position(stolperstein.getLocation().getCoordinates())
//                        .title(stolperstein.getPerson().getNameAsString())
//                        .snippet(stolperstein.getLocation().getAddressAsString())
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.stolpersteine_tile));
//                optionsList.add(markerOptions);
//            }
//            mapClusterController.addMarkers(optionsList, stolpersteine);
	    	
	    	clusterManager.addItems(stolpersteine);
	    	clusterManager.cluster();
	    }
    }
	
	private LatLng getLocationLatLng(int location) {
	    String[] locationCoordinates = getResources().getStringArray(location);
	    double lat = Double.valueOf(locationCoordinates[0]);
	    double lng = Double.valueOf(locationCoordinates[1]);
	    return new LatLng(lat, lng);
	}
}