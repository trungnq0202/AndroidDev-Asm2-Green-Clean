package com.trungngo.asm2.ui.site_detail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.trungngo.asm2.model.Site;

public class SiteDetailViewModel extends ViewModel {
    // Implement the ViewModel
    private final MutableLiveData<Site> siteObject;

    public SiteDetailViewModel() {
        siteObject = new MutableLiveData<>();
    }

    public void setData(Site siteObject) {
        this.siteObject.setValue(siteObject);
    }


    public MutableLiveData<Site> getSiteObject() {
        return this.siteObject;
    }
}