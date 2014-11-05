package com.option_u.stolpersteine.api;

import java.util.List;

import com.option_u.stolpersteine.api.model.Stolperstein;

public class SynchronizationController {
    final static int NETWORK_BATCH_SIZE = 500;
    private StolpersteineNetworkService networkService;
    private Listener listener;

    public SynchronizationController(StolpersteineNetworkService networkService) {
        this.networkService = networkService;
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public StolpersteineNetworkService getNetworkService() {
        return networkService;
    }

    public void synchronize() {
        retrieveStolpersteine(0);
    }

    private void retrieveStolpersteine(final int offset) {
        networkService.retrieveStolpersteine(null, offset, NETWORK_BATCH_SIZE, new RetrieveStolpersteineRequest.Callback() {

            @Override
            public void onStolpersteineRetrieved(List<Stolperstein> stolpersteine) {
                if (listener != null) {
                    listener.onStolpersteineAdded(stolpersteine);
                }

                if (stolpersteine != null && stolpersteine.size() == NETWORK_BATCH_SIZE) {
                    retrieveStolpersteine(offset + NETWORK_BATCH_SIZE);
                }
            }
        });
    }

    public interface Listener {
        public void onStolpersteineAdded(List<Stolperstein> stolpersteine);
    }
}