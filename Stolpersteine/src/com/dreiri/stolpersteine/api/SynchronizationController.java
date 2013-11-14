package com.dreiri.stolpersteine.api;

import java.util.List;

import android.util.Log;

import com.dreiri.stolpersteine.api.model.Stolperstein;

public class SynchronizationController {
	final static int NETWORK_BATCH_SIZE = 500;
    private NetworkService networkService;
    private Listener listener;
    
    public SynchronizationController(NetworkService networkService) {
        this.networkService = networkService;
    }
    
    public Listener getListener() {
	    return listener;
    }

	public void setListener(Listener listener) {
	    this.listener = listener;
    }

	public void synchronize() {
    	retrieveStolpersteine(0, NETWORK_BATCH_SIZE);
    }

    private void retrieveStolpersteine(final int offset, final int limit) {
        Log.i("Stolpersteine", "Started request " + offset + " " + limit);
        networkService.retrieveStolpersteine(null, offset, limit, new RetrieveStolpersteineRequest2.Callback() {

            @Override
            public void onStolpersteineRetrieved(List<Stolperstein> stolpersteine) {
                if (listener != null) {
                    listener.onStolpersteineAdded(stolpersteine);
                }
                
//                Log.i("Stolpersteine", "Received " + stolpersteine);

                if (stolpersteine != null && stolpersteine.size() == NETWORK_BATCH_SIZE) {
                    retrieveStolpersteine(offset + NETWORK_BATCH_SIZE, NETWORK_BATCH_SIZE);
                }
            }
        });
    }
    
    public interface Listener {
    	public void onStolpersteineAdded(List<Stolperstein> stolpersteine);
    }
}