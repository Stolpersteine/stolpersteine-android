package com.dreiri.stolpersteine.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import android.app.Activity;
import android.content.Intent;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class MapActivity extends Activity implements SynchronizationController.Listener, OnInfoWindowClickListener, ClusterManager.OnClusterClickListener<Stolperstein>, ClusterManager.OnClusterItemClickListener<Stolperstein> {
    
	private LatLng berlinLatLng;
	private int berlinZoom;
	private final int autoCompleteDropDownListSize = 10;
	private final int autoCompleteActivationMinLength = 3;
	private NetworkService networkService;
	private SynchronizationController synchronizationController;
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
		    map.setOnInfoWindowClickListener(this);
		    
		    clusterManager = new ClusterManager<Stolperstein>(this, map);
		    clusterManager.setAlgorithm(new GridBasedAlgorithm<Stolperstein>());
	        clusterManager.setRenderer(new StolpersteinRenderer());
	        clusterManager.setOnClusterClickListener(this);
	        clusterManager.setOnClusterItemClickListener(this);
		    map.setOnCameraChangeListener(clusterManager);
		    map.setOnMarkerClickListener(clusterManager);
		}

		// Start synchronizing data
		networkService = new NetworkService(this);
		networkService.getDefaultSearchData().setCity("Berlin");
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
	
	private LatLng getLocationLatLng(int location) {
	    String[] locationCoordinates = getResources().getStringArray(location);
	    double lat = Double.valueOf(locationCoordinates[0]);
	    double lng = Double.valueOf(locationCoordinates[1]);
	    return new LatLng(lat, lng);
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
//    	Log.i("Stolpersteine", "marker");
//		ArrayList<Stolperstein> stolpersteine = mapClusterController.getItems(marker);
//		if (!stolpersteine.isEmpty()) {
//			Intent intent = new Intent(MapActivity.this, InfoActivity.class);
//			intent.putParcelableArrayListExtra("stolpersteine", stolpersteine);
//			startActivity(intent);
//		}
	}
	
    @Override
    public boolean onClusterClick(Cluster<Stolperstein> cluster) {
//    	Log.i("Stolpersteine", "cluster");
    	
    	Intent intent = new Intent(MapActivity.this, InfoActivity.class);
    	ArrayList<Stolperstein> stolpersteine = new ArrayList<Stolperstein>(cluster.getItems());
    	intent.putParcelableArrayListExtra("stolpersteine", stolpersteine);
    	startActivity(intent);

    	return false;
    }

    @Override
    public boolean onClusterItemClick(Stolperstein stolperstein) {
//    	Log.i("Stolpersteine", "item");
    	
    	Intent intent = new Intent(MapActivity.this, InfoActivity.class);
    	ArrayList<Stolperstein> stolpersteine = new ArrayList<Stolperstein>();
    	stolpersteine.add(stolperstein);
    	intent.putParcelableArrayListExtra("stolpersteine", stolpersteine);
    	startActivity(intent);
    	
        return false;
    }
	
	@Override
    public void onStolpersteineAdded(List<Stolperstein> stolpersteine) {
	    if (stolpersteine != null) {
	    	clusterManager.addItems(stolpersteine);
	    	clusterManager.cluster();
	    }
    }
	
    private class StolpersteinRenderer extends DefaultClusterRenderer<Stolperstein> {
        public StolpersteinRenderer() {
            super(getApplicationContext(), map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(Stolperstein stolperstein, MarkerOptions markerOptions) {
        	super.onBeforeClusterItemRendered(stolperstein, markerOptions);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<Stolperstein> cluster, MarkerOptions markerOptions) {
        	super.onBeforeClusterRendered(cluster, markerOptions);
        }
    }
}