package com.trungngo.asm2.ui.my_participating_sites;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.trungngo.asm2.Constants;
import com.trungngo.asm2.R;
import com.trungngo.asm2.adapters.SiteAdapter;
import com.trungngo.asm2.model.Site;
import com.trungngo.asm2.model.User;
import com.trungngo.asm2.ui.edit_site.EditSiteViewModel;
import com.trungngo.asm2.ui.participants_list.ParticipantsListViewModel;
import com.trungngo.asm2.ui.site_detail.SiteDetailViewModel;

import java.util.ArrayList;
import java.util.List;

public class ParticipatingSitesFragment extends Fragment {

    private ParticipatingSitesViewModel mViewModel;
    private EditSiteViewModel editSiteViewModel;
    private ParticipantsListViewModel participantsListViewModel;
    private SiteDetailViewModel siteDetailViewModel;

    //Firebase, FireStore
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    User currentUserObject = null;
    String currentUserDocId = null;

    //View variables
    private List<Site> siteList;
    private RecyclerView siteInfoContainer;
    private TextView emptyMessageTextView;


    public static ParticipatingSitesFragment newInstance() {
        return new ParticipatingSitesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_participating_sites, container, false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        linkViewElements(view);
        return view;
    }

    private void linkViewElements(View rootView) {
        emptyMessageTextView = rootView.findViewById(R.id.empty_message);
        siteInfoContainer = rootView.findViewById(R.id.site_container);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        siteInfoContainer.setLayoutManager(linearLayoutManager);
    }


    private void loadSiteCells() {
        siteList = new ArrayList<>();

        if (currentUserObject.getParticipatingSitesId().isEmpty()) {
            emptyMessageTextView.setVisibility(View.VISIBLE);
        } else {
            emptyMessageTextView.setVisibility(View.GONE);
        }

        db.collection(Constants.FSSite.siteCollection)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        siteList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            if (currentUserObject.getParticipatingSitesId().contains(doc.getId())){
                                Site site = doc.toObject(Site.class);
                                siteList.add(site);
                            }
                        }
                        SiteAdapter siteAdapter = new SiteAdapter(getActivity(), siteList, false,
                                editSiteViewModel, participantsListViewModel, siteDetailViewModel);
                        siteInfoContainer.setAdapter(siteAdapter);
                    }
                });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
        mViewModel = ViewModelProviders.of(getActivity()).get(ParticipatingSitesViewModel.class);
        mViewModel.getCurrentUserObject().observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                currentUserObject = user;
                loadSiteCells();
            }
        });


        mViewModel.getCurrentUserDocId().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                currentUserDocId = s;
            }
        });

        //EditSiteViewModel
        editSiteViewModel = ViewModelProviders.of(getActivity()).get(EditSiteViewModel.class);
        participantsListViewModel = ViewModelProviders.of(getActivity()).get(ParticipantsListViewModel.class);
        siteDetailViewModel = ViewModelProviders.of(getActivity()).get(SiteDetailViewModel.class);

    }

}