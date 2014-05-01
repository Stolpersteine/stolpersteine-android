package com.option_u.stolpersteine.activities.map;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.option_u.stolpersteine.api.model.Stolperstein;

public class ClusterRenderer extends DefaultClusterRenderer<Stolperstein> {
    public ClusterRenderer(Context context, GoogleMap map, ClusterManager<Stolperstein> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<Stolperstein> cluster) {
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
