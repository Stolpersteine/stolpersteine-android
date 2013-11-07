package com.dreiri.stolpersteine.api;

import java.util.List;

import com.dreiri.stolpersteine.api.NetworkService.Callback;
import com.dreiri.stolpersteine.api.model.Stolperstein;

public class SynchronizationController {
	final static int NETWORK_BATCH_SIZE = 500;
    private NetworkService networkService;
    
    public SynchronizationController(NetworkService networkService) {
        this.networkService = networkService;
    }
    
    public void retrieveStolpersteine(Callback callback) {
    	retrieveStolpersteine(0, NETWORK_BATCH_SIZE, callback);
    }

    private void retrieveStolpersteine(final int offset, final int limit, final Callback callback) {
        networkService.retrieveStolpersteine(null, offset, limit, new Callback() {
        	@Override
            public void onStolpersteineRetrieved(List<Stolperstein> stolpersteine) {
        	    callback.onStolpersteineRetrieved(stolpersteine);
            	if (stolpersteine.size() == NETWORK_BATCH_SIZE) {
            		retrieveStolpersteine(offset + NETWORK_BATCH_SIZE, NETWORK_BATCH_SIZE, callback);
            	}
            }
        });
    }   
}