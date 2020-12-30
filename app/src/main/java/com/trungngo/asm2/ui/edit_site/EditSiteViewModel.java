package com.trungngo.asm2.ui.edit_site;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.trungngo.asm2.model.Site;
import com.trungngo.asm2.model.User;

public class EditSiteViewModel extends ViewModel {
    // Implement the ViewModel
    private MutableLiveData<User> currentUserObject;
    private MutableLiveData<String> currentUserDocId;
    private MutableLiveData<Site> currentSite;

    public EditSiteViewModel() {
        currentUserObject = new MutableLiveData<>();
        currentUserDocId = new MutableLiveData<>();
        currentSite = new MutableLiveData<>();
    }

    public void setData(User currentUserObject, String currentUserDocId) {
        this.currentUserObject.setValue(currentUserObject);
        this.currentUserDocId.setValue(currentUserDocId);
    }

    public void setCurrentSiteData(Site currentSite) {
        this.currentSite.setValue(currentSite);
    }

    public MutableLiveData<User> getCurrentUserObject() {
        return this.currentUserObject;
    }

    public MutableLiveData<String> getCurrentUserDocId() {
        return this.currentUserDocId;
    }

    public MutableLiveData<Site> getCurrentSite() {
        return this.currentSite;
    }
}