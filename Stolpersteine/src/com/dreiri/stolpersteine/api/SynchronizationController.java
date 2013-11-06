package com.dreiri.stolpersteine.api;

import java.util.List;

import com.dreiri.stolpersteine.api.model.Stolperstein;


public class SynchronizationController {
	final static int NETWORK_BATCH_SIZE = 500;
	
    private NetworkService client;
    
    public SynchronizationController() {
        this.client = new NetworkService();
    }
    
    public void retrieveAllStolpersteine(Callback callback) {
    	retrieveStolpersteineSync(0, NETWORK_BATCH_SIZE, callback);
    }

    public void retrieveStolpersteineSync(final int offset, final int limit, final Callback callback) {
    	client.retrieveStolpersteine(null, offset, limit, new Callback() {
        	@Override
            public void handle(List<Stolperstein> stolpersteine) {
        	    callback.handle(stolpersteine);
            	if (stolpersteine.size() == NETWORK_BATCH_SIZE) {
            		retrieveStolpersteineSync(offset + NETWORK_BATCH_SIZE, NETWORK_BATCH_SIZE, callback);
            	}
            }
        });
    }   
}