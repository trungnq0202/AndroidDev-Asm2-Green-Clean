package com.trungngo.asm2.ui.site_detail;

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
import com.trungngo.asm2.ui.participants_list.ParticipantsListViewModel;
import com.trungngo.asm2.utilities.DateStringParser;

import java.text.ParseException;

public class SiteDetailFragment extends Fragment {

    private SiteDetailViewModel mViewModel;

    //View variables
    private TextView siteDetailEventName;
    private TextView siteDetailLocation;
    private TextView siteDetailAddress;
    private TextView siteDetailStartDate;
    private TextView siteDetailEndDate;

    private Site siteObject;

    public static SiteDetailFragment newInstance() {
        return new SiteDetailFragment();
    }

    private void linkViewElements(View rootView) {
        siteDetailEventName = rootView.findViewById(R.id.siteDetailEventNameTextView);
        siteDetailLocation = rootView.findViewById(R.id.siteDetailLocationTextView);
        siteDetailAddress = rootView.findViewById(R.id.siteDetailAddressTextView);
        siteDetailStartDate = rootView.findViewById(R.id.siteDetailStartDateTextView);
        siteDetailEndDate = rootView.findViewById(R.id.siteDetailEndDateTextView);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_site_detail, container, false);
        linkViewElements(view);
        return view;
    }

    private void setSiteDetails() throws ParseException {
        siteDetailEventName.setText(siteObject.getSiteName());
        siteDetailLocation.setText(siteObject.getPlaceName());
        siteDetailAddress.setText(siteObject.getPlaceAddress());
        siteDetailStartDate.setText(DateStringParser.parseFromDateObjectDDMMYYYY(siteObject.getStartDate()));
        siteDetailEndDate.setText(DateStringParser.parseFromDateObjectDDMMYYYY(siteObject.getEndDate()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(SiteDetailViewModel.class);
        //Use the ViewModel

        mViewModel.getSiteObject().observe(getActivity(), new Observer<Site>() {
            @Override
            public void onChanged(Site site) {
                siteObject = site;
                System.out.println("SiteDetailFragment SiteDetailViewModel");
                System.out.println(site.getDocId());
                System.out.println(site.getSiteName());
                try {
                    setSiteDetails();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}