package com.dreiri.stolpersteine.api;

import java.util.List;

import com.dreiri.stolpersteine.api.NetworkService.Callback;
import com.dreiri.stolpersteine.api.model.Stolperstein;

public class SynchronizationController {
	final static int NETWORK_BATCH_SIZE = 500;
	
    private NetworkService client = new NetworkService();
    
    public void retrieveStolpersteine(Callback callback) {
    	retrieveStolpersteine(0, NETWORK_BATCH_SIZE, callback);
    }

    private void retrieveStolpersteine(final int offset, final int limit, final Callback callback) {
    	client.retrieveStolpersteine(null, offset, limit, new Callback() {
        	@Override
            public void handle(List<Stolperstein> stolpersteine) {
        	    callback.handle(stolpersteine);
            	if (stolpersteine.size() == NETWORK_BATCH_SIZE) {
            		retrieveStolpersteine(offset + NETWORK_BATCH_SIZE, NETWORK_BATCH_SIZE, callback);
            	}
            }
        });
    }   
}