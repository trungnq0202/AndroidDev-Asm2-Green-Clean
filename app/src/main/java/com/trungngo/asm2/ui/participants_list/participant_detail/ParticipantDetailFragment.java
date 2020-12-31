package com.trungngo.asm2.ui.participants_list.participant_detail;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trungngo.asm2.R;
import com.trungngo.asm2.model.Site;
import com.trungngo.asm2.model.User;
import com.trungngo.asm2.ui.participants_list.ParticipantsListViewModel;
import com.trungngo.asm2.utilities.DateStringParser;

import java.text.ParseException;

public class ParticipantDetailFragment extends Fragment {

    private ParticipantDetailViewModel mViewModel;

    //View variables
    private TextView profileUsername;
    private TextView profilePhone;
    private TextView profileBirthdate;
    private TextView profileGender;
    private TextView profileEmail;

    private User userObject;

    public static ParticipantDetailFragment newInstance() {
        return new ParticipantDetailFragment();
    }

    /**
     * Link all view elements to global variables
     * @param rootView
     */
    private void linkViewElements(View rootView) {
        profileUsername = rootView.findViewById(R.id.profileUsernameTextView);
        profilePhone = rootView.findViewById(R.id.profilePhoneTextView);
        profileBirthdate = rootView.findViewById(R.id.profileBirthdateTextView);
        profileGender = rootView.findViewById(R.id.profileGenderTextView);
        profileEmail = rootView.findViewById(R.id.profileEmailTextView);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_participant_detail, container, false);
        linkViewElements(view);
        return view;
    }

    /**
     * Fill user details to all textViews
     * @throws ParseException
     */
    private void setUserDetails() throws ParseException {
        profileUsername.setText(userObject.getUsername());
        profilePhone.setText(userObject.getPhone());
        profileBirthdate.setText(DateStringParser.parseFromDateObjectDDMMYYYY(userObject.getBirthDate()));
        profileGender.setText(userObject.getGender());
        profileEmail.setText(userObject.getEmail());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Use the ViewModel
        mViewModel = new ViewModelProvider(getActivity()).get(ParticipantDetailViewModel.class);
        mViewModel.getUserObject().observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                userObject = user;
                try {
                    setUserDetails();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });


    }

}