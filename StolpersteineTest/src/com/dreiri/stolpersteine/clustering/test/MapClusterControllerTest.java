package com.dreiri.stolpersteine.clustering.test;

//import static org.mockito.Matchers.any;
//import static org.mockito.Mockito.spy;
//import static org.mockito.Mockito.doAnswer;

import org.mockito.MockitoAnnotations;

import android.test.AndroidTestCase;

public class MapClusterControllerTest extends AndroidTestCase {

	@Override
	protected void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	public void testGetItems() {
//        MapClusterController<Object> clusterController = spy(new MapClusterController<Object>(null));
//        doAnswer(new Answer<Void>() {
//            public Void answer(InvocationOnMock invocation) {
//                Object[] args = invocation.getArguments();
//
//                return null;
//            }
//        }).when(clusterController).addMarker(any(MapClusterController.ClusterMarker.class));
//        
//        clusterController.addMarker(new MapClusterController.ClusterMarker<Object>(null, null));
	    
//		MapFragment mapFragment = new MapFragment();
//		GoogleMap map1 = mapFragment.getMap();
//		MapView mapView = new MapView(getActivity());
//		GoogleMap map = mapView.getMap();
//		GoogleMap map = mock(GoogleMap.class);
//		when(map.addMarker(any(MarkerOptions.class))).thenAnswer(new Answer<Void>() {
//			public Void answer(InvocationOnMock invocation) {
//				Object[] args = invocation.getArguments();
//				
//				return null;
//			}
//		});
//
//		MapClusterController<Object> mapClusterController = new MapClusterController<Object>(map);
//		ArrayList<MarkerOptions> markerOptionsList = new ArrayList<MarkerOptions>();
//		markerOptionsList.add(new MarkerOptions().position(new LatLng(0, 0)).title("test"));
//		ArrayList<Object> items = new ArrayList<Object>();
//		items.add(new Object());
//		mapClusterController.addMarkers(markerOptionsList, items);

////		assertTrue("Wrong item", mapClusterController.getItems(marker).contains(items.get(0)));
	}

}
