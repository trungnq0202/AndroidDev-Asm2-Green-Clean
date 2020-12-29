package com.trungngo.asm2.model;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class CustomClusterItemRendered extends DefaultClusterRenderer<MyClusterItem> {
    public CustomClusterItemRendered(Context context, GoogleMap map, ClusterManager<MyClusterItem> clusterManager) {
        super(context, map, clusterManager);
    }

//    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int )

    @Override
    protected void onBeforeClusterItemRendered(@NonNull MyClusterItem item, @NonNull MarkerOptions markerOptions) {

        super.onBeforeClusterItemRendered(item, markerOptions);
    }


}
