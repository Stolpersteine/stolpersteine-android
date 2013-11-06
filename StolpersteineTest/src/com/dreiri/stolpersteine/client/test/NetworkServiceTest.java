package com.dreiri.stolpersteine.client.test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.test.AndroidTestCase;

import com.dreiri.stolpersteine.api.NetworkService;
import com.dreiri.stolpersteine.api.NetworkService.Callback;
import com.dreiri.stolpersteine.api.model.Stolperstein;

public class NetworkServiceTest extends AndroidTestCase {
	private CountDownLatch doneLatch;
	private NetworkService networkService;
	
	public void setUp() {
		doneLatch = new CountDownLatch(1);
		networkService = new NetworkService();
	}
	
	public void testRetrieveStolpersteine() throws InterruptedException {
		networkService.retrieveStolpersteine(null, 0, 5, new Callback() {
			@Override
			public void handle(List<Stolperstein> stolpersteine) {
				assertEquals("Wrong number of stolpersteine", 5, stolpersteine.size());
				for (Stolperstein stolperstein: stolpersteine) {
					// Mandatory fields
					assertNotNull("Wrong ID", stolperstein.getId());
					assertTrue("Wrong type", 
							stolperstein.getType() == Stolperstein.Type.STOLPERSTEIN || 
							stolperstein.getType() == Stolperstein.Type.STOLPERSCHWELLE);
                                        assertNotNull("Wrong source", stolperstein.getSource());
					assertNotNull("Wrong source name", stolperstein.getSource().getName());
					assertNotNull("Wrong source URI", stolperstein.getSource().getUri());
					assertNotNull("Wrong person", stolperstein.getPerson());
					assertNotNull("Wrong person first name", stolperstein.getPerson().getFirstName());
					assertNotNull("Wrong person last name", stolperstein.getPerson().getLastName());
					assertNotNull("Wrong person biography URI", stolperstein.getPerson().getBiographyUri());
					assertNotNull("Wrong location street", stolperstein.getLocation().getStreet());
					assertNotNull("Wrong location city", stolperstein.getLocation().getCity());
					assertFalse("Wrong location latitude", stolperstein.getLocation().getCoordinates().latitude == 0.0);
					assertFalse("Wrong location longitude", stolperstein.getLocation().getCoordinates().longitude == 0.0);
					
					// Optional fields
					if (stolperstein.getLocation().getZipCode() != null) {
						assertTrue("Wrong location zip code", stolperstein.getLocation().getZipCode().length() > 0);
					}
				}
				
				doneLatch.countDown();
			}
		});

		assertTrue(doneLatch.await(15, TimeUnit.SECONDS));
	}
}
