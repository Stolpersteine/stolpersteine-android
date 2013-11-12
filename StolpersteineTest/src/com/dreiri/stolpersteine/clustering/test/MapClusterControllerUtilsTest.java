package com.dreiri.stolpersteine.clustering.test;

import android.test.AndroidTestCase;

import com.dreiri.stolpersteine.clustering.MapClusterControllerUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class MapClusterControllerUtilsTest extends AndroidTestCase {
	
	public void testInsetBoundsExpand() {
		LatLngBounds bounds = new LatLngBounds(new LatLng(-1.0, -1.0), new LatLng(1.0, 1.0));
		LatLng location0 = new LatLng(-0.7, 0.8);
		LatLng location1 = new LatLng(1.3, -1.4);
		assertTrue("Wrong contains", bounds.contains(location0));
		assertFalse("Wrong contains", bounds.contains(location1));

		LatLng center = bounds.getCenter();
		LatLngBounds boundsInset = MapClusterControllerUtils.expandBounds(bounds, 0.5, 0.5);
		assertEquals("Wrong center", center, boundsInset.getCenter());
		assertTrue("Wrong contains", boundsInset.contains(location0));
		assertTrue("Wrong contains", boundsInset.contains(location1));
	}

	public void testInsetBoundsShrink() {
		LatLngBounds bounds = new LatLngBounds(new LatLng(-0.5, -0.5), new LatLng(0.5, 0.5));
		LatLng location0 = new LatLng(-0.45, 0.45);
		LatLng location1 = new LatLng(0.3, -0.25);
		assertTrue("Wrong contains", bounds.contains(location0));
		assertTrue("Wrong contains", bounds.contains(location1));

		LatLng center = bounds.getCenter();
		LatLngBounds boundsInset = MapClusterControllerUtils.expandBounds(bounds, -0.1, -0.1);
		assertEquals("Wrong center", center, boundsInset.getCenter());
		assertFalse("Wrong contains", boundsInset.contains(location0));
		assertTrue("Wrong contains", boundsInset.contains(location1));
	}

}
