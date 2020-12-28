package com.trungngo.asm2.ui.find_sites;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.trungngo.asm2.model.User;

public class MapsViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<User> currentUserObject;
    private MutableLiveData<String> currentUserDocId;

    public MapsViewModel(){
        currentUserObject = new MutableLiveData<>();
        currentUserDocId = new MutableLiveData<>();
    }

    public void setData(User currentUserObject, String currentUserDocId){
        this.currentUserObject.setValue(currentUserObject);
        this.currentUserDocId.setValue(currentUserDocId);
    }

    public MutableLiveData<User> getCurrentUserObject(){
        return this.currentUserObject;
    }

    public MutableLiveData<String> getCurrentUserDocId(){
        return this.currentUserDocId;
    }
}