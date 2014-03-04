package com.dreiri.stolpersteine.activities.map;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.dreiri.stolpersteine.R;
import com.dreiri.stolpersteine.activities.info.InfoActivity;
import com.dreiri.stolpersteine.api.StolpersteinNetworkService;
import com.dreiri.stolpersteine.api.SynchronizationController;
import com.dreiri.stolpersteine.api.model.Stolperstein;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

public class MapActivity extends Activity implements SynchronizationController.Listener, ClusterManager.OnClusterClickListener<Stolperstein>, ClusterManager.OnClusterItemClickListener<Stolperstein> {
    
	private SynchronizationController synchronizationController;
	private GoogleMap map;
	private ClusterManager<Stolperstein> clusterManager;
	private LocationService locationService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		// Set up map and clustering
		map = ((MapFragment)getFragmentManager().findFragmentById(R.id.fragmentMap)).getMap();
		if (map != null) {
		    map.getUiSettings().setZoomControlsEnabled(false);
		    map.getUiSettings().setZoomGesturesEnabled(true);
		    
		    // Clustering
		    clusterManager = new ClusterManager<Stolperstein>(this, map);
//		    clusterManager.setAlgorithm(new GridBasedAlgorithm<Stolperstein>());
	        clusterManager.setRenderer(new ClusterRenderer(this, map, clusterManager));
	        clusterManager.setOnClusterClickListener(this);
	        clusterManager.setOnClusterItemClickListener(this);
		    map.setOnCameraChangeListener(clusterManager);
		    map.setOnMarkerClickListener(clusterManager);
		    
		    // User location
	        locationService = new LocationService(this, map, R.array.berlin);
	        locationService.zoomToRegion();
		}

		// Start synchronizing data
		StolpersteinNetworkService networkService = new StolpersteinNetworkService(this);
		networkService.getDefaultSearchData().setCity("Berlin");
		synchronizationController = new SynchronizationController(networkService);
		synchronizationController.setListener(this);
		synchronizationController.synchronize();		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		locationService.start();
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		locationService.stop();
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map, menu);
		SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView)menu.findItem(R.id.search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		String contentProviderAuthority = "com.dreiri.stolpersteine.suggestions";
		SearchSuggestionProvider searchSuggestionProvider = (SearchSuggestionProvider)getContentResolver().acquireContentProviderClient(contentProviderAuthority).getLocalContentProvider();
		searchSuggestionProvider.setNetworkService(synchronizationController.getNetworkService());
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
        if (itemId == R.id.action_positioning) {
            locationService.zoomToCurrentLocation(16);
        }
        
		return true;
	}

    @Override
    public boolean onClusterClick(Cluster<Stolperstein> cluster) {
    	Intent intent = new Intent(MapActivity.this, InfoActivity.class);
    	ArrayList<Stolperstein> stolpersteine = new ArrayList<Stolperstein>(cluster.getItems());
    	intent.putParcelableArrayListExtra("stolpersteine", stolpersteine);
    	startActivity(intent);

    	return false;
    }

    @Override
    public boolean onClusterItemClick(Stolperstein stolperstein) {
    	Intent intent = new Intent(MapActivity.this, InfoActivity.class);
    	ArrayList<Stolperstein> stolpersteine = new ArrayList<Stolperstein>();
    	stolpersteine.add(stolperstein);
    	intent.putParcelableArrayListExtra("stolpersteine", stolpersteine);
    	startActivity(intent);
    	
        return false;
    }
	
	@Override
    public void onStolpersteineAdded(List<Stolperstein> stolpersteine) {
	    if (clusterManager != null && stolpersteine != null) {
	    	clusterManager.addItems(stolpersteine);
	    	clusterManager.cluster();
	    }
    }
}