package com.dreiri.stolpersteine.api.test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.test.AndroidTestCase;

import com.dreiri.stolpersteine.api.NetworkService;
import com.dreiri.stolpersteine.api.RetrieveStolpersteineRequest.Callback;
import com.dreiri.stolpersteine.api.SearchData;
import com.dreiri.stolpersteine.api.model.Stolperstein;

public class NetworkServiceTest extends AndroidTestCase {
	private static int TIME_OUT = 5;
	private CountDownLatch doneLatch;
	private NetworkService networkService;

	public void setUp() {
		doneLatch = new CountDownLatch(1);
		networkService = new NetworkService(getContext());
	}
	
	public void testRetrieveStolpersteine() throws InterruptedException {
		networkService.retrieveStolpersteine(null, 0, 5, new Callback() {
			@Override
			public void onStolpersteineRetrieved(List<Stolperstein> stolpersteine) {
				assertEquals("Wrong number of stolpersteine", 5, stolpersteine.size());
				for (Stolperstein stolperstein : stolpersteine) {
					// Mandatory fields
					assertNotNull("Wrong ID", stolperstein.getId());
					assertTrue("Wrong type", stolperstein.getType() == Stolperstein.Type.STOLPERSTEIN
					        || stolperstein.getType() == Stolperstein.Type.STOLPERSCHWELLE);
					assertNotNull("Wrong source", stolperstein.getSource());
					assertNotNull("Wrong source name", stolperstein.getSource().getName());
					assertNotNull("Wrong source URI", stolperstein.getSource().getUri());
					assertNotNull("Wrong person", stolperstein.getPerson());
					assertNotNull("Wrong person last name", stolperstein.getPerson().getLastName());
					assertNotNull("Wrong person biography URI", stolperstein.getPerson().getBiographyUri());
					assertNotNull("Wrong location street", stolperstein.getLocation().getStreet());
					assertNotNull("Wrong location city", stolperstein.getLocation().getCity());
					assertFalse("Wrong location latitude", stolperstein.getLocation().getCoordinates().latitude == 0.0);
					assertFalse("Wrong location longitude", stolperstein.getLocation().getCoordinates().longitude == 0.0);

					// Optional fields
					if (!stolperstein.getPerson().getFirstName().isEmpty()) {
						assertTrue("Wrong location first name", stolperstein.getPerson().getFirstName().length() > 0);
					}

					if (!stolperstein.getLocation().getZipCode().isEmpty()) {
						assertTrue("Wrong location zip code", stolperstein.getLocation().getZipCode().length() > 0);
					}
				}

				doneLatch.countDown();
			}
		});

		assertTrue(doneLatch.await(TIME_OUT, TimeUnit.SECONDS));
	}

	public void testRetrieveStolpersteineKeyword() throws InterruptedException {
		final SearchData searchData = new SearchData();
		searchData.setKeyword("Ern");

		networkService.retrieveStolpersteine(searchData, 0, 5, new Callback() {
			@Override
			public void onStolpersteineRetrieved(List<Stolperstein> stolpersteine) {
				assertTrue(stolpersteine.size() > 0);
				for (Stolperstein stolperstein : stolpersteine) {
					boolean found = stolperstein.getPerson().getFirstName().startsWith(searchData.getKeyword());
					found |= stolperstein.getPerson().getLastName().startsWith(searchData.getKeyword());

					assertTrue("Wrong search result", found);
				}

				doneLatch.countDown();
			}
		});

		assertTrue(doneLatch.await(TIME_OUT, TimeUnit.SECONDS));
	}

	public void testRetrieveStolpersteineStreet() throws InterruptedException {
		final SearchData searchData = new SearchData();
		searchData.setStreet("Turmstraße");

		networkService.retrieveStolpersteine(searchData, 0, 5, new Callback() {
			@Override
			public void onStolpersteineRetrieved(List<Stolperstein> stolpersteine) {
				assertTrue(stolpersteine.size() > 0);
				for (Stolperstein stolperstein : stolpersteine) {
					boolean found = stolperstein.getLocation().getStreet().startsWith(searchData.getStreet());

					assertTrue("Wrong search result", found);
				}

				doneLatch.countDown();
			}
		});

		assertTrue(doneLatch.await(TIME_OUT, TimeUnit.SECONDS));
	}

	public void testRetrieveStolpersteineCity() throws InterruptedException {
		final SearchData searchData = new SearchData();
		searchData.setCity("Berlin");

		networkService.retrieveStolpersteine(searchData, 0, 5, new Callback() {
			@Override
			public void onStolpersteineRetrieved(List<Stolperstein> stolpersteine) {
				assertTrue(stolpersteine.size() > 0);
				for (Stolperstein stolperstein : stolpersteine) {
					boolean found = stolperstein.getLocation().getCity().startsWith(searchData.getCity());

					assertTrue("Wrong search result", found);
				}

				doneLatch.countDown();
			}
		});

		assertTrue(doneLatch.await(TIME_OUT, TimeUnit.SECONDS));
	}

	public void testRetrieveStolpersteineCityInvalid() throws InterruptedException {
		networkService.getDefaultSearchData().setCity("Berlin"); // will be overridden by specific data
		final SearchData searchData = new SearchData();
		searchData.setCity("xyz");

		networkService.retrieveStolpersteine(searchData, 0, 5, new Callback() {
			@Override
			public void onStolpersteineRetrieved(List<Stolperstein> stolpersteine) {
				assertEquals(0, stolpersteine.size());

				doneLatch.countDown();
			}
		});

		assertTrue(doneLatch.await(TIME_OUT, TimeUnit.SECONDS));
	}

	public void testRetrieveStolpersteineCityDefaultInvalid() throws InterruptedException {
		networkService.getDefaultSearchData().setCity("xyz"); // will be overridden by specific data

		networkService.retrieveStolpersteine(null, 0, 5, new Callback() {
			@Override
			public void onStolpersteineRetrieved(List<Stolperstein> stolpersteine) {
				assertEquals(0, stolpersteine.size());

				doneLatch.countDown();
			}
		});

		assertTrue(doneLatch.await(TIME_OUT, TimeUnit.SECONDS));
	}

	public void testRetrieveStolpersteinePaging() throws InterruptedException {
		// Load first two stolpersteine
		networkService.retrieveStolpersteine(null, 0, 2, new Callback() {
			@Override
			public void onStolpersteineRetrieved(List<Stolperstein> stolpersteine) {
				assertEquals(2, stolpersteine.size());
				final String stolpersteinId0 = stolpersteine.get(0).getId();
				final String stolpersteinId1 = stolpersteine.get(1).getId();

				// First page
				networkService.retrieveStolpersteine(null, 0, 1, new Callback() {
					@Override
					public void onStolpersteineRetrieved(List<Stolperstein> stolpersteine) {
						assertEquals(1, stolpersteine.size());
						assertEquals(stolpersteinId0, stolpersteine.get(0).getId());

						// Second page
						networkService.retrieveStolpersteine(null, 1, 1, new Callback() {
							@Override
							public void onStolpersteineRetrieved(List<Stolperstein> stolpersteine) {
								assertEquals(1, stolpersteine.size());
								assertEquals(stolpersteinId1, stolpersteine.get(0).getId());

								doneLatch.countDown();
							}
						});
					}
				});
			}
		});
		assertTrue(doneLatch.await(TIME_OUT, TimeUnit.SECONDS));
	}
}
