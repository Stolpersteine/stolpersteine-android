package com.dreiri.stolpersteine.client.test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.test.AndroidTestCase;

import com.dreiri.stolpersteine.client.Callback;
import com.dreiri.stolpersteine.client.StolpersteineClient;
import com.dreiri.stolpersteine.models.Stolperstein;
import com.google.android.gms.maps.model.LatLng;

public class StolpersteinNetworkServiceTest extends AndroidTestCase {
	private CountDownLatch doneLatch = new CountDownLatch(1);
	
	public void testRetrieveStolpersteine() throws InterruptedException {
		LatLng latLong = new LatLng(0, 0);
		latLong.describeContents();
		StolpersteineClient client = new StolpersteineClient();
		
		client.retrieveRangeOfResultsAndHandleThem(0, 5, new Callback() {

			@Override
			public void handle(List<Stolperstein> stolpersteine) {
				doneLatch.countDown();
			}
		});

		assertTrue(doneLatch.await(5, TimeUnit.SECONDS));
	}
}
