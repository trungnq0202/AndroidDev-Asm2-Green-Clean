package com.trungngo.asm2.ui.participants_list.participant_detail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.trungngo.asm2.model.Site;
import com.trungngo.asm2.model.User;

public class ParticipantDetailViewModel extends ViewModel {
    // Implement the ViewModel
    private final MutableLiveData<User> userObject;

    public ParticipantDetailViewModel() {
        userObject = new MutableLiveData<>();
    }

    public void setData(User userObject) {
        this.userObject.setValue(userObject);
    }


    public MutableLiveData<User> getUserObject() {
        return this.userObject;
    }




}