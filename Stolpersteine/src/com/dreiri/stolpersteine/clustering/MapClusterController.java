package com.dreiri.stolpersteine.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.csdgn.util.KDTree;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

public class MapClusterController<T> {
	private GoogleMap map;
	private KDTree<ClusterMarker<T>> allMarkersTree = new KDTree<ClusterMarker<T>>(2);
	private HashMap<Marker, ClusterMarker<T>> markerMap = new HashMap<Marker, ClusterMarker<T>>();
	public boolean debug;

	public MapClusterController(GoogleMap map) {
		this.map = map;
	}

	public void addMarkers(List<MarkerOptions> optionsList, List<T> items) {
		assert (optionsList.size() == items.size());

		for (int i = 0; i < optionsList.size(); i++) {
			MarkerOptions options = optionsList.get(i);
			ClusterMarker<T> clusterMarker = new ClusterMarker<T>(options, items.get(i));
			LatLng coordinates = options.getPosition();
			double[] key = new double[] { coordinates.latitude, coordinates.longitude };
			allMarkersTree.add(key, clusterMarker);
		}

		update(null);
	}

	public void update(Callback callback) {
		double[] bottomLeft = new double[] { 52.50, 13.40 };
		double[] topRight = new double[] { 52.52, 13.41 };
		List<ClusterMarker<T>> clusterMarkers = allMarkersTree.getRange(bottomLeft, topRight);
		for (ClusterMarker<T> clusterMarker : clusterMarkers) {
			addMarker(clusterMarker);
		}

		if (debug) {
			LatLngBounds visibleRect = map.getProjection().getVisibleRegion().latLngBounds;
			PolygonOptions polygonOptions = new PolygonOptions()
				.add(visibleRect.northeast)
				.add(new LatLng(visibleRect.southwest.latitude, visibleRect.northeast.longitude))
				.add(visibleRect.southwest)
				.add(new LatLng(visibleRect.northeast.latitude, visibleRect.southwest.longitude))
				.strokeWidth(1)
				.strokeColor(Color.BLUE);
			map.addPolygon(polygonOptions);
	
			Log.i("Stolpersteine", "" + (visibleRect.northeast.latitude - visibleRect.southwest.latitude));
			Log.i("Stolpersteine", "" + (visibleRect.northeast.longitude - visibleRect.southwest.longitude));
			
			LatLngBounds cellRect = new LatLngBounds(visibleRect.southwest, new LatLng(visibleRect.southwest.latitude + 0.05, visibleRect.southwest.longitude + 0.05));
			polygonOptions = new PolygonOptions()
				.add(cellRect.northeast)
				.add(new LatLng(cellRect.southwest.latitude, cellRect.northeast.longitude))
				.add(cellRect.southwest)
				.add(new LatLng(cellRect.northeast.latitude, cellRect.southwest.longitude))
				.strokeWidth(1)
				.strokeColor(Color.BLUE);
			map.addPolygon(polygonOptions);
		}

		if (callback != null) {
			callback.onUpdateComplete();
		}
	}
	
	private void addMarker(ClusterMarker<T> clusterMarker) {
		Marker marker = map.addMarker(clusterMarker.options);
		markerMap.put(marker, clusterMarker);
	}

	public ArrayList<T> getItems(Marker marker) {
		ClusterMarker<T> clusterMarker = markerMap.get(marker);
		ArrayList<T> items = (clusterMarker != null) ? clusterMarker.items : new ArrayList<T>();
		return items;
	}

	public interface Callback {
		public void onUpdateComplete();
	}

	private class ClusterMarker<Item> {
		public final MarkerOptions options;
		public final ArrayList<Item> items = new ArrayList<Item>(1);

		public ClusterMarker(MarkerOptions options, Item item) {
			this.options = options;
			this.items.add(item);
		}
	}
}
