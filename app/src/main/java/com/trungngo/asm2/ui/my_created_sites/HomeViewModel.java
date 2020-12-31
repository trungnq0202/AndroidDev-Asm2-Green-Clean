package com.trungngo.asm2.ui.my_created_sites;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.trungngo.asm2.model.User;

public class HomeViewModel extends ViewModel {
    // Implement the ViewModel
    private MutableLiveData<User> currentUserObject;
    private MutableLiveData<String> currentUserDocId;

    public HomeViewModel(){
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