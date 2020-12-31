package com.trungngo.asm2.model.GoogleMaps;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.maps.android.clustering.ClusterItem;
import com.trungngo.asm2.model.Site;

/**
 * ClusterItem class
 */
public class MyClusterItem implements ClusterItem {
    private final LatLng position;
    private final String title;
    private final String snippet;
    private final BitmapDescriptor iconBitMapDescriptor;
    private final Site site;

    public MyClusterItem(double lat, double lng, BitmapDescriptor iconBitMapDescriptor, Site site) {
        this.site = site;
        position = new LatLng(lat, lng);
        this.title = site.getSiteName();
        this.snippet = site.getSiteName();;
        this.iconBitMapDescriptor = iconBitMapDescriptor;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public BitmapDescriptor getIconBitMapDescriptor() {
        return iconBitMapDescriptor;
    }

    public Site getSite() {
        return site;
    }

}
