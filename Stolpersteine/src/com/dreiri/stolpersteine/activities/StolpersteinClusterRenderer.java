package com.dreiri.stolpersteine.activities;

import android.content.Context;

import com.dreiri.stolpersteine.api.model.Stolperstein;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class StolpersteinClusterRenderer extends DefaultClusterRenderer<Stolperstein> {
    public StolpersteinClusterRenderer(Context context, GoogleMap map, ClusterManager<Stolperstein> clusterManager) {
        super(context, map, clusterManager);
    }
    
    @Override
    @SuppressWarnings("rawtypes")
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return true;
    };

    @Override
    protected void onBeforeClusterItemRendered(Stolperstein stolperstein, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(stolperstein, markerOptions);
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<Stolperstein> cluster, MarkerOptions markerOptions) {
        super.onBeforeClusterRendered(cluster, markerOptions);
    }
}
