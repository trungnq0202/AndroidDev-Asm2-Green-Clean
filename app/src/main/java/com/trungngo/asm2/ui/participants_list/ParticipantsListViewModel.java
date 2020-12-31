package com.trungngo.asm2.ui.participants_list;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.trungngo.asm2.model.Site;
import com.trungngo.asm2.model.User;

public class ParticipantsListViewModel extends ViewModel {
    // Implement the ViewModel
    private final MutableLiveData<User> currentUserObject;
    private MutableLiveData<String> currentUserDocId;
    private MutableLiveData<Site> currentSite;

    public ParticipantsListViewModel() {
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